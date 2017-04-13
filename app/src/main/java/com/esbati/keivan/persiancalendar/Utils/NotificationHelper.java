package com.esbati.keivan.persiancalendar.Utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.esbati.keivan.persiancalendar.Activities.MainActivity;
import com.esbati.keivan.persiancalendar.Controllers.ApplicationController;
import com.esbati.keivan.persiancalendar.Models.CalendarDay;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Services.NotificationActionService;
import com.esbati.keivan.persiancalendar.Services.NotificationUpdateService;

import java.util.Calendar;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by Keivan Esbati on 4/9/2017.
 */

public class NotificationHelper {
    public static final int NOTIFICATION_ID = 666;

    public static void showStickyNotification(CalendarDay calendar, boolean registerAlarm){
        //Setup Content Intent
        Intent intent = new Intent(ApplicationController.getContext(), MainActivity.class);
        int requestId = (int)System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getActivity(ApplicationController.getContext(), requestId, intent, flags);

        //Setup Notification
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(ApplicationController.getContext())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ColorHelper.getSeasonColor(calendar.mPersianDate.getPersianMonth()))
                .setSmallIcon(R.drawable.icon01 + calendar.mDayNo - 1)
                .setContentTitle(calendar.mPersianDate.getPersianLongDate())
                //FIXME Remove This Notification on Release
                //.setContentText(new PersianCalendar(System.currentTimeMillis()).getPersianLongDateAndTime())
                //.setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pIntent)
                .setShowWhen(false)
                .setAutoCancel(false)
                .setOngoing(true);

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
            if(calendar.mGoogleEvents.size() > 1)
                collapsedText += " و " + (calendar.mGoogleEvents.size() - 1) + " رویداد دیگر";

            mBuilder.setContentText(collapsedText.trim());
        }

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
        Intent dismissIntent = new Intent(ApplicationController.getContext(), NotificationActionService.class);
        dismissIntent.putExtra(NotificationActionService.EXTRA_ACTION, NotificationActionService.ACTION_DISMISS);
        PendingIntent pDismissIntent = PendingIntent.getService(ApplicationController.getContext(), 0, dismissIntent, flags);
        mBuilder.addAction(R.drawable.ic_server_remove_white_24dp, "Dismiss", pDismissIntent);
                //.addAction(R.drawable.ic_calendar_plus_white_24dp, "Add Event", pDismissIntent);

        //Show Notification
        NotificationManager mNotificationManager = (NotificationManager) ApplicationController.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        if(registerAlarm)
            keepUpdated(ApplicationController.getContext());
    }

    private static void keepUpdated(Context context){
        //Set Alarm to Update Notification
        Intent updateIntent = new Intent(context, NotificationUpdateService.class);
        PendingIntent pUpdateIntent = PendingIntent.getService(context, 0, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pUpdateIntent);
    }
}
