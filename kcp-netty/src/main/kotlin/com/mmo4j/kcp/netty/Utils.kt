package com.mmo4j.kcp.netty

import com.mmo4j.kcp.netty.internal.CodecOutputList
import io.netty.buffer.ByteBuf

object Utils {
  fun fireExceptionAndClose(channel: UkcpChannel, t: Throwable, close: Boolean) {
    channel.pipeline().fireExceptionCaught(t)

    if (channel.isActive) {
      val unsafe = channel.unsafe()
      unsafe.close(unsafe.voidPromise())
    }
  }

  fun fireChannelRead(channel: UkcpChannel, bufList: CodecOutputList<ByteBuf>) {
    val pipeline = channel.pipeline()
    val size = bufList.size

    if (size <= 0) {
      return
    }

    for (i in 0..size) {
      val msg = bufList.getUnsafe(i)
      pipeline.fireChannelRead(msg)
    }

    pipeline.fireChannelReadComplete()
  }
}
