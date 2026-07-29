package com.nandomx.vpn.core

object UniversalTunnel {
    enum class Type { SSH, TLS, HTTP_PROXY, SOCKS, WS, WSS, VLESS, VMESS, TROJAN, SHADOWSOCKS }
    var currentType: Type = Type.SSH
    var isRunning: Boolean = false

    fun connect(config: String, onLog: (String) -> Unit): Boolean {
        return try {
            isRunning = true
            when {
                config.startsWith("vless://") -> {
                    currentType = Type.VLESS
                    onLog("VLESS OK")
                    V2rayManager.start(config, onLog)
                    true
                }
                config.startsWith("vmess://") -> {
                    currentType = Type.VMESS
                    onLog("VMESS OK")
                    V2rayManager.start(config, onLog)
                    true
                }
                config.startsWith("trojan://") -> {
                    currentType = Type.TROJAN
                    onLog("TROJAN OK")
                    V2rayManager.start(config, onLog)
                    true
                }
                config.startsWith("ss://") -> {
                    currentType = Type.SHADOWSOCKS
                    onLog("SHADOWSOCKS OK")
                    V2rayManager.start(config, onLog)
                    true
                }
                else -> {
                    // Formato SSH esperado: host:port:user:pass
                    val parts = config.split(":")
                    if (parts.size < 4) {
                        onLog("Formato SSH: host:port:user:pass")
                        return false
                    }
                    currentType = Type.SSH
                    onLog("SSH ${parts[0]}:${parts[1]}")
                    SshTunnel.connect(parts[0], parts[1].toInt(), parts[2], parts[3], onLog)
                }
            }
        } catch (e: Exception) {
            onLog("Error: ${e.message}")
            false
        }
    }

    fun disconnect() {
        isRunning = false
        try { V2rayManager.stop() } catch (_: Exception) {}
        try { SshTunnel.disconnect() } catch (_: Exception) {}
    }
}
