package com.esbati.keivan.persiancalendar.pojos

import ir.smartlab.persindatepicker.util.PersianCalendar

/**
 * Created by Keivan Esbati on 11/23/2016.
 */

data class UserEvent(
        val id: Long = 0,
        val title: String? = null,
        val description: String? = null,
        val dtStart: Long,
        val dtEnd: Long? = 0,
        val eventTimezone: String? = null)
{
    val year: Int = PersianCalendar(dtStart).persianYear
    val month: Int = PersianCalendar(dtStart).persianMonth
    val day: Int = PersianCalendar(dtStart).persianDay

    fun inTheSameDate(year: Int, month: Int, day: Int): Boolean {
        return this.year == year && this.month == month && this.day == day
    }
}
