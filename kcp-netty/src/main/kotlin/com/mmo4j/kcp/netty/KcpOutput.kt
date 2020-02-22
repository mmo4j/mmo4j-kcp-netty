package com.mmo4j.kcp.netty

import io.jpower.kcp.netty.Kcp
import io.netty.buffer.ByteBuf

interface KcpOutput {
  fun out(data: ByteBuf, kcp: Kcp)
}
