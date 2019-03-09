package com.esbati.keivan.persiancalendar.POJOs

import ir.smartlab.persindatepicker.util.PersianCalendar

/**
 * Created by asus on 11/23/2016.
 */

data class UserEvent(
        var id: Long = 0,
        var organizer: String? = null,
        var title: String? = null,
        var eventLocation: String? = null,
        var description: String? = null,
        var dtStart: Long,
        var dtEnd: Long? = 0,
        var eventTimezone: String? = null,
        var eventEndTimezone: String? = null,
        var duration: String? = null,
        var allDay: String? = null,
        var rRule: String? = null,
        var rDate: String? = null)
{
    val mStartDate: PersianCalendar = PersianCalendar(dtStart)
}
