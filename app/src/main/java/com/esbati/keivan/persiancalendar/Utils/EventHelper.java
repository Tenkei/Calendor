package com.esbati.keivan.persiancalendar.Utils;

import android.support.annotation.RawRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.esbati.keivan.persiancalendar.Controllers.ApplicationController;
import com.esbati.keivan.persiancalendar.Models.Event;
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

    public static ArrayList<Event> mEvents;

    public static String readRawResource(@RawRes int res) {
        // http://stackoverflow.com/a/5445161
        Scanner s = new Scanner(ApplicationController.getContext().getResources().openRawResource(res)).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static ArrayList<Event> readEventsFromJSON() {
        ArrayList<Event> events = new ArrayList<>();
        try {
            JSONArray days = new JSONObject(readRawResource(R.raw.events)).getJSONArray("events");

            int length = days.length();
            for (int i = 0; i < length; ++i) {
                JSONObject eventJSON = days.getJSONObject(i);
                Event event = new Event().fromJSON(eventJSON);
                events.add(event);
            }
        } catch (JSONException e) {
            Log.e("JSON Parser", e.getMessage());
        }
        return events;
    }

    public static ArrayList<Event> getEvents(PersianCalendar date) {
        if (mEvents == null) {
            mEvents = readEventsFromJSON();
        }

        ArrayList<Event> selectedEvents = new ArrayList<>();

        if(mEvents != null)
            for (Event event : mEvents) {
                if (event.mPersianDate.equals(date)) {
                    selectedEvents.add(event);
                }
            }

        return selectedEvents;
    }
}
