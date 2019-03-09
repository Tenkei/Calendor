package com.esbati.keivan.persiancalendar.Features.Notification

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.JobIntentService
import android.support.v4.content.ContextCompat
import android.util.Log

import com.esbati.keivan.persiancalendar.POJOs.CalendarDay
import com.esbati.keivan.persiancalendar.Repository.PreferencesHelper
import com.esbati.keivan.persiancalendar.Repository.Repository
import ir.smartlab.persindatepicker.util.PersianCalendar

import java.util.ArrayList

/**
 * Created by Keivan Esbati on 4/10/2017.
 */

class NotificationUpdateService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        Log.d(javaClass.simpleName, "Updating notification")

        //If notification is active update it, else cancel ongoing notification
        if (PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_SHOW, true)) {
            //Recover Today events
            val today = CalendarDay(PersianCalendar())
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)
                today.mEvents = Repository.getEvents(today.mPersianDate)
            else
                today.mEvents = ArrayList()

            //Show Sticky Notification
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
