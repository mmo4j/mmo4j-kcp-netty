package com.mmo4j.kcp.netty

import com.mmo4j.kcp.netty.internal.CodecOutputList
import io.jpower.kcp.netty.UkcpClientChannel
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelConfig
import io.netty.channel.ChannelException
import io.netty.channel.ChannelMetadata
import io.netty.channel.ChannelOutboundBuffer
import io.netty.channel.nio.AbstractNioMessageChannel
import io.netty.util.ReferenceCountUtil
import io.netty.util.internal.PlatformDependent
import io.netty.util.internal.SocketUtils
import io.netty.util.internal.StringUtil
import io.netty.util.internal.logging.InternalLoggerFactory
import java.io.IOException
import java.net.SocketAddress
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.spi.SelectorProvider
import java.util.ArrayList

internal class UkcpClientUdpChannel @JvmOverloads constructor(private val ukcpChannel: UkcpClientChannel, socket: DatagramChannel = newSocket(DEFAULT_SELECTOR_PROVIDER)) : AbstractNioMessageChannel(null, socket, SelectionKey.OP_READ) {
  var inputShutdown = false

  constructor(ukcpChannel: UkcpClientChannel, provider: SelectorProvider) : this(ukcpChannel, newSocket(provider))

  override fun metadata(): ChannelMetadata {
    return METADATA
  }

  override fun config(): ChannelConfig {
    return ukcpChannel.config()
  }

  private override fun newUnsafe(): UkcpClientUdpUnsafe {
    return UkcpClientUdpUnsafe()
  }

  override fun isActive(): Boolean {
    val ch = javaChannel()
    return ch.isOpen && ch.socket().isBound
  }

  override fun javaChannel(): DatagramChannel {
    return super.javaChannel() as DatagramChannel
  }

  override fun localAddress0(): SocketAddress {
    return javaChannel().socket().localSocketAddress
  }

  override fun remoteAddress0(): SocketAddress {
    return javaChannel().socket().remoteSocketAddress
  }

  @Throws(Exception::class)
  override fun doBind(localAddress: SocketAddress) {
    doBind0(localAddress)
  }

  @Throws(Exception::class)
  private fun doBind0(localAddress: SocketAddress) {
    if (PlatformDependent.javaVersion() >= 7) {
      SocketUtils.bind(javaChannel(), localAddress)
    } else {
      javaChannel().socket().bind(localAddress)
    }
  }

  @Throws(Exception::class)
  override fun doConnect(remoteAddress: SocketAddress, localAddress: SocketAddress): Boolean {
    localAddress.let { doBind0(it) }
    var success = false
    return try {
      javaChannel().connect(remoteAddress)
      success = true
      val current = System.currentTimeMillis() // schedule update
      val tsUp = ukcpChannel.kcpCheck(current)
      ukcpChannel.kcpTsUpdate(tsUp)
      ukcpChannel.scheduleUpdate(tsUp, current)
      true
    } finally {
      if (!success) {
        doClose()
      }
    }
  }

  @Throws(Exception::class)
  override fun doFinishConnect() {
    throw Error()
  }

  @Throws(Exception::class)
  override fun doDisconnect() {
    doClose()
  }

  @Throws(Exception::class)
  override fun doClose() {
    javaChannel().close()
    if (!ukcpChannel.closeAnother) {
      ukcpChannel.closeAnother = true
      ukcpChannel.unsafe().close(ukcpChannel.unsafe().voidPromise())
    }
  }

  @Throws(Exception::class)
  override fun doBeginRead() {
    if (inputShutdown) {
      return
    }
    super.doBeginRead()
  }

  @Throws(Exception::class)
  override fun doReadMessages(buf: MutableList<Any>): Int {
    val ch = javaChannel()
    val config = config()
    val allocHandle = unsafe().recvBufAllocHandle()
    val data = allocHandle.allocate(config.allocator)
    allocHandle.attemptedBytesRead(data.writableBytes())
    var free = true
    return try {
      val nioData = data.internalNioBuffer(data.writerIndex(), data.writableBytes())
      val pos = nioData.position()
      val read = ch.read(nioData)
      if (read <= 0) {
        return read
      }
      allocHandle.lastBytesRead(nioData.position() - pos)
      buf.add(data.writerIndex(data.writerIndex() + allocHandle.lastBytesRead()))
      free = false
      1
    } catch (cause: Throwable) {
      PlatformDependent.throwException(cause)
      -1
    } finally {
      if (free) {
        data.release()
      }
    }
  }

  @Throws(Exception::class)
  override fun doWriteMessage(msg: Any, `in`: ChannelOutboundBuffer): Boolean {
    val data = msg as ByteBuf
    val dataLen = data.readableBytes()
    if (dataLen == 0) {
      return true
    }
    val nioData = data.internalNioBuffer(data.readerIndex(), dataLen)
    val writtenBytes: Int
    writtenBytes = javaChannel().write(nioData)
    return writtenBytes > 0
  }

