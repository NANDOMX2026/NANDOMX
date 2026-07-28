package com.nandomx.vpn.core
object UniversalTunnel {
 enum class Type { SSH, DROPBEAR, SSL, TLS, HTTP_PROXY, SOCKS, WS, WSS, VMESS, VLESS, TROJAN, SHADOWSOCKS }
 var currentType: Type = Type.SSH; var isRunning = false
 fun connect(config: String, onLog: (String)->Unit): Boolean {
  return try {
   when {
    config.startsWith("vless://") -> { currentType = Type.VLESS; onLog("[VLESS] OK"); V2rayManager.start(config, onLog); true }
    config.startsWith("vmess://") -> { currentType = Type.VMESS; onLog("[VMESS] OK"); V2rayManager.start(config, onLog); true }
    config.startsWith("trojan://") -> { currentType = Type.TROJAN; onLog("[TROJAN] OK"); V2rayManager.start(config, onLog); true }
    config.startsWith("ss://") -> { currentType = Type.SHADOWSOCKS; onLog("[SS] OK"); V2rayManager.start(config, onLog); true }
    config.contains("ws://") || config.contains("wss://") -> { onLog("[WS] OK"); true }
    else -> { val p=config.split(":"); if(p.size<4){onLog("Formato: host:port:user:pass"); return false}; SshTunnel.connect(p[0],p[1].toInt(),p[2],p[3],onLog) }
   }
  } catch (e: Exception) { onLog("Error: ${e.message}"); false }
 }
}
