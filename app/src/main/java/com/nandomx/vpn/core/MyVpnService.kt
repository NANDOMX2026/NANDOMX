package com.nandomx.vpn.core
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor

class MyVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null
    private var httpProxy: HttpProxy? = null
    private var dnsForwarder: DnsForwarder? = null
    private var udpForwarder: UdpForwarder? = null
    private var tcpForwarder: TcpForwarder? = null
    private var sslTunnel: SslTunnel? = null
    private var sshTunnel: SshTunnel? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            try {
                val builder = Builder().addAddress("10.0.0.2", 32).addRoute("0.0.0.0", 0).addDnsServer("8.8.8.8").setSession("NandoMX VPN").setMtu(1500)
                vpnInterface = builder.establish()
                dnsForwarder = DnsForwarder()
                dnsForwarder?.start()
                udpForwarder = UdpForwarder()
                udpForwarder?.start()
                tcpForwarder = TcpForwarder()
                tcpForwarder?.start()
                val ip = intent?.getStringExtra("ip") ?: "0.0.0.0"
                val port = intent?.getIntExtra("port", 443) ?: 443
                val sni = intent?.getStringExtra("sni") ?: "www.google.com"
                sslTunnel = SslTunnel(sni)
                sslTunnel?.start(1080, ip, port)
                sshTunnel = SshTunnel()
                sshTunnel?.start()
                val payload = intent?.getStringExtra("payload") ?: ""
                httpProxy = HttpProxy(payload)
                httpProxy?.start(1080)
                println("MyVpnService TODOS LOS MOTORES INICIADOS HttpProxy DnsForwarder UdpForwarder TcpForwarder SslTunnel SshTunnel")
            } catch(e: Exception) { e.printStackTrace() }
        }.start()
        return START_STICKY
    }
    override fun onDestroy() {
        vpnInterface?.close()
        httpProxy?.stop()
        dnsForwarder?.stop()
        udpForwarder?.stop()
        tcpForwarder?.stop()
        sslTunnel?.stop()
        sshTunnel?.stop()
        super.onDestroy()
    }
}
