package com.esbati.keivan.persiancalendar.pojos

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by asus on 11/21/2016.
 */

data class CalendarRemark (
    val mTitle: String,
    val mYear: Int,
    val mMonth: Int,
    val mDay: Int,
    val isHoliday: Boolean) {

    fun inTheSameDate(year: Int, month: Int, day: Int): Boolean {
        return (mYear == -1 || mYear == year) && mMonth == month && mDay == day
    }

    companion object {
        @Throws(JSONException::class)
        fun fromJSON(eventJSON: JSONObject): CalendarRemark {
            val title = eventJSON.getString("title")
            val year = eventJSON.optInt("year", -1)
            val month = eventJSON.getInt("month")
            val day = eventJSON.getInt("day")
            val isHoliday = eventJSON.getBoolean("holiday")
            return CalendarRemark(title, year, month, day, isHoliday)
        }
    }
}
