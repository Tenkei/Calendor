package com.esbati.keivan.persiancalendar.features.notification

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.util.Log
import com.esbati.keivan.persiancalendar.repository.PreferencesHelper
import com.esbati.keivan.persiancalendar.repository.Repository

/**
 * Created by Keivan Esbati on 4/10/2017.
 */

class NotificationUpdateService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        Log.d(javaClass.simpleName, "Updating notification")

        //If notification is active update it, else cancel ongoing notification
        if (PreferencesHelper.shouldShowNotification) {
            //Show Sticky Notification
            val today = Repository.getToday()
            NotificationHelper.showStickyNotification(this, today)
        } else {
            NotificationHelper.cancelNotification(this)
        }
    }

    companion object {
        private const val JOB_ID = 328

        fun enqueueUpdate(context: Context) {
            JobIntentService.enqueueWork(context, NotificationUpdateService::class.java, JOB_ID, Intent())
        }
    }
}
