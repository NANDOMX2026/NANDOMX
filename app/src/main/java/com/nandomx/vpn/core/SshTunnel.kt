package com.nandomx.vpn.core
import com.jcraft.jsch.JSch
class SshTunnel {
    fun connect(host:String, port:Int, user:String, pass:String, lport:Int){
        val jsch = JSch()
        val session = jsch.getSession(user, host, port)
        session.setPassword(pass)
        session.setConfig("StrictHostKeyChecking","no")
        session.connect(10000)
        session.setPortForwardingL(lport, "127.0.0.1", 1080)
    }
}
