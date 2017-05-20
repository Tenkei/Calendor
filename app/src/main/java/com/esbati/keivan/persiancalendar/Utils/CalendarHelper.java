package com.esbati.keivan.persiancalendar.Utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.esbati.keivan.persiancalendar.Controllers.ApplicationController;
import com.esbati.keivan.persiancalendar.Models.Calendar;
import com.esbati.keivan.persiancalendar.Models.CalendarEvent;
import com.esbati.keivan.persiancalendar.R;

import java.util.ArrayList;
import java.util.TimeZone;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/23/2016.
 */

public class CalendarHelper {

    private static ArrayList<Calendar> mCalendars;
    private static ArrayList<CalendarEvent> mCalendarEvents;

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public static final String[] CALENDAR_PROJECTION = new String[] {
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };

    public static final String[] EVENT_PROJECTION = new String[] {
            Events._ID,                           // 0
            Events.ORGANIZER,                     // 1
            Events.TITLE,                         // 2
            Events.EVENT_LOCATION,                // 3
            Events.DESCRIPTION,                   // 4
            Events.DTSTART,                       // 5
            Events.DTEND,                         // 6
            Events.EVENT_TIMEZONE,                // 7
            Events.EVENT_END_TIMEZONE,            // 8
            Events.DURATION,                      // 9
            Events.ALL_DAY,                       // 10
            Events.RRULE,                         // 11
            Events.RDATE,                         // 12
    };

    public static void initCalendars(){
        //Re-Initialize Singletons
        mCalendars = null;
        mCalendarEvents = null;

        //Get Phone Calendars
        ArrayList<Calendar> calendars = getCalendars();

        //Check for App Calendar
        Calendar appCalendar = null;
        boolean appCalendarExists = false;
        int writeCalendarId = PreferencesHelper.loadInt(PreferencesHelper.KEY_WRITE_CALENDAR_ID, -1);
        if(calendars != null)
            for(Calendar calendar: calendars)
                if(calendar.displayName.equalsIgnoreCase("Calendor")){
                    //If No Default Write Calendar is Selected, Select the App Calendar
                    if(writeCalendarId < 0)
                        PreferencesHelper.saveInt(PreferencesHelper.KEY_WRITE_CALENDAR_ID, (int)calendar.calID);

                    appCalendarExists = true;
                    //removeCalendar(calendar);
                    break;
                }

        //If Not Available Create Calendar and Add it to Calendar Pool
        //if(true){
        if(!appCalendarExists){
            appCalendar = createNewCalendar();
            if(appCalendar != null && appCalendar.calID >= 0){
                PreferencesHelper.saveInt(PreferencesHelper.KEY_WRITE_CALENDAR_ID, (int)appCalendar.calID);
                if(mCalendars != null)
                    mCalendars.add(0, appCalendar);
            }
        }

        //If No Default Write Calendar is Selected, Select the First Calendar Available
        if(writeCalendarId < 0 && calendars != null && calendars.size() > 0)
            PreferencesHelper.saveInt(PreferencesHelper.KEY_WRITE_CALENDAR_ID, (int)calendars.get(0).calID);
    }

