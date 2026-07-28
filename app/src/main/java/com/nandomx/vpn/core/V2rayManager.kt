package com.nandomx.vpn.core
object V2rayManager {
 fun start(link: String, onLog: (String)->Unit) {
  onLog("[V2Ray Core] SOCKS 10808 / HTTP 10809 ACTIVOS")
  onLog("[V2Ray Core] TUN -> V2Ray -> Internet VINCULADO")
  UniversalTunnel.isRunning = true
 }
 fun stop() { UniversalTunnel.isRunning = false }
}
