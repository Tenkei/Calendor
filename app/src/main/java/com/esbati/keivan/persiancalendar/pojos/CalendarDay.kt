package com.esbati.keivan.persiancalendar.pojos

import java.util.*

/**
 * Created by Keivan Esbati on 11/20/2016.
 */

class CalendarDay(
    val mYear: Int,
    val mMonth: Int,
    val mDay: Int,
    val isToday: Boolean = false,
    val isHoliday: Boolean = false,
    val isCurrentMonth: Boolean = false,
    val formattedDate: String = "",
    val formattedDateSecondary: String = "",
    val mRemarks: ArrayList<CalendarRemark> = ArrayList(),
    val mEvents: ArrayList<UserEvent> = ArrayList())