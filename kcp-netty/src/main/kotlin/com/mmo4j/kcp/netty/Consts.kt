package com.mmo4j.kcp.netty

import io.netty.util.internal.SystemPropertyUtil
import io.netty.util.internal.logging.InternalLoggerFactory

object Consts {
  val scheduleUpdateLog = InternalLoggerFactory.getInstance("com.mmo4j.kcp.netty.scheduleUpdate")
  val FIXED_RECV_BYTEBUF_ALLOCATE_SIZE = SystemPropertyUtil.getInt("com.mmo4j.kcp.updRecvAllocateSize", 2048)
  const val CLOSE_WAIT_TIME = 5 * 1000
}
