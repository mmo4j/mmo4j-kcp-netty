package com.mmo4j.kcp.netty

import io.netty.buffer.ByteBufAllocator
import io.netty.channel.ChannelConfig
import io.netty.channel.MessageSizeEstimator
import io.netty.channel.RecvByteBufAllocator
import io.netty.channel.WriteBufferWaterMark
import java.net.StandardSocketOptions

interface UkcpServerChannelConfig : ChannelConfig {
  override fun setWriteSpinCount(writeSpinCount: Int): UkcpServerChannelConfig
  override fun setConnectTimeoutMillis(connectTimeoutMillis: Int): UkcpServerChannelConfig
  override fun setAllocator(allocator: ByteBufAllocator): UkcpServerChannelConfig
  override fun setRecvByteBufAllocator(allocator: RecvByteBufAllocator): UkcpServerChannelConfig
  override fun setAutoRead(autoRead: Boolean): UkcpServerChannelConfig
  override fun setAutoClose(autoClose: Boolean): UkcpServerChannelConfig
  override fun setMessageSizeEstimator(estimator: MessageSizeEstimator): UkcpServerChannelConfig
  override fun setWriteBufferWaterMark(writeBufferWaterMark: WriteBufferWaterMark): UkcpServerChannelConfig

  /**
   * Gets the [StandardSocketOptions.SO_RCVBUF] option.
   */
  val udpReceiveBufferSize: Int

  /**
   * Sets the [StandardSocketOptions.SO_RCVBUF] option.
   */
  fun setUdpReceiveBufferSize(receiveBufferSize: Int): UkcpServerChannelConfig?

  /**
   * Gets the [StandardSocketOptions.SO_SNDBUF] option.
   */
  val udpSendBufferSize: Int

  /**
   * Sets the [StandardSocketOptions.SO_SNDBUF] option.
   */
  fun setUdpSendBufferSize(sendBufferSize: Int): UkcpServerChannelConfig?

  /**
   * Gets the [StandardSocketOptions.IP_TOS] option.
   */
  val udpTrafficClass: Int

  /**
   * Sets the [StandardSocketOptions.IP_TOS] option.
   */
  fun setUdpTrafficClass(trafficClass: Int): UkcpServerChannelConfig?

  /**
   * Gets the [StandardSocketOptions.SO_REUSEADDR] option.
   */
  val isReuseAddress: Boolean

  /**
   * Gets the [StandardSocketOptions.SO_REUSEADDR] option.
   */
  fun setReuseAddress(reuseAddress: Boolean): UkcpServerChannelConfig?
}
