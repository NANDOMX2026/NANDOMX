package com.nandomx.vpn;
import android.app.Activity; import android.content.Intent; import android.net.VpnService; import android.os.Bundle; import android.widget.*;
public class MainActivity extends Activity {
    EditText etHost, etPort, etUser, etPass, etSni; Button btn; boolean conn=false;
    protected void onCreate(Bundle b){ super.onCreate(b);
        LinearLayout L=new LinearLayout(this); L.setOrientation(1); L.setPadding(40,40,40,40);
        etHost=new EditText(this); etHost.setHint("Servidor: 78.12.55.225");
        etPort=new EditText(this); etPort.setHint("Puerto: 22");
        etUser=new EditText(this); etUser.setHint("Usuario");
        etPass=new EditText(this); etPass.setHint("Clave");
        etSni=new EditText(this); etSni.setHint("SNI: www.google.com");
        btn=new Button(this); btn.setText("CONECTAR NANDOMX");
        L.addView(etHost); L.addView(etPort); L.addView(etUser); L.addView(etPass); L.addView(etSni); L.addView(btn);
        setContentView(L);
        btn.setOnClickListener(v->{ if(!conn){ Intent i=VpnService.prepare(this); if(i!=null) startActivityForResult(i,100); else startVpn(); } else { NandoVpnService.stop(); conn=false; btn.setText("CONECTAR NANDOMX"); }});
    }
    void startVpn(){ Intent i=new Intent(this,NandoVpnService.class); i.putExtra("host",etHost.getText().toString()); i.putExtra("port",etPort.getText().toString()); i.putExtra("user",etUser.getText().toString()); i.putExtra("pass",etPass.getText().toString()); i.putExtra("sni",etSni.getText().toString()); startService(i); conn=true; btn.setText("DESCONECTAR"); }
    protected void onActivityResult(int a,int b,Intent c){ if(a==100&&b==RESULT_OK) startVpn(); }
}
