package com.nandomx.vpn.core
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor

class MyVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null
    private var dnsForwarder: DnsForwarder? = null
    private var httpProxy: HttpProxy? = null
    private var tcpForwarder: TcpForwarder? = null
    private var udpForwarder: UdpForwarder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val builder = Builder().addAddress("10.0.0.2", 32).addRoute("0.0.0.0", 0).addDnsServer("8.8.8.8").setSession("NANDOMX VPN").setMtu(1500)
            vpnInterface = builder.establish()
            dnsForwarder?.start()
            httpProxy?.start()
            tcpForwarder?.start()
            udpForwarder?.start()
            val host = intent?.getStringExtra("host") ?: "8.8.8.8"
            val port = intent?.getIntExtra("port", 443) ?: 443
            val sni = intent?.getStringExtra("sni") ?: "www.google.com"
            val user = intent?.getStringExtra("user") ?: ""
            val pass = intent?.getStringExtra("pass") ?: ""
            val payload = intent?.getStringExtra("payload") ?: ""
            
            SshTunnel.connect(host, port, user, pass) { log -> println(log) }
            println("VPN INICIADA: $host:$port SNI:$sni payload:$payload")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        vpnInterface?.close()
        dnsForwarder?.stop()
        httpProxy?.stop()
        tcpForwarder?.stop()
        udpForwarder?.stop()
        SshTunnel.disconnect()
        super.onDestroy()
    }
}
