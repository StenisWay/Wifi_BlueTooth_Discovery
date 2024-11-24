package com.stenisway.wifi_bluetooth_discovery.base

import android.widget.TextView
import androidx.fragment.app.Fragment
import com.stenisway.wifi_bluetooth_discovery.util.toTimeString
import com.stenisway.wifi_bluetooth_discovery.util.withDefault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

abstract class BaseTimeFragment : Fragment() {

    private var job_time : Job? = null

    protected fun TextView.updatingNowTime(){
        if (job_time == null){
            job_time = withDefault("TimeCount"){
                while (true){
                    val now = LocalDateTime.now().toTimeString()
                    withContext(Dispatchers.Main){
                        this@updatingNowTime.text = now
                    }
                    delay(1000)
                }
            }
        }
    }

    protected fun stopTimeUpdating(){
        job_time?.cancel()
        job_time = null
    }

}