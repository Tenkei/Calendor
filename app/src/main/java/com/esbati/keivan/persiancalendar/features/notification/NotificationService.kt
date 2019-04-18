package com.esbati.keivan.persiancalendar.features.notification

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.ContextCompat
import android.util.Log
import com.esbati.keivan.persiancalendar.components.locate
import com.esbati.keivan.persiancalendar.repository.Repository

/**
 * Created by Keivan Esbati on 5/2/2017.
 */

class NotificationService : Service() {

    private val repository: Repository by locate()
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
        val today = repository.getToday()
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
            if (!isServiceRunning(context))
                ContextCompat.startForegroundService(context, Intent(context, NotificationService::class.java))
        }

        fun stopService(context: Context){
            if (isServiceRunning(context))
                context.stopService(Intent(context, NotificationService::class.java))
        }

        fun isServiceRunning(context: Context): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Integer.MAX_VALUE))
                if (NotificationService::class.java.name == service.service.className)
                    return true

            return false
        }
    }
}
