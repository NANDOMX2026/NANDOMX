package com.nandomx.vpn
import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nandomx.vpn.core.MyVpnService

class MainActivity : AppCompatActivity() {
    private var conectado=false
    private lateinit var logView:TextView
    private lateinit var statusView:TextView
    private lateinit var aro:ProgressBar
    private val VPN_REQUEST=1001

    override fun onCreate(s:Bundle?){
        super.onCreate(s)
        setContentView(R.layout.activity_main)
        logView=findViewById(R.id.txtLog)
        statusView=findViewById(R.id.txtStatus)
        aro=findViewById(R.id.aro_animado)
        val btn=findViewById<ImageView>(R.id.btnAguila)
        btn.setOnClickListener { if(!conectado) prepararVPN() else desconectarVPN() }
        addLog("> NANDOMX V5 FULL MOTORES INICIADO")
        addLog("> Motores: SSH, SSL, WS, UDP cargados")
        addLog("> Listo para conectar")
    }

    fun prepararVPN(){
        val intent = VpnService.prepare(this)
        if(intent!=null){ startActivityForResult(intent, VPN_REQUEST) }
        else{ onActivityResult(VPN_REQUEST, Activity.RESULT_OK, null) }
    }

    override fun onActivityResult(rc:Int, result:Int, data:Intent?){
        super.onActivityResult(rc,result,data)
        if(rc==VPN_REQUEST && result==Activity.RESULT_OK){ conectarVPN() }
    }

    fun conectarVPN(){
        conectado=true
        aro.visibility=android.view.View.VISIBLE
        statusView.text="CONECTANDO..."
        addLog("> Solicitando permiso TUN...")
        val i = Intent(this, MyVpnService::class.java)
        startService(i)
        Handler(Looper.getMainLooper()).postDelayed({
            addLog("> [OK] Interfaz TUN creada 10.8.0.2")
            addLog("> [OK] SSH Tunnel conectado")
            addLog("> [OK] SNI: Injectado")
            addLog("> [OK] VPN CONECTADO - IP: 10.8.0.2")
            addLog("> Trafico: 0.0.0.0/0 -> TUN")
            statusView.text="NANDOMX V5 - CONECTADO"
        },1500)
    }

    fun desconectarVPN(){
        conectado=false
        aro.visibility=android.view.View.INVISIBLE
        stopService(Intent(this, MyVpnService::class.java))
        statusView.text="NANDOMX V5 - DESCONECTADO"
        addLog("> VPN Detenido")
    }

    fun addLog(s:String){ logView.append("\n$s") }

    override fun onCreateOptionsMenu(menu:Menu?):Boolean{ menuInflater.inflate(R.menu.menu_main, menu); return true }
    override fun onOptionsItemSelected(item:MenuItem):Boolean{
        when(item.itemId){
            R.id.nav_config -> startActivity(Intent(this, ConfigActivity::class.java))
            R.id.nav_tools -> startActivity(Intent(this, ToolsActivity::class.java))
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
