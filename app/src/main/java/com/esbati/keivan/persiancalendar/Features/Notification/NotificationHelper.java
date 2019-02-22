package com.esbati.keivan.persiancalendar.Features.Notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.esbati.keivan.persiancalendar.Components.ApplicationController;
import com.esbati.keivan.persiancalendar.Features.Home.MainActivity;
import com.esbati.keivan.persiancalendar.POJOs.CalendarDay;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Repository.PreferencesHelper;
import com.esbati.keivan.persiancalendar.Utils.ColorHelper;
import com.esbati.keivan.persiancalendar.Utils.LanguageHelper;

import java.util.Calendar;

/**
 * Created by Keivan Esbati on 4/9/2017.
 */

public class NotificationHelper {
    private static final int STICKY_NOTIFICATION_ID = 666;
    private static final int[] NOTIFICATION_PRIORITY = {
            NotificationCompat.PRIORITY_MIN
            , NotificationCompat.PRIORITY_LOW
            , NotificationCompat.PRIORITY_DEFAULT
            , NotificationCompat.PRIORITY_HIGH
            , NotificationCompat.PRIORITY_MAX
    };

    public static void showStickyNotification(CalendarDay shownDay){
        Context context = ApplicationController.getContext();

        //Setup Content Intent
        Intent intent = new Intent(context, MainActivity.class);
        int requestId = (int)System.currentTimeMillis(); //unique requestID to differentiate between various notification with same Id
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getActivity(context, requestId, intent, flags);


        //Setup Notification
        int notificationPriority = NOTIFICATION_PRIORITY[PreferencesHelper.loadInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY, 2)];
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setPriority(notificationPriority)
                .setColor(ColorHelper.getSeasonColor(shownDay.mPersianDate.getPersianMonth()))
                .setSmallIcon(R.drawable.icon01 + shownDay.mDayNo - 1)
                .setContentIntent(pIntent)
                .setWhen(0)
                .setShowWhen(false)
                .setAutoCancel(false)
                .setOngoing(true);

        //Setup Title Text
        String mTitle = LanguageHelper.formatStringInPersian(shownDay.mPersianDate.getPersianLongDate());
        mBuilder.setContentTitle(mTitle);

        //Set Content Text
        String collapsedText = "";
        if(shownDay.mGoogleEvents != null)
            //Find an Event with Title
            for(int i = 0 ; i < shownDay.mGoogleEvents.size() ; i++)
                if(!TextUtils.isEmpty(shownDay.mGoogleEvents.get(i).mTITLE)){
                    collapsedText += shownDay.mGoogleEvents.get(i).mTITLE ;
                    break;
                }

        //Adjust Content Text
        if(!TextUtils.isEmpty(collapsedText)){
            //If an Event with Title is Found and R est of Events Count
            if(shownDay.mGoogleEvents.size() > 1)
                collapsedText += " و " + (shownDay.mGoogleEvents.size() - 1) + " رویداد دیگر";
        } else {
            //If No Event with Title is Found just Add Events Count
            if(shownDay.mGoogleEvents.size() > 0)
                collapsedText = shownDay.mGoogleEvents.size() + " رویداد";
        }
        mBuilder.setContentText(collapsedText.trim());

        //If Events are more than One Create Expanded Inbox Style View
        if(shownDay.mGoogleEvents != null && shownDay.mGoogleEvents.size() > 1){
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            String[] events = new String[shownDay.mGoogleEvents.size()];

            for(int i = 0 ; i < shownDay.mGoogleEvents.size() ; i++)
                events[i] = !TextUtils.isEmpty(shownDay.mGoogleEvents.get(i).mTITLE) ? shownDay.mGoogleEvents.get(i).mTITLE : shownDay.mGoogleEvents.get(i).mDESCRIPTION;

            inboxStyle.setBigContentTitle(shownDay.mPersianDate.getPersianLongDate());
            for (int i = 0 ; i < events.length ; i++)
                inboxStyle.addLine(events[i]);

            mBuilder.setStyle(inboxStyle);
        }

        //Setup Actions
        if(PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_ACTIONS, true)){
            mBuilder.addAction(R.drawable.ic_server_remove_white_24dp, "Dismiss", NotificationActionService.Companion.getDismissAction(context));
        }

        //Show Notification
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(STICKY_NOTIFICATION_ID, mBuilder.build());

        //Register Alarm to Trigger in case of Broadcast Failed and Service Killed
        registerAlarm(context);
        enableReceiver(context);
    }

    public static void cancelNotification(){
        ((NotificationManager) ApplicationController
                .getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(STICKY_NOTIFICATION_ID);

    }

    private static void registerAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm to start at midnight
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP
                , calendar.getTimeInMillis()
                , AlarmManager.INTERVAL_DAY
                , alarmIntent);
    }

    private static void enableReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, NotificationBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private static void disableReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, NotificationBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
