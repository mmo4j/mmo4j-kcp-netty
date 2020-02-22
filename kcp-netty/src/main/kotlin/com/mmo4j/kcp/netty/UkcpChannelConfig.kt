package com.mmo4j.kcp.netty

import io.netty.channel.ChannelConfig

interface UkcpChannelConfig : ChannelConfig {
  val isNodelay: Boolean
  val interval: Int
  val fastResend: Int
  val fastLimit: Int
  val isNocwnd: Boolean
  val isStream: Boolean
  val deadLink: Int
  val isAutoSetConv: Boolean
  val isFastFlush: Boolean
  val isMergeSegmentBuf: Boolean
  val minRto: Int
  val mtu: Int
  val rcvWnd: Int
  val sndWnd: Int

  fun setNodelay(nodelay: Boolean): UkcpChannelConfig?
  fun setInterval(interval: Int): UkcpChannelConfig?
  fun setFastResend(fastResend: Int): UkcpChannelConfig?
  fun setFastLimit(fastLimit: Int): UkcpChannelConfig?
  fun setNocwnd(nc: Boolean): UkcpChannelConfig?
  fun setMinRto(minRto: Int): UkcpChannelConfig?
  fun setMtu(mtu: Int): UkcpChannelConfig?
  fun setRcvWnd(rcvWnd: Int): UkcpChannelConfig?
  fun setSndWnd(sndWnd: Int): UkcpChannelConfig?
  fun setStream(stream: Boolean): UkcpChannelConfig?
  fun setDeadLink(deadLink: Int): UkcpChannelConfig?
  fun setAutoSetConv(autoSetConv: Boolean): UkcpChannelConfig?
  fun setFastFlush(fastFlush: Boolean): UkcpChannelConfig?
  fun setMergeSegmentBuf(mergeSegmentBuf: Boolean): UkcpChannelConfig?
}
