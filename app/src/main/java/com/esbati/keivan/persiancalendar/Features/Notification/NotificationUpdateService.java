package com.esbati.keivan.persiancalendar.Features.Notification;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.esbati.keivan.persiancalendar.POJOs.CalendarDay;
import com.esbati.keivan.persiancalendar.Repository.CalendarDataStore;
import com.esbati.keivan.persiancalendar.Repository.PreferencesHelper;

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
            calendarDay.mGoogleEvents = CalendarDataStore.getEvents(calendarDay.mPersianDate);

            //Show Sticky Notification
            NotificationHelper.showStickyNotification(calendarDay);
        } else {
            NotificationHelper.cancelNotification();
        }
    }
}
