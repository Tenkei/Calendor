package com.esbati.keivan.persiancalendar.Repository;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.esbati.keivan.persiancalendar.Components.ApplicationController;
import com.esbati.keivan.persiancalendar.POJOs.GoogleCalendar;
import com.esbati.keivan.persiancalendar.POJOs.GoogleEvent;
import com.esbati.keivan.persiancalendar.R;

import java.util.ArrayList;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/23/2016.
 */

public class CalendarDataStore {

    private static ArrayList<GoogleCalendar> mGCalendars;
    private static ArrayList<GoogleEvent> mGEvents;

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    private static final String[] CALENDAR_PROJECTION = new String[] {
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };

    private static final String[] EVENT_PROJECTION = new String[] {
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

    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    public static void init(){
        mGCalendars = getCalendars();

        mGEvents = new ArrayList<>();
        for(GoogleCalendar gCalendar:mGCalendars)
            mGEvents.addAll(getEvents(gCalendar));
    }

    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    private static ArrayList<GoogleCalendar> getCalendars(){
        ArrayList<GoogleCalendar> calendars = new ArrayList<>();

        // Run query
        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        Cursor cur = cr.query(Calendars.CONTENT_URI, CALENDAR_PROJECTION, null, null, null);

        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            GoogleCalendar gCalendar = new GoogleCalendar();

            // Get the field values
            gCalendar.calID = cur.getLong(PROJECTION_ID_INDEX);
            gCalendar.displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            gCalendar.accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            gCalendar.ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            calendars.add(gCalendar);
        }
        cur.close();

        return calendars;
    }

    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    private static ArrayList<GoogleEvent> getEvents(GoogleCalendar calendar){
        ArrayList<GoogleEvent> events = new ArrayList<>();

        // Submit the query and get a Cursor object back.
        String selection = "((" + Events.CALENDAR_ID + " = ?))";
        String[] selectionArgs = new String[] {"" + calendar.calID};

        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        Cursor cur = cr.query(Events.CONTENT_URI, EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            GoogleEvent gEvent = new GoogleEvent();
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

            events.add(gEvent);
        }
        cur.close();

        return events;
    }

    public static ArrayList<GoogleEvent> getEvents(PersianCalendar selectedDate) {
        ArrayList<GoogleEvent> selectedGoogleEvents = new ArrayList<>();

        if(mGEvents != null)
            for (GoogleEvent gEvent : mGEvents)
                if (selectedDate.equals(gEvent.mStartDate))
                    selectedGoogleEvents.add(gEvent);

        return selectedGoogleEvents;
    }

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    public static int saveEvent(GoogleEvent event){
        //Return if No Calendar is Available
        if(mGCalendars == null || mGCalendars.size() <= 0)
            return -1;

        return event.mID != 0
                ? updateEvent(event)
                : saveSimpleEvent(event);
    }

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    private static int saveSimpleEvent(GoogleEvent newEvent){
        GoogleCalendar defaultCalendar = mGCalendars.get(0);

        ContentValues values = new ContentValues();
        values.put(Events.CALENDAR_ID, defaultCalendar.calID);
        values.put(Events.TITLE, newEvent.mTITLE);
        values.put(Events.DESCRIPTION, newEvent.mDESCRIPTION);
        values.put(Events.DTSTART, newEvent.mDTSTART);
        values.put(Events.DTEND, newEvent.mDTSTART + 1000 * 60 * 60);
        values.put(Events.EVENT_TIMEZONE, new PersianCalendar().getTimeZone().getDisplayName());

        // Submit the query and get a Cursor object back.
        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        Uri uri = cr.insert(Events.CONTENT_URI, values);

        //Get the event ID and Add it to Google Events Pool
        newEvent.mID = Long.parseLong(uri.getLastPathSegment());
        mGEvents.add(newEvent);

        return 1;
    }

    private static int updateEvent(GoogleEvent gEvent){
        //Update Event Row
        ContentValues values = new ContentValues();
        values.put(Events.TITLE, gEvent.mTITLE);
        values.put(Events.DESCRIPTION, gEvent.mDESCRIPTION);

        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, gEvent.mID);
        int rows = cr.update(updateUri, values, null, null);

        //Update Google Events Pool
        for(GoogleEvent event:mGEvents)
            if(gEvent.mID == event.mID){
                event.mTITLE = gEvent.mTITLE;
                event.mDESCRIPTION = gEvent.mDESCRIPTION;
                break;
            }

        return rows;
    }

    public static int deleteEvent(GoogleEvent gEvent){
        //Delete Event Row
        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, gEvent.mID);
        int rows = cr.delete(deleteUri, null, null);

        //Remove From Google Events Pool
        mGEvents.remove(gEvent);

        return rows;
    }
}
