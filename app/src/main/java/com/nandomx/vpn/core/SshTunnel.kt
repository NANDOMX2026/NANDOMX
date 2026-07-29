package com.nandomx.vpn.core

object SshTunnel {
    var isConnected = false

    fun connect(host: String, port: Int, user: String, pass: String, onLog: (String) -> Unit): Boolean {
        onLog("SSH Conectando $host:$port con $user")
        isConnected = true
        // Aqui va tu logica real de SSH, por ahora solo simula conexion
        return true
    }

    fun disconnect() {
        isConnected = false
    }

    fun start() {
        println("SshTunnel iniciado")
    }

    fun stop() {
        isConnected = false
    }
}
