package com.nandomx.vpn.core
import java.net.Socket
import javax.net.ssl.*
import javax.net.ssl.SNIHostName

class SslTunnel(private val sniHost: String) {
    private var running = false
    private var socket: Socket? = null
    // SNI INJECTION: esto es lo que hace que jale con redes bloqueadas
    fun start(localPort: Int, remoteIp: String, remotePort: Int) {
        running = true
        Thread {
            try {
                val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
                val sslSocket = factory.createSocket(remoteIp, remotePort) as SSLSocket
                val params = SSLParameters()
                params.serverNames = listOf(SNIHostName(sniHost))
                sslSocket.sslParameters = params
                sslSocket.startHandshake()
                println("SslTunnel conectado con SNI: $sniHost -> $remoteIp:$remotePort")
                socket = sslSocket
                while(running) { Thread.sleep(1000) }
            } catch(e: Exception) { e.printStackTrace() }
        }.start()
    }
    fun stop() { running = false; try { socket?.close() } catch(_: Exception) {} }
}
