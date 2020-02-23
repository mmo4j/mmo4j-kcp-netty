package com.mmo4j.kcp.netty

import io.jpower.kcp.netty.Kcp
import io.jpower.kcp.netty.KcpOutput
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.Channel
import io.netty.util.internal.logging.InternalLoggerFactory
import java.io.IOException
import kotlin.math.max

class Ukcp(conv: Int, output: KcpOutput) {
  private val kcp: Kcp = Kcp(conv, output)

  var fastFlush = true
    private set
  var mergeSegmentBuf = true
    private set
  var tsUpdate: Long = -1
    private set
  @Volatile
  var active: Boolean = true
    private set

  /**
   * Receives ByteBufs.
   */
  fun receive(buf: ByteBuf) {
    if (kcp.recv(buf) == -3) {
      throw IOException("Received Data exceeds maxCapacity of buf")
    }
  }

  /**
   * Receives ByteBufs.
   *
   * @param bufList received ByteBuf will be add to the list
   */
  fun receive(bufList: List<ByteBuf>) {
    kcp.recv(bufList)
  }

  fun input(data: ByteBuf) {
    when (kcp.input(data)) {
      -1 -> throw IOException("Not enough bytes of head")
      -2 -> throw IOException("Not enough bytes of data")
      -3 -> throw IOException("Mismatch cmd")
      -4 -> throw IOException("Conv inconsistency")
      else -> Unit
    }
  }

  /**
   * Sends a Bytebuf.
   */
  fun send(buf: ByteBuf) {
    if (kcp.send(buf) == -2) {
      throw IOException("Too many fragments")
    }
  }

  /**
   * The size of the first msg of the kcp.
   *
   * @return The size of the first msg of the kcp, or -1 if none of msg
   */
  fun peekSize(): Int = kcp.peekSize()

  /**
   * Returns `true` if there are bytes can be received.
   */
  fun canRecv(): Boolean = kcp.canRecv()

  /**
   * Returns `true` if the kcp can send more bytes.
   *
   * @param curCanSend last state of canSend
   * @return `true` if the kcp can send more bytes
   */
  fun canSend(curCanSend: Boolean): Boolean {
    val max = kcp.sndWnd * 2
    val waitSnd = kcp.waitSnd()

    return if (curCanSend) {
      waitSnd < max
    } else {
      waitSnd < max(1, max / 2)
    }
  }

  /**
   * Updates the kcp.
   *
   * @param current current time in milliseconds
   * @return the next time to update
   */
  fun update(current: Long): Long {
    kcp.update(current)
    val nextTsUp = check(current)
    setTsUpdate(nextTsUp)
    return nextTsUp
  }

  /**
   * Determines when should you invoke udpate.
   *
   * @param current current time in milliseconds
   * @see Kcp.check
   */
  fun check(current: Long): Long= kcp.check(current)

  /**
   * Returns `true` if the kcp need to flush.
   *
   * @return `true` if the kcp need to flush
   */
  fun checkFlush(): Boolean= kcp.checkFlush()

  /**
   * Sets params of nodelay.
   *
   * @param nodelay  `true` if nodelay mode is enabled
   * @param interval protocol internal work interval, in milliseconds
   * @param resend   fast retransmission mode, 0 represents off by default, 2 can be set (2 ACK spans will result
   * in direct retransmission)
   * @param nc       `true` if turn off flow control
   */
  fun nodelay(nodelay: Boolean, interval: Int, resend: Int, nc: Boolean) {
    kcp.nodelay(nodelay, interval, resend, nc)
  }

  /**
   * Returns conv of kcp.
   *
   * @return conv of kcp
   */
  /**
   * Set the conv of kcp.
   *
   * @param conv the conv of kcp
   */
  var conv: Int
    get() = kcp.conv
    set(conv) {
      kcp.conv = conv
    }

  /**
   * Returns `true` if and only if nodelay is enabled.
   *
   * @return `true` if and only if nodelay is enabled
   */
  val isNodelay: Boolean
    get() = kcp.isNodelay

  /**
   * Sets whether enable nodelay.
   *
   * @param nodelay `true` if enable nodelay
   * @return this object
   */
  fun setNodelay(nodelay: Boolean): Ukcp {
    kcp.isNodelay = nodelay
    return this
  }

  /**
   * Returns update interval.
   *
   * @return update interval
   */
  val interval: Int
    get() = kcp.interval

