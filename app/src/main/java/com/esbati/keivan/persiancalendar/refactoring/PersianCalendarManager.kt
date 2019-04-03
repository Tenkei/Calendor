package com.esbati.keivan.persiancalendar.refactoring

import com.esbati.keivan.persiancalendar.pojos.CalendarDay
import com.esbati.keivan.persiancalendar.refactoring.bases.CalendarManager
import com.esbati.keivan.persiancalendar.refactoring.bases.Event
import com.esbati.keivan.persiancalendar.refactoring.bases.EventDataSource
import com.esbati.keivan.persiancalendar.refactoring.bases.MutableEventDataSource
import com.esbati.keivan.persiancalendar.utils.Constants
import ir.smartlab.persindatepicker.util.PersianCalendar
import java.util.*

class PersianCalendarManager(val calendarEvent: EventDataSource,
                             val userEvent: MutableEventDataSource):CalendarManager {

    private val mCalendar: PersianCalendar = PersianCalendar().apply {
        // Set time at the middle of the day to prevent shift in days
        // for dates like yyyy/1/1 caused by DST
        set(Calendar.HOUR_OF_DAY, 12)
    }


    override fun provideMonth(year: Int, month: Int): List<CalendarDay> {
        val todayCalendar = (mCalendar.clone() as PersianCalendar).apply {
            timeInMillis = System.currentTimeMillis()
        }
        val mToday = todayCalendar.persianDay
        val containToday = todayCalendar.persianYear == year && todayCalendar.persianMonth == month

        val currentYearCalendar = (mCalendar.clone() as PersianCalendar).setPersianDate(year, month, 1)
        val isLeapYear = currentYearCalendar.isPersianLeapYear
        val dayOfWeek = currentYearCalendar.persianWeekDay % 7
        var currentMonthDays = Constants.daysOfMonth_fa[month - 1]
        var previousMonthDays = Constants.daysOfMonth_fa[(month - 2 + 12) % 12]

        //Add Extra Day to current month in Case of Leap Year
        if (isLeapYear && month == 12)
            currentMonthDays++

        //Add Extra Day to Previous Month in Case of Leap Year
        val lastYearCalendar = (mCalendar.clone() as PersianCalendar).setPersianDate(year - 1, month, 1)
        if (month == 1 && lastYearCalendar.isPersianLeapYear)
            previousMonthDays++

        val days = ArrayList<CalendarDay>()
        //Add Trailing Days from Last Month if Needed
        if (dayOfWeek > 0)
            for (i in dayOfWeek - 1 downTo 0)
                days.add(CalendarDay(year, month - 1, previousMonthDays - i))

        //Add Month Days
        for (i in 1..currentMonthDays) {
            val currentDayCalendar = (mCalendar.clone() as PersianCalendar).setPersianDate(year, month, i)
            val georgianDate = GregorianCalendar().run {
                time = currentDayCalendar.time
                "${Constants.weekdays_en[get(Calendar.DAY_OF_WEEK) - 1]}, ${Constants.months_en[get(Calendar.MONTH)]} ${get(Calendar.DAY_OF_MONTH)} ${get(Calendar.YEAR)}"
            }

            val dayRemarks = calendarEvent.getDayEvents(year, month, i)
            val dayEvents = userEvent.getDayEvents(year, month, i)
            val isToday = i == mToday && containToday
            val isHoliday = currentDayCalendar.persianWeekDay == 6 || dayRemarks.any { it.isHoliday }

            days.add(
                    CalendarDay(year, month, i
                            , isToday, isHoliday, true
                            , currentDayCalendar.persianLongDate, georgianDate
                            , dayRemarks as ArrayList<Event>, dayEvents as ArrayList<Event>)
            )
        }

        //Add Leading Month Days
        for (i in 1..(7 - days.size % 7))
            days.add(CalendarDay(year, month + 1, i))

        return days
    }

    override fun provideToday(): CalendarDay {
        val date = (mCalendar.clone() as PersianCalendar).apply {
            timeInMillis = System.currentTimeMillis()
        }

        val georgianDate = GregorianCalendar().run {
            time = date.time
            "${Constants.weekdays_en[get(Calendar.DAY_OF_WEEK) - 1]}, ${Constants.months_en[get(Calendar.MONTH)]} ${get(Calendar.DAY_OF_MONTH)} ${get(Calendar.YEAR)}"
        }

        return CalendarDay(date.persianYear, date.persianMonth, date.persianDay
                , true, true, true
                , date.persianLongDate, georgianDate
                , calendarEvent.getDayEvents(date.persianYear, date.persianMonth, date.persianDay) as ArrayList<Event>
                , userEvent.getDayEvents(date.persianYear, date.persianMonth, date.persianDay)  as ArrayList<Event>
        )
    }


}