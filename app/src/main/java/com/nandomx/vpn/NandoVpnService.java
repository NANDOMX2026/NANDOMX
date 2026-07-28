package com.nandomx.vpn;
import android.content.Intent; import android.net.VpnService; import android.os.ParcelFileDescriptor; import android.util.Log;
public class NandoVpnService extends VpnService {
    static NandoVpnService inst; ParcelFileDescriptor tun; boolean running;
    public static void stop(){ if(inst!=null){ inst.running=false; inst.stopSelf(); } }
    public int onStartCommand(Intent it,int f,int id){
        inst=this; running=true;
        String host=it.getStringExtra("host"); String sni=it.getStringExtra("sni");
        new Thread(()->{ try{
            Builder b=new Builder().addAddress("10.8.0.2",32).addRoute("0.0.0.0",0).addDnsServer("8.8.8.8").setSession("NANDOMX").setMtu(1500);
            tun=b.establish();
            Log.i("NANDOMX","VPN Conectado a "+host+" SNI:"+sni);
            while(running) Thread.sleep(1000);
        }catch(Exception e){ Log.e("NANDOMX",e.toString()); } }).start();
        return START_STICKY;
    }
    public void onDestroy(){ running=false; try{if(tun!=null) tun.close();}catch(Exception e){} super.onDestroy(); }
}
