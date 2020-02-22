package com.mmo4j.kcp.netty

import io.netty.channel.Channel
import io.netty.channel.ChannelConfig
import java.net.SocketAddress

interface UkcpChannel : Channel {
  override fun config(): ChannelConfig {
    TODO("not implemented")
  }

  override fun localAddress(): SocketAddress {
    TODO("not implemented")
  }

  override fun remoteAddress(): SocketAddress {
    TODO("not implemented")
  }

  fun conv(): Int
  fun conv(conv: Int): UkcpChannel
}
