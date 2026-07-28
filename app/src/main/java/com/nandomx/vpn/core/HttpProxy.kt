package com.nandomx.vpn.core
import java.net.*
import java.io.*

class HttpProxy(private val payloadTemplate: String) {
    private var running = false
    private var server: ServerSocket? = null
    fun start(port: Int) {
        running = true
        Thread {
            try {
                server = ServerSocket(port)
                println("HttpProxy iniciado en $port con payload: $payloadTemplate")
                while(running) {
                    val client = server!!.accept()
                    Thread {
                        try {
                            val reader = BufferedReader(InputStreamReader(client.getInputStream()))
                            val request = reader.readLine() ?: ""
                            // PAYLOAD INJECTION
                            if(request.contains("CONNECT")) {
                                val payload = payloadTemplate.replace("[host_port]", "8.8.8.8:443")
                                client.getOutputStream().write(payload.toByteArray())
                                client.getOutputStream().write("HTTP/1.1 200 Connection established\r\n\r\n".toByteArray())
                                client.getOutputStream().flush()
                            }
                        } catch(_: Exception) {}
                    }.start()
                }
            } catch(e: Exception) { e.printStackTrace() }
        }.start()
    }
    fun stop() { running = false; try { server?.close() } catch(_: Exception) {} }
}
