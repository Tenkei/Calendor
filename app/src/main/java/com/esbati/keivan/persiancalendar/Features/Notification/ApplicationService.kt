package com.esbati.keivan.persiancalendar.Features.Notification

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities

/**
 * Created by asus on 5/2/2017.
 */

class ApplicationService : Service() {
    private var broadcastReceiver = NotificationBroadcastReceiver()
    private val intentFilter = IntentFilter().apply {
        addAction(Intent.ACTION_TIME_TICK)
        addAction(Intent.ACTION_TIME_CHANGED)
        addAction(Intent.ACTION_DATE_CHANGED)
        addAction(Intent.ACTION_TIMEZONE_CHANGED)
    }

    override fun onBind(paramIntent: Intent) = null

    override fun onCreate() {
        super.onCreate()
        Log.d(javaClass.simpleName, "Created")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(javaClass.simpleName, "Started")

        registerReceiver(broadcastReceiver, intentFilter)
        return Service.START_STICKY
    }

    override fun onDestroy() {
        Log.d(javaClass.simpleName, "Destroyed")
        unregisterReceiver(broadcastReceiver)

        super.onDestroy()
    }

    companion object {

        fun startService(context: Context) {
            if (!AndroidUtilities.isServiceRunning(ApplicationService::class.java))
                context.startService(Intent(context, ApplicationService::class.java))
        }
    }
}
