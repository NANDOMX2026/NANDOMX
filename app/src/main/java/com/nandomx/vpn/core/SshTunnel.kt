package com.nandomx.vpn.core
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import android.util.Log

object SshTunnel {
    private var session: Session? = null
    var isConnected = false

    fun connect(host:String, port:Int, user:String, pass:String, onLog:(String)->Unit): Boolean {
        return try {
            val jsch = JSch()
            session = jsch.getSession(user, host, port)
            session?.setPassword(pass)
            session?.setConfig("StrictHostKeyChecking", "no")
            session?.setConfig("PreferredAuthentications", "password")
            session?.timeout = 10000
            session?.connect()
            // SOCKS 1080 dinamico - TODO el trafico del TUN sale por aqui
        session?.setPortForwardingD(1080)
            isConnected = true
            onLog("[SSH] Conectado a $host:$port")
            onLog("[SSH] SOCKS 127.0.0.1:1080 ACTIVO")
            onLog("[SSH] Trafico TUN -> SSH -> Internet")
            true
        } catch (e: Exception) {
            onLog("[SSH] Error: ${e.message}")
            Log.e("SSH", "Error", e)
            false
        }
    }
    fun disconnect() { session?.disconnect(); isConnected = false }
}
