package com.mmo4j.kcp.netty

import io.netty.channel.ChannelOption

class UkcpChannelOption<T> : ChannelOption<T>(null) {
  val KCP_NODELAY = valueOf<Boolean>(UkcpChannelOption::class.java, "KCP_NODELAY")
  val KCP_INTERVAL = valueOf<Int>(UkcpChannelOption::class.java, "KCP_INTERVAL")
  val KCP_FAST_RESEND = valueOf<Int>(UkcpChannelOption::class.java, "KCP_FAST_RESEND")
  val KCP_FAST_LIMIT = valueOf<Int>(UkcpChannelOption::class.java, "KCP_FAST_LIMIT")
  val KCP_NOCWND = valueOf<Boolean>(UkcpChannelOption::class.java, "KCP_NOCWND")
  val KCP_MIN_RTO = valueOf<Int>(UkcpChannelOption::class.java, "KCP_MIN_RTO")
  val KCP_MTU = valueOf<Int>(UkcpChannelOption::class.java, "KCP_MTU")
  val KCP_RCV_WND = valueOf<Int>(UkcpChannelOption::class.java, "KCP_RCV_WND")
  val KCP_SND_WND = valueOf<Int>(UkcpChannelOption::class.java, "KCP_SND_WND")
  val KCP_STREAM = valueOf<Boolean>(UkcpChannelOption::class.java, "KCP_STREAM")
  val KCP_DEAD_LINK = valueOf<Int>(UkcpChannelOption::class.java, "KCP_DEAD_LINK")
  val KCP_AUTO_SET_CONV = valueOf<Boolean>(UkcpChannelOption::class.java, "KCP_AUTO_SET_CONV")
  val KCP_FAST_FLUSH = valueOf<Boolean>(UkcpChannelOption::class.java, "KCP_FAST_FLUSH")
  val KCP_MERGE_SEGMENT_BUF = valueOf<Boolean>(UkcpChannelOption::class.java, "KCP_MERGE_SEGMENT_BUF")
}
