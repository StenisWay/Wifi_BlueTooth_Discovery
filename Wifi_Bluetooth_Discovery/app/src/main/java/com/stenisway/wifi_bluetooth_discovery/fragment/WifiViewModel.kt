package com.stenisway.wifi_bluetooth_discovery.fragment

import androidx.lifecycle.ViewModel
import com.stenisway.wifi_bluetooth_discovery.repository.wifiList_flow
import kotlinx.coroutines.flow.map

class WifiViewModel : ViewModel() {
    val wifiList = wifiList_flow.map {
        it
    }
}