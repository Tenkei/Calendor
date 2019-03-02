package com.esbati.keivan.persiancalendar.Features.Notification;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.esbati.keivan.persiancalendar.POJOs.CalendarDay;
import com.esbati.keivan.persiancalendar.Repository.PreferencesHelper;
import com.esbati.keivan.persiancalendar.Repository.Repository;

import java.util.ArrayList;

/**
 * Created by Keivan Esbati on 4/10/2017.
 */

public class NotificationUpdateService extends JobIntentService {

    private static int JOB_ID = 328;
    
    public static void enqueueUpdate(Context context){
        enqueueWork(context, NotificationUpdateService.class, JOB_ID, new Intent());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(getClass().getSimpleName(), " Handling Work");

        if(PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_SHOW, true)){
            //Recover Day Events
            CalendarDay calendarDay = new CalendarDay();
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED)
                calendarDay.mGoogleEvents = Repository.INSTANCE.getEvents(calendarDay.mPersianDate);
            else
                calendarDay.mGoogleEvents = new ArrayList<>();
            //Show Sticky Notification
            NotificationHelper.INSTANCE.showStickyNotification(this, calendarDay);
        } else {
            NotificationHelper.INSTANCE.cancelNotification(this);
        }
    }
}
