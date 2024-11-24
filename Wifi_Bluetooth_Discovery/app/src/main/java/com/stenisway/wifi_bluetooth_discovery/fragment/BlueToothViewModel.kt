package com.stenisway.wifi_bluetooth_discovery.fragment

import androidx.lifecycle.ViewModel
import com.stenisway.wifi_bluetooth_discovery.repository.blueToothList_flow
import kotlinx.coroutines.flow.map

class BlueToothViewModel : ViewModel() {

    val btList_flow = blueToothList_flow.map {
        //可以在這邊做數據操作
        it
    }

}