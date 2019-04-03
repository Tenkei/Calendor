package com.esbati.keivan.persiancalendar.refactoring.eventdatasource

import android.support.annotation.RawRes
import android.util.Log
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.components.ApplicationController
import com.esbati.keivan.persiancalendar.refactoring.bases.CalendarTypes
import com.esbati.keivan.persiancalendar.refactoring.bases.Event
import com.esbati.keivan.persiancalendar.refactoring.bases.EventDataSource
import com.esbati.keivan.persiancalendar.refactoring.events.CalendarEvent
import org.json.JSONException
import org.json.JSONObject
import java.util.*


object SolarHijriEventsDataSource : EventDataSource {

    private const val DAY_IN_MILLIS = 1000L * 24 * 60 * 60
    private val calendarEvents: List<CalendarEvent>

    init {
        calendarEvents = readEventsFromJSON()
    }

    private fun readRawResource(@RawRes res: Int): String {
        val s = Scanner(ApplicationController.getContext().resources.openRawResource(res)).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    private fun readEventsFromJSON(): ArrayList<CalendarEvent> {
        val calendarEvents = ArrayList<CalendarEvent>()
        try {
            val eventsJSON = JSONObject(readRawResource(R.raw.events)).getJSONArray("events")

            for (i in 0 until eventsJSON.length()) {
                val eventJSON = eventsJSON.getJSONObject(i)
                val event = convertJsonToCalendarEvent(eventJSON)
                calendarEvents.add(event)
            }

        } catch (e: JSONException) {
            Log.e("JSON Parser", e.message)
        }

        return calendarEvents
    }

    @Throws(JSONException::class)
    fun convertJsonToCalendarEvent(eventJSON: JSONObject): CalendarEvent {
        val title = eventJSON.getString("title")
        val year = eventJSON.optInt("year", -1)
        val month = eventJSON.getInt("month")
        val day = eventJSON.getInt("day")
        val isHoliday = eventJSON.getBoolean("holiday")
        return CalendarEvent(title, year, month, day, isHoliday, CalendarTypes.SOLAR_HIJRI)
    }

    override fun getMonthEvents(year: Int, month: Int): List<Event> {
        return calendarEvents.filter {
            it.month == month
        }.map {
            it as Event
        }
    }

    override fun getDayEvents(year: Int, month: Int, day: Int): List<Event> {
        return calendarEvents.filter {
            it.inTheSameDate(year,month,day,CalendarTypes.SOLAR_HIJRI)
        }.map {
            it as Event
        }
    }
}