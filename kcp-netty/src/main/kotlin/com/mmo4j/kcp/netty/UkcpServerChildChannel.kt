package com.mmo4j.kcp.netty

import com.sun.org.slf4j.internal.LoggerFactory
import io.jpower.kcp.netty.Ukcp
import io.netty.buffer.ByteBuf
import io.netty.channel.AbstractChannel
import io.netty.channel.Channel
import io.netty.channel.ChannelMetadata
import io.netty.util.internal.StringUtil.simpleClassName
import java.net.InetSocketAddress

class UkcpServerChildChannel(
  private val parent: Channel,
  private val kcp: Ukcp,
  private val remoteAddress: InetSocketAddress
) : AbstractChannel(parent), UkcpChannel {
  private val logger = LoggerFactory.getLogger(javaClass)
//  private val config: KcpServerChannelConfig
  private var flushPending: Boolean = false

  init {
//    config =
  }

  companion object {
    private val METADATA = ChannelMetadata(false)
    private val EXPECTED_TYPES = " (expected: ${simpleClassName(ByteBuf::class)})"
  }
}
