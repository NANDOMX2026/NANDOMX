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
            val s = jsch.getSession(user, host, port)
            s.setPassword(pass)
            s.setConfig("StrictHostKeyChecking", "no")
            s.setConfig("PreferredAuthentications", "password")
            s.timeout = 10000
            s.connect()
            s.setPortForwardingD(1080)
            session = s
            isConnected = true
            onLog("[SSH] Conectado a $host:Sport()")
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
