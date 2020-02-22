package com.mmo4j.kcp.netty

import java.net.InetSocketAddress

data class UkcpUser(val channel: UkcpChannel, val remoteAddress: InetSocketAddress, val localAddress: InetSocketAddress)
