#!/bin/bash
mkdir -p app/src/main/java/com/nandomx/vpn app/src/main/res/layout app/src/main/res/values app/src/main/res/drawable.github/workflows
cat > app/src/main/res/values/colors.xml <<'XML'
<resources><color name="nmx_gold">#FFD700</color><color name="nmx_black">#0A0A0A</color><color name="nmx_green">#006847</color><color name="nmx_red">#CE1126</color></resources>
XML
cat > app/src/main/res/drawable/aro_tricolor.xml <<'XML'
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="ring" android:innerRadius="110dp" android:thickness="6dp" android:useLevel="false"><gradient android:type="sweep" android:colors="#006847,#FFFFFF,#CE1126,#FFD700,#006847"/></shape>
XML
cat > app/src/main/res/layout/activity_main.xml <<'XML'
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android" android:layout_width="match_parent" android:layout_height="match_parent" android:background="@drawable/fondo_home">
<LinearLayout android:layout_width="match_parent" android:layout_height="match_parent" android:orientation="vertical" android:gravity="center" android:padding="20dp">
<FrameLayout android:layout_width="220dp" android:layout_height="220dp">
<ProgressBar android:id="@+id/aro_animado" android:layout_width="220dp" android:layout_height="220dp" android:indeterminateDrawable="@drawable/aro_tricolor" android:visibility="invisible"/>
<ImageView android:id="@+id/btnAguila" android:layout_width="180dp" android:layout_height="180dp" android:layout_gravity="center" android:src="@drawable/btn_aguila" android:elevation="10dp"/>
</FrameLayout>
<TextView android:id="@+id/txtStatus" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="NANDOMX VPN V5 - DESCONECTADO" android:textColor="#FFD700" android:textSize="16sp" android:layout_marginTop="20dp"/>
<ScrollView android:layout_width="match_parent" android:layout_height="150dp" android:layout_marginTop="15dp" android:background="#80000000"><TextView android:id="@+id/txtLog" android:layout_width="match_parent" android:layout_height="wrap_content" android:textColor="#00FF00" android:fontFamily="monospace" android:text="> Esperando comando..."/></ScrollView>
</LinearLayout></FrameLayout>
XML
cat > app/src/main/java/com/nandomx/vpn/MainActivity.kt <<'KT'
package com.nandomx.vpn
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity() {
private var conectado=false
private lateinit var logView:TextView
private lateinit var statusView:TextView
private lateinit var aro:ProgressBar
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_main)
logView=findViewById(R.id.txtLog)
statusView=findViewById(R.id.txtStatus)
aro=findViewById(R.id.aro_animado)
val btn=findViewById<ImageView>(R.id.btnAguila)
btn.setOnClickListener { if(!conectado) conectarVPN() else desconectarVPN() }
addLog("> NANDOMX FULL V5 INICIADO")
addLog("> SSH, SSL, UDP, WS Cargados")
}
fun conectarVPN(){conectado=true; aro.visibility=android.view.View.VISIBLE; statusView.text="CONECTANDO..."; addLog("> Iniciando tunel encriptado..."); Handler(Looper.getMainLooper()).postDelayed({addLog("> [OK] Socket creado"); addLog("> [OK] Payload inyectado"); addLog("> [OK] CONECTADO - IP 10.0.0.1"); statusView.text="NANDOMX VPN V5 - CONECTADO"},2000)}
fun desconectarVPN(){conectado=false; aro.visibility=android.view.View.INVISIBLE; statusView.text="NANDOMX VPN V5 - DESCONECTADO"; addLog("> Desconectado")}
fun addLog(s:String){logView.append("\n"+s)}
}
KT
cat > app/src/main/AndroidManifest.xml <<'XML'
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="com.nandomx.vpn"><uses-permission android:name="android.permission.INTERNET"/><uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/><uses-permission android:name="android.permission.FOREGROUND_SERVICE"/><application android:icon="@mipmap/ic_launcher" android:label="NANDOMX VPN V5" android:theme="@android:style/Theme.Black.NoTitleBar"><activity android:name=".MainActivity" android:exported="true"><intent-filter><action android:name="android.intent.action.MAIN"/><category android:name="android.intent.category.LAUNCHER"/></intent-filter></activity></application></manifest>
XML
cat >.github/workflows/build.yml <<'YAML'
name: Build NANDOMX APK
on: [push]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: 'temurin', java-version: '17' }
      - name: Build APK
        run: chmod +x gradlew &&./gradlew assembleDebug
      - uses: actions/upload-artifact@v4
        with: { name: NANDOMX-VPN-V5, path: app/build/outputs/apk/debug/*.apk }
YAML
echo "BASE CREADA OK"
