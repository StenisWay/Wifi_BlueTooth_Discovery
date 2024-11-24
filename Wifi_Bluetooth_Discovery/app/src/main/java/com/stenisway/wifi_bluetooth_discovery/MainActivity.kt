package com.stenisway.wifi_bluetooth_discovery

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stenisway.wifi_bluetooth_discovery.service.BluetoothService
import com.stenisway.wifi_bluetooth_discovery.service.WifiService
import com.stenisway.wifi_bluetooth_discovery.util.withDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewToScreen()
        startBTService(this)
        startWifiService(this)
        checkBluetoothPermissions()
    }

    override fun onDestroy() {
        stopWifiService(this)
        stopBTService(this)
        super.onDestroy()
    }

    private var bluetoothService: BluetoothService? = null
    private var wifiService: WifiService? = null

    private val wifi_connectStatus = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            wifiService = (service as WifiService.LocalBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            wifiService = null
        }
    }
    private val bluetooth_connectStatus = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bluetoothService = (service as BluetoothService.LocalBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bluetoothService = null
        }
    }

    private fun initViewToScreen() {
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun checkBluetoothPermissions() {
        // wifi請求權限 ACCESS_COARSE_LOCATION
        // 如果系統版本是 Android 12 或更高版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // 請求 BLUETOOTH_SCAN 和 BLUETOOTH_CONNECT 權限
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.BLUETOOTH_SCAN,
                        android.Manifest.permission.BLUETOOTH_CONNECT,
                    ),
                    1
                )
            }
        } else {
            // 如果是 Android 6 到 Android 11，則請求 ACCESS_FINE_LOCATION 權限
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    1
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // 請求 BLUETOOTH_SCAN 和 BLUETOOTH_CONNECT 權限
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    1
                )
            }
        } else {
            // 如果是 Android 6 到 Android 11，則請求 ACCESS_FINE_LOCATION 權限
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        }
    }

    fun startWifiScan() {
        withDefault("startBTScan") {
            val job = withDefault("wait for service ready") {
                while (wifiService == null) {
                    delay(1000)
                }
            }
            job.join()
            wifiService?.startScan()
            if (wifiService?.isEnable() != true) {
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "請開啟Wifi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun stopWifiScan() {
        wifiService?.stopScan()
    }

    fun startBluetoothScan() {
        withDefault("startBTScan") {
            val job = withDefault("wait for service ready") {
                while (bluetoothService == null) {
                    delay(2000)
                }
            }
            job.join()
            bluetoothService?.startScan()
            if (bluetoothService?.isEnable() != true) {
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "請開啟藍芽", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun stopBluetoothScan() {
        bluetoothService?.stopScan()
    }

    private fun startBTService(context: Context) {
        val intent = Intent(context, BluetoothService::class.java)
        context.bindService(intent, bluetooth_connectStatus, Context.BIND_AUTO_CREATE)
        Log.d(TAG, "start service")
    }

    private fun stopBTService(context: Context) {
        Log.d(TAG, "stop service ")
        context.unbindService(bluetooth_connectStatus)
    }

    private fun startWifiService(context: Context) {
        val intent = Intent(context, WifiService::class.java)
        context.bindService(intent, wifi_connectStatus, Context.BIND_AUTO_CREATE)
        Log.d(TAG, "start service")
    }

    private fun stopWifiService(context: Context) {
        Log.d(TAG, "stop service ")
        context.unbindService(wifi_connectStatus)
    }


}