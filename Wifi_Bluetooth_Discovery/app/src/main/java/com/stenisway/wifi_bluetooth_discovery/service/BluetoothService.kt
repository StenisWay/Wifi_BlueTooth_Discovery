package com.stenisway.wifi_bluetooth_discovery.service

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import com.stenisway.wifi_bluetooth_discovery.interfaces.BaseDeviceUse
import com.stenisway.wifi_bluetooth_discovery.model.BTItem
import com.stenisway.wifi_bluetooth_discovery.repository.submitBluetoothList
import com.stenisway.wifi_bluetooth_discovery.util.withDefault
import com.stenisway.wifi_bluetooth_discovery.util.withIO
import kotlinx.coroutines.delay

class BluetoothService : Service(), BaseDeviceUse {

    private val TAG = "Bluetooth"

    private val btList = mutableListOf<BTItem>()

    private lateinit var bluetoothAdapter: BluetoothAdapter

    private var isDiscovery : Boolean = false

    fun isEnable() : Boolean = bluetoothAdapter.isEnabled

    override fun onBind(intent: Intent): IBinder {
        init()
        Log.d(TAG, "Service Bind")
        return LocalBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopScan()
        this.unregisterReceiver(deviceBroadcast)
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BTService onCreate")
        init()
    }

    private fun init() {
        bluetoothAdapter =
            (this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        val intent = IntentFilter()
        intent.addAction(BluetoothDevice.ACTION_FOUND)
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intent.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        if (Build.VERSION.SDK_INT >= 33) {
            this.registerReceiver(deviceBroadcast, intent, RECEIVER_EXPORTED)
        } else {
            this.registerReceiver(deviceBroadcast, intent)
        }
    }

    private val deviceBroadcast: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            action?.let { ac ->
                when (ac) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val devicex = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE,
                                BluetoothDevice::class.java
                            )
                        } else {
                            intent.getParcelableExtra<Parcelable>(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice?
                        }

                        val rssi = intent.getShortExtra(
                            BluetoothDevice.EXTRA_RSSI,
                            Short.MIN_VALUE
                        )
                        devicex?.let { bt ->
                            rssi.let { rs ->
                                Log.d(TAG, "onReceive: found ${bt.name}")
                                val name = if (bt.name == null){
                                    "N/A"
                                }else{
                                    bt.name
                                }
                                btList.add(BTItem(name, bt.address, rs))
                            }
                        }
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        if (isDiscovery) {
                            Log.d(TAG, "StartNextDiscovery ")
                            withDefault(TAG){
                                val list = btList.sortedByDescending {
                                    it.bt_rssi
                                }
                                withIO(TAG) {
                                    submitBluetoothList(list)
                                }
                                btList.clear()
                                isDiscovery = false
                                delay(2000)
                                this@BluetoothService.startScan()
                            }
                        } else {
                            Log.d(TAG, "DiscoveryFinish")
                        }
                    }

                    BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                        val connectionState = intent.getIntExtra(
                            BluetoothAdapter.EXTRA_CONNECTION_STATE,
                            BluetoothAdapter.ERROR
                        )
                        when (connectionState) {
                            BluetoothAdapter.STATE_CONNECTED -> {
                                // 藍牙已連接
                                Log.d(TAG, "Bluetooth device connected")
                            }

                            BluetoothAdapter.STATE_DISCONNECTED -> {
                                // 藍牙已斷開
                                Log.d(TAG, "Bluetooth device disconnected")
                            }

                            else -> {

                            }
                        }

                    }

                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        val state =
                            intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                        when (state) {
                            BluetoothAdapter.STATE_OFF -> {
                                Log.d("BluetoothStateReceiver", "Bluetooth is OFF")
                                this@BluetoothService.stopScan()
                            }

                            BluetoothAdapter.STATE_TURNING_OFF -> {
                                Log.d("BluetoothStateReceiver", "Bluetooth is TURNING OFF")
                            }

                            BluetoothAdapter.STATE_ON -> {
                                Log.d("BluetoothStateReceiver", "Bluetooth is ON")
                                if (bluetoothAdapter.isEnabled){
                                    this@BluetoothService.startScan()
                                } else {

                                }
                            }

                            BluetoothAdapter.STATE_TURNING_ON -> {
                                Log.d("BluetoothStateReceiver", "Bluetooth is TURNING ON")
                            }

                            else -> {}
                        }
                    }

                    else -> {

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

    inner class LocalBinder : Binder() {

        fun getService(): BluetoothService = this@BluetoothService

    }

    @SuppressLint("MissingPermission")
    override fun startScan() {
        Log.d(TAG, "start discovery bt")
        isDiscovery = true
        bluetoothAdapter.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        isDiscovery = false
        bluetoothAdapter.cancelDiscovery()
    }
}