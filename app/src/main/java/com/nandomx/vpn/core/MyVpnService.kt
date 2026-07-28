package com.nandomx.vpn.core
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.DatagramChannel
class MyVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null
    private var running = false
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotification()
        val builder = Builder()
            .addAddress("10.8.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("8.8.8.8")
            .addDnsServer("1.1.1.1")
            .setMtu(1500)
            .setSession("NANDOMX V5")
        vpnInterface = builder.establish()
        running = true
        Thread { vpnLoop() }.start()
        return START_STICKY
    }
    fun vpnLoop(){
        val fd = vpnInterface?.fileDescriptor ?: return
        val input = FileInputStream(fd).channel
        val output = FileOutputStream(fd).channel
        val buffer = java.nio.ByteBuffer.allocate(2048)
        while(running){
            try{
                val read = input.read(buffer)
                if(read>0){ buffer.flip(); output.write(buffer); buffer.clear() }
            }catch(e:Exception){ break }
        }
    }
    override fun onDestroy() { running=false; vpnInterface?.close(); super.onDestroy() }
    fun createNotification(){
        val chan = NotificationChannel("vpn","NANDOMX VPN",NotificationManager.IMPORTANCE_LOW)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(chan)
        val notif = Notification.Builder(this,"vpn").setContentTitle("NANDOMX V5 Conectado").setContentText("Tunel activo").setSmallIcon(android.R.drawable.ic_lock_lock).build()
        startForeground(1, notif)
    }
    override fun onRevoke() { stopSelf() }
}
