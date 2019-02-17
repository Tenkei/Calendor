package com.esbati.keivan.persiancalendar.Features.Notification;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.esbati.keivan.persiancalendar.POJOs.CalendarDay;
import com.esbati.keivan.persiancalendar.Repository.CalendarDataStore;
import com.esbati.keivan.persiancalendar.Repository.PreferencesHelper;

import java.util.ArrayList;

/**
 * Created by Keivan Esbati on 4/10/2017.
 */

public class NotificationUpdateService extends IntentService {

    public NotificationUpdateService() {
        super("NotificationUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_SHOW, true)){
            //Recover Day Events
            CalendarDay calendarDay = new CalendarDay();
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED)
                calendarDay.mGoogleEvents = CalendarDataStore.INSTANCE.getEvents(calendarDay.mPersianDate);
            else
                calendarDay.mGoogleEvents = new ArrayList<>();
            //Show Sticky Notification
            NotificationHelper.showStickyNotification(calendarDay);
        } else {
            NotificationHelper.cancelNotification();
        }
    }
}
