package com.esbati.keivan.persiancalendar.Utils;

import android.support.annotation.RawRes;
import android.util.Log;

import com.esbati.keivan.persiancalendar.Controllers.ApplicationController;
import com.esbati.keivan.persiancalendar.Models.CalendarEvent;
import com.esbati.keivan.persiancalendar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/21/2016.
 */

public class EventHelper {

    public static ArrayList<CalendarEvent> mCalendarEvents;

    public static String readRawResource(@RawRes int res) {
        // http://stackoverflow.com/a/5445161
        Scanner s = new Scanner(ApplicationController.getContext().getResources().openRawResource(res)).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static ArrayList<CalendarEvent> readEventsFromJSON() {
        ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();
        try {
            JSONArray days = new JSONObject(readRawResource(R.raw.events)).getJSONArray("events");

            int length = days.length();
            for (int i = 0; i < length; ++i) {
                JSONObject eventJSON = days.getJSONObject(i);
                CalendarEvent calendarEvent = new CalendarEvent().fromJSON(eventJSON);
                calendarEvents.add(calendarEvent);
            }

        } catch (JSONException e) {
            Log.e("JSON Parser", e.getMessage());
        }
        return calendarEvents;
    }

    public static ArrayList<CalendarEvent> getEvents(PersianCalendar date) {
        if (mCalendarEvents == null) {
            mCalendarEvents = readEventsFromJSON();
        }

        ArrayList<CalendarEvent> selectedCalendarEvents = new ArrayList<>();

        if(mCalendarEvents != null)
            for (CalendarEvent calendarEvent : mCalendarEvents) {
                if (calendarEvent.mPersianDate.equals(date)) {
                    selectedCalendarEvents.add(calendarEvent);
                }
            }

        return selectedCalendarEvents;
    }
}
