package com.esbati.keivan.persiancalendar.Features.Notification

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.util.Log
import com.esbati.keivan.persiancalendar.POJOs.CalendarDay
import com.esbati.keivan.persiancalendar.Repository.Repository
import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities
import java.util.ArrayList

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

        //Recover Today events
        val today = CalendarDay()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)
            today.mGoogleEvents = Repository.getEvents(today.mPersianDate)
        else
            today.mGoogleEvents = ArrayList()

        //Promote service to foreground using sticky notification
        val notification = NotificationHelper.createStickyNotification(this, today)
        startForeground(NotificationHelper.STICKY_NOTIFICATION_ID, notification)
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
                ContextCompat.startForegroundService(context, Intent(context, ApplicationService::class.java))
        }
    }
}
