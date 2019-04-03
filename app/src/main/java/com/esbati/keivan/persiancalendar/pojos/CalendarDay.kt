package com.esbati.keivan.persiancalendar.pojos

import com.esbati.keivan.persiancalendar.refactoring.bases.Event
import java.util.*

/**
 * Created by asus on 11/20/2016.
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
    val mRemarks: ArrayList<Event> = ArrayList(),
    val mEvents: ArrayList<Event> = ArrayList())