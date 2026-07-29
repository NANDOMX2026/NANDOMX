p=open("app/src/main/java/com/nandomx/vpn/NandoVpnService.java").read()
if "startForeground" not in p:
  p=p.replace("import android.net.VpnService;", "import android.net.VpnService;\nimport android.app.Notification;\nimport android.app.NotificationChannel;\nimport android.app.NotificationManager;\nimport androidx.core.app.NotificationCompat;\nimport com.nandomx.vpn.R;")
  p=p.replace("tun=b.establish();", 'NotificationChannel ch=new NotificationChannel("nandomx_vpn","NANDOMX VPN",NotificationManager.IMPORTANCE_LOW);\n ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);\n Notification notif=new NotificationCompat.Builder(this,"nandomx_vpn").setSmallIcon(R.mipmap.ic_launcher).setContentTitle("NANDOMX VPN V5").setContentText("Conectado a "+host).setOngoing(true).build();\n startForeground(1,notif);\n tun=b.establish();')
  open("app/src/main/java/com/nandomx/vpn/NandoVpnService.java","w").write(p)
  print("PARCHE OK")
else:
  print("YA ESTABA")
