package com.esbati.keivan.persiancalendar.pojos

import org.json.JSONException
import org.json.JSONObject

import ir.smartlab.persindatepicker.util.PersianCalendar

/**
 * Created by asus on 11/21/2016.
 */

data class CalendarRemark(
    val mTitle: String,
    val mYear: Int,
    val mMonth: Int,
    val mDay: Int,
    val isHoliday: Boolean,
    val mPersianDate: PersianCalendar) {

    companion object {
        @Throws(JSONException::class)
        fun fromJSON(eventJSON: JSONObject): CalendarRemark {
            val title = eventJSON.getString("title")
            val year = eventJSON.optInt("year", -1)
            val month = eventJSON.getInt("month")
            val day = eventJSON.getInt("day")
            val isHoliday = eventJSON.getBoolean("holiday")
            val persianDate = PersianCalendar().setPersianDate(year, month, day )
            return CalendarRemark(title, year, month, day, isHoliday, persianDate)
        }
    }
}