  /**
   * Sets update interval
   *
   * @param interval update interval
   * @return this object
   */
  fun setInterval(interval: Int): Ukcp {
    kcp.interval = interval
    return this
  }

  /**
   * Returns the fastresend of kcp.
   *
   * @return the fastresend of kcp
   */
  val fastResend: Int
    get() = kcp.fastresend

  /**
   * Sets the fastresend of kcp.
   *
   * @return this object
   */
  fun setFastResend(fastResend: Int): Ukcp {
    kcp.fastresend = fastResend
    return this
  }

  val fastLimit: Int
    get() = kcp.fastlimit

  fun setFastLimit(fastLimit: Int): Ukcp {
    kcp.fastlimit = fastLimit
    return this
  }

  val isNocwnd: Boolean
    get() = kcp.isNocwnd

  fun setNocwnd(nocwnd: Boolean): Ukcp {
    kcp.isNocwnd = nocwnd
    return this
  }

  val minRto: Int
    get() = kcp.rxMinrto

  fun setMinRto(minRto: Int): Ukcp {
    kcp.rxMinrto = minRto
    return this
  }

  val mtu: Int
    get() = kcp.mtu

  fun setMtu(mtu: Int): Ukcp {
    kcp.mtu = mtu
    return this
  }

  val isStream: Boolean
    get() = kcp.isStream

  fun setStream(stream: Boolean): Ukcp {
    kcp.isStream = stream
    return this
  }

  val deadLink: Int
    get() = kcp.deadLink

  fun setDeadLink(deadLink: Int): Ukcp {
    kcp.deadLink = deadLink
    return this
  }

  /**
   * Sets the [ByteBufAllocator] which is used for the kcp to allocate buffers.
   *
   * @param allocator the allocator is used for the kcp to allocate buffers
   * @return this object
   */
  fun setByteBufAllocator(allocator: ByteBufAllocator): Ukcp {
    kcp.setByteBufAllocator(allocator)
    return this
  }

  val isAutoSetConv: Boolean
    get() = kcp.isAutoSetConv

  fun setAutoSetConv(autoSetConv: Boolean): Ukcp {
    kcp.isAutoSetConv = autoSetConv
    return this
  }

  fun wndSize(sndWnd: Int, rcvWnd: Int): Ukcp {
    kcp.wndsize(sndWnd, rcvWnd)
    return this
  }

  fun waitSnd(): Int {
    return kcp.waitSnd()
  }

  val rcvWnd: Int
    get() = kcp.rcvWnd

  fun setRcvWnd(rcvWnd: Int): Ukcp {
    kcp.rcvWnd = rcvWnd
    return this
  }

  val sndWnd: Int
    get() = kcp.sndWnd

  fun setSndWnd(sndWnd: Int): Ukcp {
    kcp.sndWnd = sndWnd
    return this
  }

  fun setFastFlush(fastFlush: Boolean): Ukcp {
    this.fastFlush = fastFlush
    return this
  }

  fun setMergeSegmentBuf(mergeSegmentBuf: Boolean): Ukcp {
    this.mergeSegmentBuf = mergeSegmentBuf
    return this
  }

  fun setTsUpdate(tsUpdate: Long): Ukcp {
    this.tsUpdate = tsUpdate
    return this
  }

  val state: Int
    get() = kcp.state

  fun setClosed(closeKcp: Boolean) {
    active = false
    if (closeKcp) {
      setKcpClosed()
    }
  }

  fun setKcpClosed() {
    kcp.state = -1
    kcp.release()
    kcp.logMonitor()
  }

  fun <T : Channel> channel(): T {
    return kcp.user as T
  }

  fun channel(channel: Channel): Ukcp {
    kcp.user = channel
    return this
  }

  override fun toString(): String {
    return "Ukcp(" +
      "getConv=" + kcp.conv +
      ", state=" + kcp.state +
      ", active=" + active +
      ')'
  }

  companion object {
    private val log = InternalLoggerFactory.getInstance(Ukcp::class.java)
  }
}

fun test() {
  val limit = 500
  val isEven = { x: Int -> x % 2 == 0 }
  val sum = generateSequence(0) { it + 1}
    .map { it * it }
    .takeWhile { it < limit }
    .filter { isEven(it) }
    .fold(0) { sum, x -> sum + x }
}
