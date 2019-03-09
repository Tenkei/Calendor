package com.esbati.keivan.persiancalendar.pojos

import java.util.ArrayList

import ir.smartlab.persindatepicker.util.PersianCalendar

/**
 * Created by asus on 11/20/2016.
 */

class CalendarDay(var mPersianDate: PersianCalendar) {

    var mDayNo: Int = 0
    var isToday: Boolean = false
    var isHoliday: Boolean = false
    var isCurrentMonth: Boolean = false
    var mRemarks: ArrayList<CalendarRemark> = ArrayList()
    var mEvents: ArrayList<UserEvent> = ArrayList()

    init {
        mDayNo = mPersianDate.persianDay
    }
}
