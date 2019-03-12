package com.esbati.keivan.persiancalendar.pojos

import ir.smartlab.persindatepicker.util.PersianCalendar

/**
 * Created by asus on 11/23/2016.
 */

data class UserEvent(
        val id: Long = 0,
        val title: String? = null,
        val description: String? = null,
        val dtStart: Long,
        val dtEnd: Long? = 0,
        val eventTimezone: String? = null)
{
    val mStartDate: PersianCalendar = PersianCalendar(dtStart)
}