  override fun filterOutboundMessage(msg: Any): Any {
    if (msg is ByteBuf) {
      val buf = msg
      return if (isSingleDirectBuffer(buf)) {
        buf
      } else newDirectBuffer(buf)
    }
    throw UnsupportedOperationException(
      "unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES)
  }

  override fun continueOnWriteError(): Boolean { // Continue on write error as a DatagramChannel can write to multiple remote peers
//
// See https://github.com/netty/netty/issues/2665
    return true
  }

  private inner class UkcpClientUdpUnsafe : AbstractNioUnsafe() {
    private val readBuf: MutableList<Any> = ArrayList()
    override fun read() {
      assert(eventLoop().inEventLoop())
      val config = config()
      val pipeline = pipeline()
      val ukcpPipeline = ukcpChannel.pipeline()
      val allocHandle = recvBufAllocHandle()
      allocHandle.reset(config)
      var closed = false
      var exception: Throwable? = null
      try {
        try {
          do {
            val localRead = doReadMessages(readBuf)
            if (localRead == 0) {
              break
            }
            if (localRead < 0) {
              closed = true
              break
            }
            allocHandle.incMessagesRead(localRead)
          } while (allocHandle.continueReading())
        } catch (t: Throwable) {
          exception = t
        }
        var exception1: Throwable? = null
        val readBufSize = readBuf.size
        try {
          for (o in readBuf) {
            val byteBuf = o as ByteBuf
            ukcpChannel.kcpInput(byteBuf)
          }
          if (readBufSize > 0) {
            ukcpChannel.kcpTsUpdate(-1) // update kcp
          }
        } catch (t: Throwable) {
          exception1 = t
        }
        if (exception1 == null) {
          val mergeSegmentBuf = ukcpChannel.config().isMergeSegmentBuf
          var recvBufList: CodecOutputList<ByteBuf?>? = null
          var recv = false
          try {
            if (mergeSegmentBuf) {
              val allocator = config.allocator
              var peekSize: Int
              while (ukcpChannel.kcpPeekSize().also { peekSize = it } >= 0) {
                recv = true
                val recvBuf = allocator.ioBuffer(peekSize)
                ukcpChannel.kcpReceive(recvBuf)
                ukcpPipeline.fireChannelRead(recvBuf)
              }
            } else {
              while (ukcpChannel.kcpCanRecv()) {
                recv = true
                if (recvBufList == null) {
                  recvBufList = CodecOutputList.newInstance()
                }
                ukcpChannel.kcpReceive(recvBufList)
              }
            }
          } catch (t: Throwable) {
            exception1 = t
          }
          if (recv) {
            if (mergeSegmentBuf) {
              ukcpPipeline.fireChannelReadComplete()
            } else {
              Utils.fireChannelRead(ukcpChannel, recvBufList)
              recvBufList.recycle()
            }
          }
        }
        clearAndReleaseReadBuf()
        allocHandle.readComplete()
        if (exception != null) {
          closed = closeOnReadError(exception)
          ukcpPipeline.fireExceptionCaught(exception)
        }
        if (exception1 != null) {
          closed = true
          ukcpPipeline.fireExceptionCaught(exception1)
        }
        if (closed) {
          inputShutdown = true
          if (isOpen) {
            close(voidPromise())
          }
        }
      } finally { // Check if there is a readPending which was not processed yet.
// This could be for two reasons:
// * The user called Channel.read() or ChannelHandlerContext.read() in channelRead(...) method
// * The user called Channel.read() or ChannelHandlerContext.read() in channelReadComplete(...) method
//
// See https://github.com/netty/netty/issues/2254
        if (!config.isAutoRead) {
          removeReadOp()
        }
      }
    }

    private fun clearAndReleaseReadBuf() {
      val size = readBuf.size
      for (msg in readBuf) {
        ReferenceCountUtil.release(msg)
      }
      readBuf.clear()
    }
  }

  companion object {
    private val log = InternalLoggerFactory.getInstance(UkcpClientUdpChannel::class.java)
    private val METADATA = ChannelMetadata(false)
    private val DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider()
    private val EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf::class.java) + ')'
    private fun newSocket(provider: SelectorProvider): DatagramChannel {
      return try {
        /**
         * Use the [SelectorProvider] to open [SocketChannel] and so remove condition in
         * [SelectorProvider.provider] which is called by each DatagramChannel.open() otherwise.
         *
         * See [#2308](https://github.com/netty/netty/issues/2308).
         */
        provider.openDatagramChannel()
      } catch (e: IOException) {
        throw ChannelException("Failed to open a socket.", e)
      }
    }

    /**
     * Checks if the specified buffer is a direct buffer and is composed of a single NIO buffer.
     * (We check this because otherwise we need to make it a non-composite buffer.)
     */
    private fun isSingleDirectBuffer(buf: ByteBuf): Boolean {
      return buf.isDirect && buf.nioBufferCount() == 1
    }
  }

}
