package com.stenisway.wifi_bluetooth_discovery.model

import android.bluetooth.BluetoothDevice
import android.net.wifi.ScanResult

data class BTItem(val btName : String?, val btAddress : String, var bt_rssi: Short)

data class WifiItem(val wifiName : String, val wifiMac: String, var wifi_rssi : Int)