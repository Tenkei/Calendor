package com.esbati.keivan.persiancalendar.pojos

import com.esbati.keivan.persiancalendar.utils.Constants
import ir.smartlab.persindatepicker.util.PersianCalendar
import java.util.*

/**
 * Created by asus on 11/20/2016.
 */

class CalendarDay(persianDate: PersianCalendar) {

    var mDay: Int = 0
    var mMonth: Int = 0
    var mYear: Int = 0
    var isToday: Boolean = false
    var isHoliday: Boolean = false
    var isCurrentMonth: Boolean = false
    var mRemarks: ArrayList<CalendarRemark> = ArrayList()
    var mEvents: ArrayList<UserEvent> = ArrayList()
    var formattedDate: String
    var formattedDateSecondary: String

    init {
        mDay = persianDate.persianDay
        mMonth = persianDate.persianMonth
        mYear = persianDate.persianYear
        formattedDate = persianDate.persianLongDate
        formattedDateSecondary = GregorianCalendar().run {
            time = persianDate.time
            "${Constants.weekdays_en[get(Calendar.DAY_OF_WEEK) - 1]}, ${Constants.months_en[get(Calendar.MONTH)]} ${get(Calendar.DAY_OF_MONTH)} ${get(Calendar.YEAR)}"
        }
    }
}
