package com.nandomx.vpn.core
import android.content.Context
data class VpnConfig(val host:String, val port:Int, val user:String, val pass:String, val sni:String, val payload:String, val udp:Boolean)
class ConfigManager(val ctx:Context){
    fun save(config:VpnConfig){ ctx.getSharedPreferences("nmx",0).edit().putString("cfg", config.toString()).apply() }
    fun load():VpnConfig? { return null }
}
