package com.esbati.keivan.persiancalendar.Utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.esbati.keivan.persiancalendar.ApplicationController;
import com.esbati.keivan.persiancalendar.Models.GoogleCalendar;
import com.esbati.keivan.persiancalendar.Models.GoogleEvent;
import com.esbati.keivan.persiancalendar.R;

import java.util.ArrayList;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/23/2016.
 */

public class GoogleCalendarHelper {

    private static ArrayList<GoogleCalendar> mGCalendars;
    private static ArrayList<GoogleEvent> mGEvents;

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

    public static ArrayList<GoogleCalendar> getCalendars(){
        if(mGCalendars != null)
            return mGCalendars;

        // Run query
        Cursor cur = null;
        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        Uri uri = Calendars.CONTENT_URI;

        if(ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)
            cur = cr.query(uri, CALENDAR_PROJECTION, null, null, null);
        else
            return mGCalendars;

        //String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
        //        + Calendars.ACCOUNT_TYPE + " = ?) AND ("
        //        + Calendars.OWNER_ACCOUNT + " = ?))";
        //String[] selectionArgs = new String[] {"hera@example.com", "com.example",
        //        "hera@example.com"};
        //// Submit the query and get a Cursor object back.
        //cur = cr.query(uri, CALENDAR_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        mGCalendars = new ArrayList<>();
        while (cur.moveToNext()) {
            GoogleCalendar gCalendar = new GoogleCalendar();

            // Get the field values
            gCalendar.calID = cur.getLong(PROJECTION_ID_INDEX);
            gCalendar.displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            gCalendar.accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            gCalendar.ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            mGCalendars.add(gCalendar);
        }

        return mGCalendars;
    }

    public static ArrayList<GoogleEvent> getEvents(){
        if(mGEvents == null)
            if(mGCalendars != null)
                for(GoogleCalendar gCalendar:mGCalendars){
                    GoogleCalendarHelper.getEvents(gCalendar);
                }

        return mGEvents;
    }

    public static ArrayList<GoogleEvent> getEvents(GoogleCalendar calendar){

        // add the begin and end times to the URI to use these to limit the list to events between them
        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();

        // Run query
        Cursor cur = null;
        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        Uri uri = Events.CONTENT_URI;

        String selection = "((" + Events.CALENDAR_ID + " = ?))";
        String[] selectionArgs = new String[] {"" + calendar.calID};

        // Submit the query and get a Cursor object back.
        if(ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED)
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        else
            return mGEvents;

        if(mGEvents == null)
            mGEvents = new ArrayList<>();

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

            mGEvents.add(gEvent);
        }

        return mGEvents;
    }

    public static ArrayList<GoogleEvent> getEvents(PersianCalendar date) {
        if (mGEvents == null) {
            mGEvents = getEvents();
        }

        ArrayList<GoogleEvent> selectedGoogleEvents = new ArrayList<>();

        if(mGEvents != null)
            for (GoogleEvent gEvent : mGEvents) {
                if (gEvent.mStartDate != null && gEvent.mStartDate.equals(date)) {
                    selectedGoogleEvents.add(gEvent);
                }
            }

        return selectedGoogleEvents;
    }

    public static int saveSimpleEvent(String title, String desc, long startMillis){
        return saveSimpleEvent(new GoogleEvent(title, desc, startMillis));
    }

    public static int saveSimpleEvent(GoogleEvent newEvent){
        //Return if No Calendar is Available
        if(mGCalendars == null || mGCalendars.size() <= 0)
            return R.string.event_error_no_calendar;

        if(TextUtils.isEmpty(newEvent.mTITLE) && TextUtils.isEmpty(newEvent.mDESCRIPTION)){
            return R.string.event_error_no_content;
        }

        Uri uri = null;
        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.CALENDAR_ID, mGCalendars.get(0).calID);
        values.put(Events.TITLE, newEvent.mTITLE);
        values.put(Events.DESCRIPTION, newEvent.mDESCRIPTION);
        values.put(Events.DTSTART, newEvent.mDTSTART);
        values.put(Events.DTEND, newEvent.mDTSTART + 1000 * 60 * 60);
        values.put(Events.EVENT_TIMEZONE, new PersianCalendar().getTimeZone().getDisplayName());

        // Submit the query and get a Cursor object back.
        if(ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED)
            uri = cr.insert(Events.CONTENT_URI, values);
        else
            return R.string.event_error_write_permission;

        //Get the event ID and Add it to Google Events Pool
        newEvent.mID = Long.parseLong(uri.getLastPathSegment());

        if(mGEvents == null)
            mGEvents = new ArrayList<>();

        mGEvents.add(newEvent);

        return R.string.event_successfully_added;
    }

    public static int updateEvent(GoogleEvent gEvent){
        Uri updateUri = null;
        ContentResolver cr = ApplicationController.getContext().getContentResolver();
        ContentValues values = new ContentValues();

        //Update Event Row
        values.put(Events.TITLE, gEvent.mTITLE);
        values.put(Events.DESCRIPTION, gEvent.mDESCRIPTION);

        updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, gEvent.mID);
        int rows = cr.update(updateUri, values, null, null);

        //Update Google Events Pool
        if(mGEvents != null)
            for(GoogleEvent event:mGEvents){
                if(gEvent.mID == event.mID){
                    event.mTITLE = gEvent.mTITLE;
                    event.mDESCRIPTION = gEvent.mDESCRIPTION;
                    break;
                }
            }

        return R.string.event_successfully_updated;
    }

    public static int deleteEvent(GoogleEvent gEvent){
        Uri deleteUri = null;
        ContentResolver cr = ApplicationController.getContext().getContentResolver();

        deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, gEvent.mID);
        int rows = cr.delete(deleteUri, null, null);

        //Remove From Google Events Pool
        if(mGEvents != null)
            if(mGEvents.contains(gEvent))
                mGEvents.remove(gEvent);

        return R.string.event_successfully_deleted;
    }
}
