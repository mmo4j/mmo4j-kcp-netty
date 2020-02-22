package com.mmo4j.kcp.netty

import io.netty.buffer.ByteBufAllocator
import io.netty.channel.ChannelConfig
import io.netty.channel.MessageSizeEstimator
import io.netty.channel.RecvByteBufAllocator
import io.netty.channel.WriteBufferWaterMark
import java.net.StandardSocketOptions

interface UkcpClientChannelConfig : ChannelConfig, UkcpChannelConfig {
  override fun setNodelay(nodelay: Boolean): UkcpClientChannelConfig
  override fun setInterval(interval: Int): UkcpClientChannelConfig
  override fun setFastResend(resend: Int): UkcpClientChannelConfig
  override fun setNocwnd(nc: Boolean): UkcpClientChannelConfig
  override fun setMinRto(minRto: Int): UkcpClientChannelConfig
  override fun setMtu(mtu: Int): UkcpClientChannelConfig
  override fun setRcvWnd(rcvWnd: Int): UkcpClientChannelConfig
  override fun setSndWnd(sndWnd: Int): UkcpClientChannelConfig
  override fun setStream(stream: Boolean): UkcpClientChannelConfig
  override fun setDeadLink(deadLink: Int): UkcpClientChannelConfig
  override fun setWriteSpinCount(writeSpinCount: Int): UkcpClientChannelConfig
  override fun setConnectTimeoutMillis(connectTimeoutMillis: Int): UkcpClientChannelConfig
  override fun setAllocator(allocator: ByteBufAllocator): UkcpClientChannelConfig
  override fun setRecvByteBufAllocator(allocator: RecvByteBufAllocator): UkcpClientChannelConfig
  override fun setAutoRead(autoRead: Boolean): UkcpClientChannelConfig
  override fun setAutoClose(autoClose: Boolean): UkcpClientChannelConfig
  override fun setMessageSizeEstimator(estimator: MessageSizeEstimator): UkcpClientChannelConfig
  override fun setWriteBufferWaterMark(writeBufferWaterMark: WriteBufferWaterMark): UkcpClientChannelConfig

  /**
   * Gets the [StandardSocketOptions.SO_RCVBUF] option.
   */
  val udpReceiveBufferSize: Int

  /**
   * Sets the [StandardSocketOptions.SO_RCVBUF] option.
   */
  fun setUdpReceiveBufferSize(receiveBufferSize: Int): UkcpClientChannelConfig

  /**
   * Gets the [StandardSocketOptions.SO_SNDBUF] option.
   */
  val udpSendBufferSize: Int

  /**
   * Sets the [StandardSocketOptions.SO_SNDBUF] option.
   */
  fun setUdpSendBufferSize(sendBufferSize: Int): UkcpClientChannelConfig

  /**
   * Gets the [StandardSocketOptions.IP_TOS] option.
   */
  val udpTrafficClass: Int

  /**
   * Sets the [StandardSocketOptions.IP_TOS] option.
   */
  fun setUdpTrafficClass(trafficClass: Int): UkcpClientChannelConfig

  /**
   * Gets the [StandardSocketOptions.SO_REUSEADDR] option.
   */
  val isReuseAddress: Boolean

  /**
   * Gets the [StandardSocketOptions.SO_REUSEADDR] option.
   */
  fun setReuseAddress(reuseAddress: Boolean): UkcpClientChannelConfig
}
