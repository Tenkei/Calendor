package com.esbati.keivan.persiancalendar.features.notification

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import com.esbati.keivan.persiancalendar.BuildConfig

import com.esbati.keivan.persiancalendar.features.home.MainActivity
import com.esbati.keivan.persiancalendar.pojos.CalendarDay
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.repository.PreferencesHelper
import com.esbati.keivan.persiancalendar.utils.ColorHelper
import com.esbati.keivan.persiancalendar.utils.LanguageHelper

/**
 * Created by Keivan Esbati on 4/9/2017.
 */

object NotificationHelper {
    const val STICKY_NOTIFICATION_ID = 16
    private const val STICKY_NOTIFICATION_CHANNEL_ID = "STICKY_NOTIFICATION_CHANNEL"
    private val NOTIFICATION_PRIORITY = intArrayOf(
            NotificationCompat.PRIORITY_MIN,
            NotificationCompat.PRIORITY_LOW,
            NotificationCompat.PRIORITY_DEFAULT,
            NotificationCompat.PRIORITY_HIGH,
            NotificationCompat.PRIORITY_MAX
    )

    fun createNotificationChannelIfRequired(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_sticky_channel_name)
            val descriptionText = context.getString(R.string.notification_sticky_channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
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

    @JvmStatic
    @TargetApi(26)
    fun getChannelImportance(context: Context): Int {
        val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.getNotificationChannel(STICKY_NOTIFICATION_CHANNEL_ID).importance
    }

    @JvmStatic
    @TargetApi(26)
    fun openChannelSetting(context: Context) {
        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID)
            putExtra(Settings.EXTRA_CHANNEL_ID, NotificationHelper.STICKY_NOTIFICATION_CHANNEL_ID)
        }
        context.startActivity(intent)
    }

    fun showStickyNotification(context: Context, shownDay: CalendarDay) {
        val notification = createStickyNotification(context, shownDay)

        //Show Notification
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .notify(STICKY_NOTIFICATION_ID, notification )

        NotificationService.startService(context)
    }

    fun createStickyNotification(context: Context, shownDay: CalendarDay): Notification {
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
        if (shownDay.mEvents.size > 1) {
            val inboxStyle = NotificationCompat.InboxStyle()
            inboxStyle.setBigContentTitle(shownDay.mPersianDate.persianLongDate)

            for (event in shownDay.mEvents)
                inboxStyle.addLine(
                        if (!TextUtils.isEmpty(event.title))
                            event.title
                        else
                            event.description
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

        return mBuilder.build()
    }

    private fun prepareCollapsedText(context: Context, day: CalendarDay): String {
        var title = ""
        //Find an event with title
        for (event in day.mEvents)
            if (!TextUtils.isEmpty(event.title)) {
                title = event.title!!
                break
            }

        //Adjust Content Text
        return when {
            //If an Event with Title is found, add events count if needed
            !TextUtils.isEmpty(title) && day.mEvents.size > 1 ->
                context.getString(
                        R.string.notification_collapsed_text_with_title
                        , title
                        , day.mEvents.size - 1
                )

            //If No Event with title is found just show event count if available
            TextUtils.isEmpty(title) && day.mEvents.size > 0 ->
                context.getString(
                        R.string.notification_collapsed_text_without_title
                        , day.mEvents.size
                )

            //Show title without any change
            else -> title
        }
    }

    fun cancelNotification(context: Context) {
        NotificationService.stopService(context)
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .cancel(STICKY_NOTIFICATION_ID)
    }
}
