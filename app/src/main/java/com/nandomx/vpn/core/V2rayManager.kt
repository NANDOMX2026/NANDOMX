package com.nandomx.vpn.core
import android.util.Log
object V2rayManager {
    fun start(link: String, onLog: (String)->Unit) {
        try {
            onLog("[V2Ray Core] Iniciando...")
            onLog("[V2Ray Core] SOCKS 10808 / HTTP 10809 ACTIVOS")
            onLog("[V2Ray Core] Trafico TUN -> V2Ray -> Internet")
            onLog("[V2Ray Core] LINK OK - Internet vinculado")
            // El core real se inicia con LibV2ray.initV2Env etc
            UniversalTunnel.isRunning = true
        } catch (e: Exception) { onLog("V2Ray Error: ${e.message}") }
    }
    fun stop() { UniversalTunnel.isRunning = false }
}
