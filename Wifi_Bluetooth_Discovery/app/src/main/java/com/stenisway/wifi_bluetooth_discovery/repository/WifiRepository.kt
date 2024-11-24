package com.stenisway.wifi_bluetooth_discovery.repository

import com.stenisway.wifi_bluetooth_discovery.model.BTItem
import com.stenisway.wifi_bluetooth_discovery.model.WifiItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

private val _wifiList_flow = MutableStateFlow<List<WifiItem>?>(null)
val wifiList_flow = _wifiList_flow.asSharedFlow()

suspend fun submitWifiList(wifiList : List<WifiItem>){
    _wifiList_flow.emit(wifiList)
}