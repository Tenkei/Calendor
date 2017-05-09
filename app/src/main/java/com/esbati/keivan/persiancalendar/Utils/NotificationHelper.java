package com.esbati.keivan.persiancalendar.Utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.esbati.keivan.persiancalendar.Activities.MainActivity;
import com.esbati.keivan.persiancalendar.Controllers.ApplicationController;
import com.esbati.keivan.persiancalendar.Models.CalendarDay;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Receivers.NotificationBroadcastReceiver;
import com.esbati.keivan.persiancalendar.Services.NotificationActionService;

import java.util.Calendar;

/**
 * Created by Keivan Esbati on 4/9/2017.
 */

public class NotificationHelper {
    public static final int NOTIFICATION_ID = 666;
    public static final int[] NOTIFICATION_PRIORITY = {
            NotificationCompat.PRIORITY_MIN
            , NotificationCompat.PRIORITY_LOW
            , NotificationCompat.PRIORITY_DEFAULT
            , NotificationCompat.PRIORITY_HIGH
            , NotificationCompat.PRIORITY_MAX
    };

    public static void showStickyNotification(CalendarDay calendar){

        Context context = ApplicationController.getContext();

        //Setup Content Intent
        Intent intent = new Intent(context, MainActivity.class);
        int requestId = (int)System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getActivity(context, requestId, intent, flags);


        //Setup Notification
        int notificationPriority = NOTIFICATION_PRIORITY[PreferencesHelper.loadInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY, 2)];
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setPriority(notificationPriority)
                .setColor(ColorHelper.getSeasonColor(calendar.mPersianDate.getPersianMonth()))
                .setSmallIcon(R.drawable.icon01 + calendar.mDayNo - 1)
                //.setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pIntent)
                .setWhen(0)
                .setShowWhen(false)
                .setAutoCancel(false)
                .setOngoing(true);

        //Setup Title Text
        String mTitle = LanguageHelper.formatStringInPersian(calendar.mPersianDate.getPersianLongDate());
        mBuilder.setContentTitle(mTitle);

        //Set Content Text
        String collapsedText = "";
        if(calendar.mGoogleEvents != null)
            //Find an Event with Title
            for(int i = 0 ; i < calendar.mGoogleEvents.size() ; i++)
                if(!TextUtils.isEmpty(calendar.mGoogleEvents.get(i).mTITLE)){
                    collapsedText += calendar.mGoogleEvents.get(i).mTITLE ;
                    break;
                }

        //Adjust Content Text
        if(!TextUtils.isEmpty(collapsedText)){
            //If an Event with Title is Found and R est of Events Count
            if(calendar.mGoogleEvents.size() > 1)
                collapsedText += " و " + (calendar.mGoogleEvents.size() - 1) + " رویداد دیگر";
        } else {
            //If No Event with Title is Found just Add Events Count
            if(calendar.mGoogleEvents.size() > 0)
                collapsedText = calendar.mGoogleEvents.size() + " رویداد";
        }
        mBuilder.setContentText(collapsedText.trim());

        //If Events are more than One Create Expanded Inbox Style View
        if(calendar.mGoogleEvents != null && calendar.mGoogleEvents.size() > 1){
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            String[] events = new String[calendar.mGoogleEvents.size()];

            for(int i = 0 ; i < calendar.mGoogleEvents.size() ; i++)
                events[i] = !TextUtils.isEmpty(calendar.mGoogleEvents.get(i).mTITLE) ? calendar.mGoogleEvents.get(i).mTITLE : calendar.mGoogleEvents.get(i).mDESCRIPTION;

            inboxStyle.setBigContentTitle(calendar.mPersianDate.getPersianLongDate());
            for (int i = 0 ; i < events.length ; i++)
                inboxStyle.addLine(events[i]);

            mBuilder.setStyle(inboxStyle);
        }

        //Setup Actions
        if(PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_ACTIONS, true)){
            Intent dismissIntent = new Intent(context, NotificationActionService.class);
            dismissIntent.putExtra(NotificationActionService.EXTRA_ACTION, NotificationActionService.ACTION_DISMISS);
            PendingIntent pDismissIntent = PendingIntent.getService(context, 0, dismissIntent, flags);
            mBuilder.addAction(R.drawable.ic_server_remove_white_24dp, "Dismiss", pDismissIntent);
                    //.addAction(R.drawable.ic_calendar_plus_white_24dp, "Add Event", pDismissIntent);
        }

        //Show Notification
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        //Register Alarm to Trigger in case of Broadcast Failed and Service Killed
        registerAlarm(context);
        enableReceiver(context);
    }

    public static void cancelNotification(){
        NotificationManager mNotificationManager = (NotificationManager) ApplicationController.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
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
