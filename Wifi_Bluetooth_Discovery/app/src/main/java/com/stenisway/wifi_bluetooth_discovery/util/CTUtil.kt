package com.stenisway.wifi_bluetooth_discovery.util

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


fun withIO(TAG : String, func : suspend () -> Unit) : Job{
    val handler = CoroutineExceptionHandler{_, t ->
        Log.e(TAG, t.message.toString())
    }
    return CoroutineScope(Dispatchers.IO + handler).launch {
        func()
    }
}

fun withDefault(TAG : String, func : suspend () -> Unit) : Job{
    val handler = CoroutineExceptionHandler{_, t ->
        Log.e(TAG, t.message.toString())
    }
    return CoroutineScope(Dispatchers.Default + handler).launch {
        func()
    }
}