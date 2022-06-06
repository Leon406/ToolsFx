package me.leon.socket

object Socket {

    private const val tcpMode = true

    @JvmStatic
    fun main(args: Array<String>) {
        if (tcpMode) tcpTest() else udpTest()

        while (true) {
            // if needed
        }
    }

    private fun tcpTest() {
        TcpClient("localhost", 11_111).fromConsole()
    }

    private fun udpTest() {
        UdpClient("localhost", 11_112, 11_113).fromConsole()
    }
}
