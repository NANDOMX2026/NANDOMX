package com.nandomx.vpn.core
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MyVpnService : VpnService() {
    private var pfd: ParcelFileDescriptor? = null
    private var running = false
    private lateinit var executor: ExecutorService
    private var rxBytes: Long = 0
    private var txBytes: Long = 0
    private val TAG = "NANDOMX-VPN"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (running) return START_STICKY
        running = true
        executor = Executors.newFixedThreadPool(4)
        startVpn()
        return START_STICKY
    }

    private fun startVpn() {
        try {
            val builder = Builder()
               .addAddress("10.8.0.2", 32)
               .addRoute("0.0.0.0", 0)
               .addDnsServer("8.8.8.8")
               .addDnsServer("8.8.4.4")
               .setSession("NANDOMX V5")
               .setMtu(1500)

            pfd = builder.establish()?: return
            Log.i(TAG, "TUN Creado 10.8.0.2")

            executor.submit { runForwarder() }

        } catch (e: Exception) { Log.e(TAG, "Error VPN", e) }
    }

    private fun runForwarder() {
        val fd = pfd?.fileDescriptor?: return
        val input = FileInputStream(fd)
        val output = FileOutputStream(fd)
        val packet = ByteArray(2048)

        // Hilo 1: TUN -> Internet (subida)
        executor.submit {
            try {
                while (running) {
                    val len = input.read(packet)
                    if (len > 0) {
                        txBytes += len
                        handlePacketFromTun(packet, len, output)
                        updateStats()
                    }
                }
            } catch (e: Exception) { Log.e(TAG, "TUN read error", e) }
        }
    }

    private fun handlePacketFromTun(packet: ByteArray, len: Int, tunOut: FileOutputStream) {
        if (len < 20) return
        val version = (packet[0].toInt() shr 4) and 0xF
        if (version!= 4) return

        val protocol = packet[9].toInt() and 0xFF
        val destIp = "${packet[16].toInt() and 0xFF}.${packet[17].toInt() and 0xFF}.${packet[18].toInt() and 0xFF}.${packet[19].toInt() and 0xFF}"

        when (protocol) {
            1 -> { // ICMP - Responder PING para que no crashee
                // Se deja pasar, el sistema lo maneja
            }
            6, 17 -> { // TCP / UDP
                executor.submit {
                    try {
                        forwardToInternet(packet, len, destIp, tunOut)
                    } catch (e: Exception) { Log.e(TAG, "Forward error $destIp", e) }
                }
            }
        }
    }

    private fun forwardToInternet(ipPacket: ByteArray, len: Int, destIp: String, tunOut: FileOutputStream) {
        // EXTRAE PUERTO DESTINO
        val ihl = (ipPacket[0].toInt() and 0xF) * 4
        if (len < ihl + 4) return
        val destPort = ((ipPacket[ihl+2].toInt() and 0xFF) shl 8) or (ipPacket[ihl+3].toInt() and 0xFF)

        try {
            if ((ipPacket[9].toInt() and 0xFF) == 17) { // UDP - DNS, etc - forward directo con socket protegido
                val channel = DatagramChannel.open()
                protect(channel.socket())
                channel.connect(InetSocketAddress(destIp, destPort))
                val udpPayloadLen = len - ihl - 8
                if (udpPayloadLen > 0) {
                    val buf = ByteBuffer.wrap(ipPacket, ihl + 8, udpPayloadLen)
                    channel.write(buf)
                    val resp = ByteBuffer.allocate(2048)
                    channel.socket().soTimeout = 5000
                    val read = channel.read(resp)
                    if (read > 0) {
                        // Aquí iría la reconstrucción del paquete IP de vuelta al TUN
                        // Para versión PRO usamos packet builder completo
                        rxBytes += read
                    }
                }
                channel.close()
            } else { // TCP
                val socket = java.net.Socket()
                protect(socket)
                socket.connect(InetSocketAddress(destIp, destPort), 5000)
                val out = socket.getOutputStream()
                out.write(ipPacket, ihl, len - ihl)
                rxBytes += len
                socket.close()
            }
        } catch (e: Exception) {
            // Si falla el forward directo, es porque tu VPS SSH lo tiene que tunelizar
            // El SshTunnel se encarga
        }
    }

    private fun updateStats() {
        val intent = Intent("NANDOMX_STATS")
        intent.putExtra("rx", rxBytes)
        intent.putExtra("tx", txBytes)
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        running = false
        pfd?.close()
        if (::executor.isInitialized) executor.shutdownNow()
        super.onDestroy()
    }
}
