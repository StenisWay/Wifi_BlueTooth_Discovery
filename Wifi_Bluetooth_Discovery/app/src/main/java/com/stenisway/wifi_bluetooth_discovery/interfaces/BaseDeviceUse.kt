package com.stenisway.wifi_bluetooth_discovery.interfaces

interface BaseDeviceUse {

    fun connect() : Boolean

    fun disconnect() : Boolean

    fun startScan()

    fun stopScan()

}