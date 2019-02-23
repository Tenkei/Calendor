package com.esbati.keivan.persiancalendar.Features.Notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.text.TextUtils

import com.esbati.keivan.persiancalendar.Features.Home.MainActivity
import com.esbati.keivan.persiancalendar.POJOs.CalendarDay
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.Repository.PreferencesHelper
import com.esbati.keivan.persiancalendar.Utils.ColorHelper
import com.esbati.keivan.persiancalendar.Utils.LanguageHelper

import java.util.Calendar

/**
 * Created by Keivan Esbati on 4/9/2017.
 */

object NotificationHelper {
    private const val STICKY_NOTIFICATION_CHANNEL_ID = "STICKY_NOTIFICATION_CHANNEL"
    private const val STICKY_NOTIFICATION_ID = 16
    private val NOTIFICATION_PRIORITY = intArrayOf(
            NotificationCompat.PRIORITY_MIN,
            NotificationCompat.PRIORITY_LOW,
            NotificationCompat.PRIORITY_DEFAULT,
            NotificationCompat.PRIORITY_HIGH,
            NotificationCompat.PRIORITY_MAX
    )


    fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_sticky_channel_name)
            val descriptionText = context.getString(R.string.notification_sticky_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(STICKY_NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showStickyNotification(context: Context, shownDay: CalendarDay) {
        //Setup Content Intent
        val intent = Intent(context, MainActivity::class.java)
        val requestId = System.currentTimeMillis().toInt() //unique requestID to differentiate between various notification with same Id
        val pIntent = PendingIntent.getActivity(context, requestId, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        //Setup Notification
        val notificationPriority = NOTIFICATION_PRIORITY[PreferencesHelper.loadInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY, 2)]
        val mBuilder = NotificationCompat.Builder(context, STICKY_NOTIFICATION_CHANNEL_ID)
                .setPriority(notificationPriority)
                .setColor(ColorHelper.getSeasonColor(shownDay.mPersianDate.persianMonth))
                .setSmallIcon(R.drawable.icon01 + shownDay.mDayNo - 1)
                .setContentIntent(pIntent)
                .setWhen(0)
                .setShowWhen(false)
                .setAutoCancel(false)
                .setOngoing(true) as NotificationCompat.Builder

        //Setup Title Text
        mBuilder.setContentTitle(
                LanguageHelper.formatStringInPersian(shownDay.mPersianDate.persianLongDate)
        )

        //Set Content Text
        if(prepareCollapsedText(context, shownDay).isNotBlank())
            mBuilder.setContentText(
                    prepareCollapsedText(context, shownDay).trim()
            )

        //If more than one event is available add an expanded Inbox Style view
        if (shownDay.mGoogleEvents != null && shownDay.mGoogleEvents.size > 1) {
            val inboxStyle = NotificationCompat.InboxStyle()
            inboxStyle.setBigContentTitle(shownDay.mPersianDate.persianLongDate)

            for (event in shownDay.mGoogleEvents)
                inboxStyle.addLine(
                        if (!TextUtils.isEmpty(event.mTITLE))
                            event.mTITLE
                        else
                            event.mDESCRIPTION
                )

            mBuilder.setStyle(inboxStyle)
        }

        //Setup Actions
        if (PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_ACTIONS, true)) {
            mBuilder.addAction(
                    R.drawable.ic_server_remove_white_24dp
                    , context.getString(R.string.notification_action_dismiss_title)
                    , NotificationActionService.getDismissAction(context)
            )
        }

        //Show Notification
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .notify(STICKY_NOTIFICATION_ID, mBuilder.build())

        //Register Alarm to Trigger in case of Broadcast Failed and Service Killed
        registerAlarm(context)
        enableReceiver(context)
    }

    fun cancelNotification(context: Context) {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .cancel(STICKY_NOTIFICATION_ID)

    }

    private fun prepareCollapsedText(context: Context, day: CalendarDay): String {
        var title = ""
        //Find an event with title
        if (day.mGoogleEvents != null)
            for (event in day.mGoogleEvents)
                if (!TextUtils.isEmpty(event.mTITLE)) {
                    title = event.mTITLE
                    break
                }

        //Adjust Content Text
        return when{
            //If an Event with Title is found, add events count if needed
            !TextUtils.isEmpty(title) && day.mGoogleEvents.size > 1 ->
                context.getString(
                        R.string.notification_collapsed_text_with_title
                        , title
                        , day.mGoogleEvents.size - 1
                )

            //If No Event with title is found just show event count if available
            TextUtils.isEmpty(title) && day.mGoogleEvents.size > 0 ->
                context.getString(
                        R.string.notification_collapsed_text_without_title
                        , day.mGoogleEvents.size
                )

            //Show title without any change
            else -> title
        }
    }

    private fun registerAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Set the alarm to start at midnight
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
    }

    private fun enableReceiver(context: Context) {
        val receiver = ComponentName(context, NotificationBroadcastReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        )
    }

    private fun disableReceiver(context: Context) {
        val receiver = ComponentName(context, NotificationBroadcastReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        )
    }
}
