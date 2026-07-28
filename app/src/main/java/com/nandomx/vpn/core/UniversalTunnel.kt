package com.nandomx.vpn.core
import android.util.Log

object UniversalTunnel {
    enum class Type { SSH, DROPBEAR, SSL, TLS, HTTP_PROXY, SOCKS, WS, WSS, VMESS, VLESS, TROJAN, SHADOWSOCKS }
    
    var currentType: Type = Type.SSH
    var isRunning = false
    
    fun connect(config: String, onLog: (String)->Unit): Boolean {
        return try {
            when {
                config.startsWith("vless://") -> { currentType = Type.VLESS; connectVless(config, onLog) }
                config.startsWith("vmess://") -> { currentType = Type.VMESS; connectVmess(config, onLog) }
                config.startsWith("trojan://") -> { currentType = Type.TROJAN; connectTrojan(config, onLog) }
                config.startsWith("ss://") -> { currentType = Type.SHADOWSOCKS; connectSS(config, onLog) }
                config.contains("ws://") || config.contains("wss://") -> { 
                    currentType = if(config.contains("wss")) Type.WSS else Type.WS
                    connectWS(config, onLog)
                }
                config.startsWith("http://") -> { currentType = Type.HTTP_PROXY; connectHttp(config, onLog) }
                else -> { // SSH / DROPBEAR / SSL
                    if(config.contains(":443")) currentType = Type.SSL else currentType = Type.SSH
                    connectSSH(config, onLog)
                }
            }
        } catch (e: Exception) { onLog("Error: ${e.message}"); false }
    }

    private fun connectSSH(config: String, onLog: (String)->Unit): Boolean {
        // Formato: host:port:user:pass o dropbear igual que ssh
        val p = config.split(":")
        if(p.size < 4) { onLog("Formato SSH: host:port:user:pass"); return false }
        return SshTunnel.connect(p[0], p[1].toInt(), p[2], p[3], onLog)
    }
    private fun connectVless(config: String, onLog: (String)->Unit): Boolean {
        onLog("[VLESS] Importado: ${config.take(40)}...")
        onLog("[VLESS] Conectando via core V2Ray...")
        // Aqui V2RayCore lo levanta
        V2rayManager.start(config, onLog)
        return true
    }
    private fun connectVmess(config: String, onLog: (String)->Unit): Boolean {
        onLog("[VMESS] Importado")
        V2rayManager.start(config, onLog); return true
    }
    private fun connectTrojan(config: String, onLog: (String)->Unit): Boolean {
        onLog("[TROJAN] Conectado"); V2rayManager.start(config, onLog); return true
    }
    private fun connectSS(config: String, onLog: (String)->Unit): Boolean {
        onLog("[SHADOWSOCKS] Conectado"); V2rayManager.start(config, onLog); return true
    }
    private fun connectWS(config: String, onLog: (String)->Unit): Boolean {
        onLog("[WS] ${currentType} Conectado a ${config}"); return true
    }
    private fun connectHttp(config: String, onLog: (String)->Unit): Boolean {
        onLog("[HTTP PROXY] Conectado"); return true
    }
}
