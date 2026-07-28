package com.nandomx.vpn.ui
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nandomx.vpn.core.UniversalTunnel
import com.nandomx.vpn.databinding.ActivityConfigImportBinding
class ConfigImportActivity : AppCompatActivity() {
 private lateinit var binding: ActivityConfigImportBinding
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  binding = ActivityConfigImportBinding.inflate(layoutInflater)
  setContentView(binding.root)
  binding.btnImport.setOnClickListener {
   val link = binding.etLink.text.toString().trim()
   if(link.isEmpty()) return@setOnClickListener
   binding.tvLog.append("\n> Importando: $link\n")
   UniversalTunnel.connect(link) { log -> runOnUiThread { binding.tvLog.append("$log\n") } }
  }
 }
}
