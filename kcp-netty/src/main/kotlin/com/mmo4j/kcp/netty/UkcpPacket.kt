package com.mmo4j.kcp.netty

import io.netty.buffer.ByteBuf
import io.netty.util.AbstractReferenceCounted
import io.netty.util.Recycler
import io.netty.util.ReferenceCounted
import java.net.InetSocketAddress

class UkcpPacket(
  private var recycleHandler: Recycler.Handle<UkcpPacket>,
  private var content: ByteBuf? = null,
  private var remoteAddress: InetSocketAddress? = null
) : AbstractReferenceCounted() {
  override fun touch(hint: Any): ReferenceCounted = this

  override fun deallocate() {
    if (content != null) {
      content?.release()
      content = null
      remoteAddress = null

      recycle()
    }
  }

  fun recycle() {
    recycleHandler.recycle(this)
  }

  fun remoteAddress(): InetSocketAddress? = remoteAddress

  fun content(): ByteBuf? = content

  companion object {
    private val RECYCLER = object : Recycler<UkcpPacket>() {
      override fun newObject(handle: Handle<UkcpPacket>): UkcpPacket = UkcpPacket(handle)
    }

    fun newInstance(content: ByteBuf, remoteAddress: InetSocketAddress): UkcpPacket = RECYCLER.get().apply {
      setRefCnt(1)
      this.content = content
      this.remoteAddress = remoteAddress
    }
  }
}
