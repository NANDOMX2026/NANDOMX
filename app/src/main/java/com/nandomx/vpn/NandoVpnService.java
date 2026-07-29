package com.nandomx.vpn;
import android.content.Intent; import android.net.VpnService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;
import com.nandomx.vpn.R; import android.os.ParcelFileDescriptor; import android.util.Log;
public class NandoVpnService extends VpnService {
    static NandoVpnService inst; ParcelFileDescriptor tun; boolean running;
    public static void stop(){ if(inst!=null){ inst.running=false; inst.stopSelf(); } }
    public int onStartCommand(Intent it,int f,int id){
        inst=this; running=true;
        String host=it.getStringExtra("host"); String sni=it.getStringExtra("sni");
        new Thread(()->{ try{
            Builder b=new Builder().addAddress("10.8.0.2",32).addRoute("0.0.0.0",0).addDnsServer("8.8.8.8").setSession("NANDOMX").setMtu(1500);
            NotificationChannel ch=new NotificationChannel("nandomx_vpn","NANDOMX VPN",NotificationManager.IMPORTANCE_LOW);
 ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
 Notification notif=new NotificationCompat.Builder(this,"nandomx_vpn").setSmallIcon(R.mipmap.ic_launcher).setContentTitle("NANDOMX VPN V5").setContentText("Conectado a "+host).setOngoing(true).build();
 startForeground(1,notif);
 NotificationChannel ch=new NotificationChannel("nandomx_vpn","NANDOMX VPN",NotificationManager.IMPORTANCE_LOW); ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch); Notification notif=new NotificationCompat.Builder(this,"nandomx_vpn").setSmallIcon(R.mipmap.ic_launcher).setContentTitle("NANDOMX VPN V5").setContentText("Conectado").setOngoing(true).build(); startForeground(1,notif); tun=b.establish();;
            Log.i("NANDOMX","VPN Conectado a "+host+" SNI:"+sni);
            while(running) Thread.sleep(1000);
        }catch(Exception e){ Log.e("NANDOMX",e.toString()); } }).start();
        return START_STICKY;
    }
    public void onDestroy(){ running=false; try{if(tun!=null) tun.close();}catch(Exception e){} super.onDestroy(); }
}
