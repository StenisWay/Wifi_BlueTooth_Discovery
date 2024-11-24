package com.stenisway.wifi_bluetooth_discovery.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun LocalDateTime.toTimeString() : String{
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    return this.format(formatter)
}