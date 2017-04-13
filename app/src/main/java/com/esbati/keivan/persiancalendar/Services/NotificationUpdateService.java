package com.esbati.keivan.persiancalendar.Services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.esbati.keivan.persiancalendar.Models.CalendarDay;
import com.esbati.keivan.persiancalendar.Utils.GoogleCalendarHelper;
import com.esbati.keivan.persiancalendar.Utils.NotificationHelper;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by Keivan Esbati on 4/10/2017.
 */

public class NotificationUpdateService extends IntentService {

    public NotificationUpdateService() {
        super("NotificationUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Recover Day Events
        CalendarDay calendarDay = new CalendarDay();
        calendarDay.mGoogleEvents = GoogleCalendarHelper.getEvents(calendarDay.mPersianDate);

        //Show Sticky Notification
        NotificationHelper.showStickyNotification(calendarDay, false);
    }
}