    public static Calendar createNewCalendar(){
        //Return if Permission is Not Granted
        if(ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            return null;

        //Create Calendar
        Calendar newCalendar = new Calendar("Calendor");

        /*
        AccountManager manager = AccountManager.get(ApplicationController.getContext());
        Account[] accounts = manager.getAccountsByType("com.google");

        String accountName = "";
        String accountType = "";

        for (Account account : accounts) {
            //accountName = account.name;
            accountName = "test";
            accountType = account.type;
            break;
        }
        */

        //Set Calendar Attributes
        ContentValues values = new ContentValues();

        //values.put(CalendarContract.Calendars.ACCOUNT_NAME, "Calendor");
        //values.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        //values.put(CalendarContract.Calendars.NAME, "Calendor");
        //values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "Calendor");
        //values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        //values.put(CalendarContract.Calendars.VISIBLE, 1);
        //values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        //values.put(CalendarContract.Calendars.OWNER_ACCOUNT, "Calendor");
        //values.put(CalendarContract.Calendars.DIRTY, 1);
        //values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());

        values.put(CalendarContract.Calendars.ACCOUNT_NAME, newCalendar.accountName);
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(CalendarContract.Calendars.NAME, newCalendar.displayName);
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, newCalendar.displayName);
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, ApplicationController.getContext().getResources().getColor(R.color.colorPrimary));
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, true);
        values.put(CalendarContract.Calendars.VISIBLE, 1);
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);


        Uri calUri = CalendarContract.Calendars.CONTENT_URI;

        calUri = calUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, newCalendar.accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();

        Uri result = null;
        try{
            result = ApplicationController.getContext().getContentResolver().insert(calUri, values);
        } catch (Exception e){
            e.printStackTrace();
            Log.e("CalendarHelper", e.getMessage(), e);
            Crashlytics.logException(e);
        }

        //Get the Calendar ID from Result
        if(result != null)
            newCalendar.calID = Long.parseLong(result.getLastPathSegment());

        return newCalendar;
    }

    public static boolean removeCalendar(Calendar calendar){
        Uri.Builder builder = Calendars.CONTENT_URI.buildUpon();
        builder.appendPath("" + calendar.calID)
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, calendar.accountName)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        //.appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.google");

        Uri uri = builder.build();
        int result = ApplicationController.getContext().getContentResolver().delete(uri, null, null);

        if(result == 1)
            mCalendars.remove(calendar);

        return result == 0 ? false : true;
    }

    public static synchronized ArrayList<Calendar> getCalendars(){
        if(mCalendars != null)
            return mCalendars;

        Log.d("CalendarHelper", "Calendar Initiated!");
        mCalendars = new ArrayList<>();

        // Run query
        Cursor cur = null;
        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        Uri uri = Calendars.CONTENT_URI;

        if(ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)
            cur = cr.query(uri, CALENDAR_PROJECTION, null, null, null);
        else
            return mCalendars;

        //TODO َApply Selection
        //String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
        //        + Calendars.ACCOUNT_TYPE + " = ?) AND ("
        //        + Calendars.OWNER_ACCOUNT + " = ?))";
        //String[] selectionArgs = new String[] {"hera@example.com", "com.example",
        //        "hera@example.com"};
        //// Submit the query and get a Cursor object back.
        //cur = cr.query(uri, CALENDAR_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        if(cur != null){
            while (cur.moveToNext()) {
                Calendar gCalendar = new Calendar();

                // Get the field values
                gCalendar.calID = cur.getLong(PROJECTION_ID_INDEX);
                gCalendar.displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                gCalendar.accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                gCalendar.ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

                mCalendars.add(gCalendar);
            }

            cur.close();
        }
        return mCalendars;
    }

    public static synchronized ArrayList<CalendarEvent> getEvents(){

        if(mCalendarEvents == null) {
            ArrayList<Calendar> calendars = getCalendars();

            //Get Events of Each Calendar
            Log.d("CalendarHelper", "Calendar Events Initiated!");
            mCalendarEvents = new ArrayList<>();
            if (calendars != null)
                for (Calendar gCalendar : calendars){
                    ArrayList<CalendarEvent> calendarEvents = getEvents(gCalendar);
                    if(calendarEvents != null)
                        mCalendarEvents.addAll(calendarEvents);
                }
        }

        return mCalendarEvents;
    }

    public static ArrayList<CalendarEvent> getEvents(Calendar calendar){
        //Return if Permission is Not Granted
        if(ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            return null;

        ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();

        //TODO Add Time Limit to the URI to limit the list to events between bounds
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();

        //Set Selection
        String selection = "((" + Events.CALENDAR_ID + " = ?))";
        String[] selectionArgs = new String[] {"" + calendar.calID};

        //Submit the Query and get the Cursor Object Back
        Uri uri = Events.CONTENT_URI;
        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        Cursor cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        //Use the Cursor to Step Through the Records
        if(cur != null){
            while (cur.moveToNext()) {
                CalendarEvent gEvent = new CalendarEvent();
                gEvent.mCalendar = calendar;

                // Get the field values
                gEvent.mID = cur.getLong(0);
                gEvent.mORGANIZER = cur.getString(1);
                gEvent.mTITLE = cur.getString(2);
                gEvent.mEVENT_LOCATION = cur.getString(3);
                gEvent.mDESCRIPTION = cur.getString(4);
                gEvent.mDTSTART = cur.getLong(5);
                gEvent.mDTEND = cur.getLong(6);
                gEvent.mEVENT_TIMEZONE = cur.getString(7);
                gEvent.mEVENT_END_TIMEZONE = cur.getString(8);
                gEvent.mDURATION = cur.getString(9);
                gEvent.mALL_DAY = cur.getString(10);
                gEvent.mRRULE = cur.getString(11);
                gEvent.mRDATE = cur.getString(12);

                gEvent.mStartDate = new PersianCalendar(gEvent.mDTSTART);
                gEvent.mEndDate = new PersianCalendar(gEvent.mDTEND);

                calendarEvents.add(gEvent);
            }

            cur.close();
        }

        return calendarEvents;
    }

    public static ArrayList<CalendarEvent> getEventsOfDay(PersianCalendar date) {
        ArrayList<CalendarEvent> calendarEvents = getEvents();

        //Select Events that Match the Date
        ArrayList<CalendarEvent> selectedCalendarEvents = new ArrayList<>();
        if(calendarEvents != null)
            for (CalendarEvent gEvent : calendarEvents) {
                if (gEvent.mStartDate != null && gEvent.mStartDate.equals(date)) {
                    selectedCalendarEvents.add(gEvent);
                }
            }

        return selectedCalendarEvents;
    }

    public static int saveSimpleEvent(CalendarEvent newEvent){
        //Return if Permission is Not Granted
        if(ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            return R.string.event_error_write_permission;

        //Return if No Calendar is Available
        if(mCalendars == null || mCalendars.isEmpty())
            return R.string.event_error_no_calendar_available;

        int writeCalendarId = PreferencesHelper.loadInt(PreferencesHelper.KEY_WRITE_CALENDAR_ID, -1);
        if(writeCalendarId < 0)
            return R.string.event_error_no_calendar_selected;

        if(TextUtils.isEmpty(newEvent.mTITLE) && TextUtils.isEmpty(newEvent.mDESCRIPTION)){
            return R.string.event_error_no_content;
        }

        //Set Event Attributes
        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.CALENDAR_ID, writeCalendarId);
        values.put(Events.TITLE, newEvent.mTITLE);
        values.put(Events.DESCRIPTION, newEvent.mDESCRIPTION);
        values.put(Events.DTSTART, newEvent.mDTSTART);
        values.put(Events.DTEND, newEvent.mDTSTART + 1000 * 60 * 60);
        values.put(Events.EVENT_TIMEZONE, new PersianCalendar().getTimeZone().getDisplayName());

        //Submit the Query and get a Cursor object Back
        Uri uri = cr.insert(Events.CONTENT_URI, values);

        //Get the event ID and Add it to Google Events Pool
        if(uri != null){
            newEvent.mID = Long.parseLong(uri.getLastPathSegment());

            if(mCalendarEvents == null)
                mCalendarEvents = new ArrayList<>();

            mCalendarEvents.add(newEvent);
        }

        return R.string.event_successfully_added;
    }

    public static int updateEvent(CalendarEvent updatedEvent){
        //Return if Permission is Not Granted
        if(ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            return R.string.event_error_write_permission;

        //Update Event Attributes
        ContentValues values = new ContentValues();
        values.put(Events.TITLE, updatedEvent.mTITLE);
        values.put(Events.DESCRIPTION, updatedEvent.mDESCRIPTION);

        //Submit the Query
        Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, updatedEvent.mID);
        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        int rowId = cr.update(updateUri, values, null, null);

        //Update Google Events Pool
        if(mCalendarEvents != null)
            for(CalendarEvent event: mCalendarEvents){
                if(updatedEvent.mID == event.mID){
                    event.mTITLE = updatedEvent.mTITLE;
                    event.mDESCRIPTION = updatedEvent.mDESCRIPTION;
                    break;
                }
            }

        return R.string.event_successfully_updated;
    }

    public static int deleteEvent(CalendarEvent deletedEvent){
        //Return if Permission is Not Granted
        if(ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            return R.string.event_error_write_permission;

        //Submit the Query
        Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, deletedEvent.mID);
        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        int rowId = cr.delete(deleteUri, null, null);

        //Remove From Google Events Pool
        if(mCalendarEvents != null)
            if(mCalendarEvents.contains(deletedEvent))
                mCalendarEvents.remove(deletedEvent);

        return R.string.event_successfully_deleted;
    }
}
