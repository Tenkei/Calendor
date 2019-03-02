package com.esbati.keivan.persiancalendar.Features.Notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by Keivan Esbati on 4/10/2017.
 */

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(javaClass.simpleName, "Received intent with action $action")

        when(action){
            Intent.ACTION_BOOT_COMPLETED -> ApplicationService.startService(context)

            // In case of following
            // Intent.ACTION_DATE_CHANGED
            // Intent.ACTION_TIME_CHANGED
            // Intent.ACTION_TIMEZONE_CHANGED
            // Intent.ACTION_TIME_TICK
            else -> NotificationUpdateService.enqueueUpdate(context)
        }
    }
}
