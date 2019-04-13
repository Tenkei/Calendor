package com.esbati.keivan.persiancalendar.features.notification

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.ContextCompat
import android.util.Log
import com.esbati.keivan.persiancalendar.components.ApplicationController
import com.esbati.keivan.persiancalendar.repository.Repository

/**
 * Created by asus on 5/2/2017.
 */

class NotificationService : Service() {
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

        //Promote service to foreground using sticky notification
        val today = Repository.INSTANCE.getToday()
        val notification = NotificationHelper.createStickyNotification(this, today)
        startForeground(NotificationHelper.STICKY_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
            if (!isServiceRunning())
                ContextCompat.startForegroundService(context, Intent(context, NotificationService::class.java))
        }

        fun stopService(context: Context){
            if (isServiceRunning())
                context.stopService(Intent(context, NotificationService::class.java))
        }

        fun isServiceRunning(): Boolean {
            val manager = ApplicationController.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Integer.MAX_VALUE))
                if (NotificationService::class.java.name == service.service.className)
                    return true

            return false
        }
    }
}
