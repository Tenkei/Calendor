package com.esbati.keivan.persiancalendar.repository

import android.content.res.Resources
import android.util.Log
import androidx.annotation.RawRes
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.pojos.CalendarRemark
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class LocalRemarkDataStore(private val resources: Resources) : RemarkDataStore {

    private val remarks: List<CalendarRemark> by lazy { readEventsFromJSON() }

    private fun readEventsFromJSON(): ArrayList<CalendarRemark> {
        val calendarEvents = ArrayList<CalendarRemark>()
        try {
            val eventsJSON = JSONObject(readRawResource(R.raw.events)).getJSONArray("events")

            for (i in 0 until eventsJSON.length()) {
                val eventJSON = eventsJSON.getJSONObject(i)
                val event = CalendarRemark.fromJSON(eventJSON)
                calendarEvents.add(event)
            }

        } catch (e: JSONException) {
            Log.e("JSON Parser", e.message)
        }

        return calendarEvents
    }

    private fun readRawResource(@RawRes res: Int): String {
        val s = Scanner(resources.openRawResource(res)).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    override fun getRemarks(year: Int, month: Int, day: Int): ArrayList<CalendarRemark>
            = remarks.filter { it.inTheSameDate(year, month, day) } as ArrayList
}