package com.esbati.keivan.persiancalendar.Services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.esbati.keivan.persiancalendar.Models.CalendarCell;
import com.esbati.keivan.persiancalendar.Utils.CalendarHelper;
import com.esbati.keivan.persiancalendar.Utils.NotificationHelper;
import com.esbati.keivan.persiancalendar.Utils.PreferencesHelper;

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
            CalendarCell calendarDay = new CalendarCell();
            calendarDay.mCalendarEvents = CalendarHelper.getEventsOfDay(calendarDay.mPersianDate);

            //Show Sticky Notification
            NotificationHelper.showStickyNotification(calendarDay);
        } else {
            NotificationHelper.cancelNotification();
        }
    }


}
