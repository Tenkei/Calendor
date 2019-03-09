package com.esbati.keivan.persiancalendar.POJOs

import ir.smartlab.persindatepicker.util.PersianCalendar

/**
 * Created by asus on 11/23/2016.
 */

data class UserEvent(
        val id: Long = 0,
        val organizer: String? = null,
        val title: String? = null,
        val eventLocation: String? = null,
        val description: String? = null,
        val dtStart: Long,
        val dtEnd: Long? = 0,
        val eventTimezone: String? = null,
        val eventEndTimezone: String? = null,
        val duration: String? = null,
        val allDay: String? = null,
        val rRule: String? = null,
        val rDate: String? = null)
{
    val mStartDate: PersianCalendar = PersianCalendar(dtStart)
}
