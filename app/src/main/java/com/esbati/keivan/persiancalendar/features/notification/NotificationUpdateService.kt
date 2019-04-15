package com.esbati.keivan.persiancalendar.features.notification

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.util.Log
import com.esbati.keivan.persiancalendar.components.ServiceLocator
import com.esbati.keivan.persiancalendar.repository.PreferencesHelper

/**
 * Created by Keivan Esbati on 4/10/2017.
 */

class NotificationUpdateService : JobIntentService() {

    private val repository by lazy { ServiceLocator.instance(this).getRepository() }

    override fun onHandleWork(intent: Intent) {
        Log.d(javaClass.simpleName, "Updating notification")

        //If notification is active update it, else cancel ongoing notification
        if (PreferencesHelper.shouldShowNotification) {
            //Show Sticky Notification
            val today = repository.getToday()
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
