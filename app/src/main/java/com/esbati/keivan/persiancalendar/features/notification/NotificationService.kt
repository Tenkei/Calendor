package com.esbati.keivan.persiancalendar.features.notification

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.ContextCompat
import android.util.Log
import com.esbati.keivan.persiancalendar.refactoring.CalendarManagerFactory
import com.esbati.keivan.persiancalendar.refactoring.bases.CalendarManager
import com.esbati.keivan.persiancalendar.utils.AndroidUtilities

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

    private val calendarManager: CalendarManager = CalendarManagerFactory.create()


    override fun onBind(paramIntent: Intent) = null

    override fun onCreate() {
        super.onCreate()
        Log.d(javaClass.simpleName, "Created")

        //Promote service to foreground using sticky notification
        val today = calendarManager.provideToday()
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
            if (!AndroidUtilities.isServiceRunning(NotificationService::class.java))
                ContextCompat.startForegroundService(context, Intent(context, NotificationService::class.java))
        }

        fun stopService(context: Context){
            if (AndroidUtilities.isServiceRunning(NotificationService::class.java))
                context.stopService(Intent(context, NotificationService::class.java))
        }
    }
}
