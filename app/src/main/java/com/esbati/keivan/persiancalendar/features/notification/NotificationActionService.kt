package com.esbati.keivan.persiancalendar.features.notification

import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
 * Created by Keivan Esbati on 4/9/2017.
 */

private const val EXTRA_ACTION = "extra_action"
private const val ACTION_DISMISS = 0

class NotificationActionService : IntentService("NotificationActionService") {

    override fun onHandleIntent(intent: Intent?) {
        val mActionType = intent!!.getIntExtra(EXTRA_ACTION, -1)

        if (mActionType >= 0)
            when (mActionType) {
                ACTION_DISMISS -> NotificationHelper.cancelNotification(this)
            }
    }

    companion object {
        fun getDismissAction(context: Context): PendingIntent {
            val dismissIntent = Intent(context, NotificationActionService::class.java).apply {
                putExtra(EXTRA_ACTION, ACTION_DISMISS)
            }
            return PendingIntent.getService(context, 0, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        }
    }
}
