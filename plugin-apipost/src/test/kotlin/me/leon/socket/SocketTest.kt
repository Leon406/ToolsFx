package me.leon.socket

import org.junit.Test

class SocketTest {
    @Test
    fun launchServer() {
        val tcpServer = TcpServer(11_111)
        tcpServer.receive()

        while (true) {
            // if needed
        }
    }

    @Test
    fun launchUdpServer() {
        val tcpServer = UdpServer(11_112)
        tcpServer.receive()

        while (true) {
            // if needed
        }
    }
}
