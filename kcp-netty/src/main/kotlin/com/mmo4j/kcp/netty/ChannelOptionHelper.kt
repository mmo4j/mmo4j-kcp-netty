package com.mmo4j.kcp.netty

import io.jpower.kcp.netty.UkcpChannelOption
import io.netty.bootstrap.Bootstrap
import io.netty.bootstrap.UkcpServerBootstrap

object ChannelOptionHelper {
  fun nodelay(b: Bootstrap, nodelay: Boolean, interval: Int, fastResend: Int, nocwnd: Boolean): Bootstrap =
    b.option(UkcpChannelOption.UKCP_NODELAY, nodelay)
      .option(UkcpChannelOption.UKCP_INTERVAL, interval)
      .option(UkcpChannelOption.UKCP_FAST_RESEND, fastResend)
      .option(UkcpChannelOption.UKCP_NOCWND, nocwnd)

  fun nodelay(
    b: UkcpServerBootstrap,
    nodelay: Boolean,
    interval: Int,
    fastResend: Int,
    nocwnd: Boolean
  ): UkcpServerBootstrap =
    b.childOption(UkcpChannelOption.UKCP_NODELAY, nodelay)
      .childOption(UkcpChannelOption.UKCP_INTERVAL, interval)
      .childOption(UkcpChannelOption.UKCP_FAST_RESEND, fastResend)
      .childOption(UkcpChannelOption.UKCP_NOCWND, nocwnd)
}
