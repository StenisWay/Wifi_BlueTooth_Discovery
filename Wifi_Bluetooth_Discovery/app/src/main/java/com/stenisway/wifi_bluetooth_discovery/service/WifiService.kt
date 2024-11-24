package com.stenisway.wifi_bluetooth_discovery.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.stenisway.wifi_bluetooth_discovery.R
import com.stenisway.wifi_bluetooth_discovery.interfaces.BaseDeviceUse
import com.stenisway.wifi_bluetooth_discovery.model.WifiItem
import com.stenisway.wifi_bluetooth_discovery.repository.submitWifiList
import com.stenisway.wifi_bluetooth_discovery.util.withDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class WifiService : Service(), BaseDeviceUse {

    private val TAG = "WIFI"
    private lateinit var wifiManager: WifiManager
    private var isDiscovery = false

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "WifiService onCreate")
        init()
    }

    override fun onDestroy() {
        this.unregisterReceiver(wifiScanReceiver)
        super.onDestroy()
    }

    private fun init(){
        val weakContext = WeakReference(applicationContext)
        wifiManager = weakContext.get()?.getSystemService(WIFI_SERVICE) as WifiManager

        val intent = IntentFilter()
        intent.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intent.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        if (Build.VERSION.SDK_INT >= 33) {
            this.registerReceiver(wifiScanReceiver, intent, RECEIVER_EXPORTED)
        } else {
            this.registerReceiver(wifiScanReceiver, intent)
        }
    }


    override fun onBind(intent: Intent): IBinder {

        return LocalBinder()
    }

    inner class LocalBinder : Binder() {

        fun getService(): WifiService = this@WifiService

    }

    private fun toggleWifi(enable: Boolean) {
        if (wifiManager.isWifiEnabled != enable) {
            wifiManager.isWifiEnabled = enable
        }
    }

    private fun String?.nullToNA() : String{
        return if (this.isNullOrEmpty() || this.isBlank()){
                    "N/A"
                }else{
                    this
                }
    }


    private fun createNotification(): Notification {
        val channelId = "WifiServiceChannel"

        val channel = NotificationChannel(
            channelId,
            "Wi-Fi 控制服務",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Wi-Fi 服務")
            .setContentText("正在控制 Wi-Fi 設備")
            .setSmallIcon(R.drawable.baseline_network_wifi_24)
            .build()
    }


    fun isEnable() : Boolean{
       return wifiManager.isWifiEnabled
    }


    private val wifiScanReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                    Log.d(TAG, "wifi Finish Scan")
                    withDefault(TAG){
                        val list = wifiManager.scanResults.map {
                            val name =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    it.wifiSsid.toString().nullToNA()
                                } else {
                                    it.SSID.toString().nullToNA()
                                }
                            WifiItem(name, it.BSSID, it.level)
                        }
                        Log.d(TAG, "onReceive: ${list.toString()}")
                        withContext(Dispatchers.IO){
                            submitWifiList(list)
                        }
                        if (isDiscovery){
                            delay(30000)
                            if (wifiManager.isWifiEnabled){
                                Log.d(TAG, "wifi startNextScan")
                                this@WifiService.startScan()
                            }
                        }
                    }
                }
                WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                    val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                    when(wifiState){
                        WifiManager.WIFI_STATE_DISABLED -> {
                            Log.d(TAG, "Wi-Fi is disabled")
                            this@WifiService.stopScan()
                        }
                        WifiManager.WIFI_STATE_DISABLING -> Log.d(TAG, "Wi-Fi is disabling")
                        WifiManager.WIFI_STATE_ENABLED -> {
                            Log.d(TAG, "Wi-Fi is enabled")
                            if (isDiscovery){
                                if (wifiManager.isWifiEnabled){
                                    this@WifiService.startScan()
                                }
                            }
                        }
                        WifiManager.WIFI_STATE_ENABLING -> Log.d(TAG, "Wi-Fi is enabling")
                        WifiManager.WIFI_STATE_UNKNOWN -> Log.d(TAG, "Wi-Fi state is unknown")
                    }
                }
            }
        }
    }

    override fun connect(): Boolean {
        return false
    }

    override fun disconnect(): Boolean {
        return false
    }

    @SuppressLint("MissingPermission")
    override fun startScan() {
        Log.d(TAG, "wifi startScan")
        isDiscovery = true
        wifiManager.startScan()
    }

    override fun stopScan() {
        isDiscovery = false
    }
}