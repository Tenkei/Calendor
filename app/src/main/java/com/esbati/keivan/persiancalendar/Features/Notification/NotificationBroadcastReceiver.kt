package com.esbati.keivan.persiancalendar.Features.Notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities

/**
 * Created by Keivan Esbati on 4/10/2017.
 */

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val event = intent.action
        Log.d(javaClass.simpleName, "Received intent with action " + event!!)

        //Update Notification
        NotificationUpdateService.enqueueUpdate(context)

        //Start Application Service if Not Running
        ApplicationService.startService(context)
    }
}
