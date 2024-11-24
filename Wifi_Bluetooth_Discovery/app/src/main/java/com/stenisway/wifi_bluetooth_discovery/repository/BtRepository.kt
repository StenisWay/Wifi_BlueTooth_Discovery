package com.stenisway.wifi_bluetooth_discovery.repository

import com.stenisway.wifi_bluetooth_discovery.adapter.BluetoothAdapter
import com.stenisway.wifi_bluetooth_discovery.model.BTItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow


private val _bluetoothList_flow = MutableStateFlow<List<BTItem>?>(null)
val blueToothList_flow = _bluetoothList_flow.asStateFlow()

suspend fun submitBluetoothList(btList : List<BTItem>){
    _bluetoothList_flow.emit(btList)
}