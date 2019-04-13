package com.esbati.keivan.persiancalendar.repository

import android.Manifest
import android.content.pm.PackageManager
import android.support.annotation.RawRes
import android.support.annotation.RequiresPermission
import android.support.v4.content.ContextCompat
import android.util.Log
import com.esbati.keivan.persiancalendar.components.ApplicationController
import com.esbati.keivan.persiancalendar.pojos.CalendarDay
import com.esbati.keivan.persiancalendar.pojos.CalendarRemark
import com.esbati.keivan.persiancalendar.pojos.UserEvent
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.utils.Constants
import ir.smartlab.persindatepicker.util.PersianCalendar
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class Repository (
    calendar: PersianCalendar,
    calendarDataStore: CalendarDataStore
) {

    private val remarks = readEventsFromJSON()
    private val mCalendar = calendar
    private val mCalendarDataStore = calendarDataStore

    companion object {
        private val calendar = PersianCalendar().apply {
            // Set time at the middle of the day to prevent shift in days
            // for dates like yyyy/1/1 caused by DST
            set(Calendar.HOUR_OF_DAY, 12)
        }
        private val calendarDataStore = CalendarDataStore(ApplicationController.getContext().contentResolver)

        val INSTANCE: Repository by lazy {
            Repository(calendar, calendarDataStore)
        }
    }

    private fun readRawResource(@RawRes res: Int): String {
        val s = Scanner(ApplicationController.getContext().resources.openRawResource(res)).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    private fun readEventsFromJSON(): ArrayList<CalendarRemark> {
        val calendarEvents = ArrayList<CalendarRemark>()
        try {
            val eventsJSON = JSONObject(readRawResource(R.raw.events)).getJSONArray("events")

            for (i in 0 until eventsJSON.length()) {
                val eventJSON = eventsJSON.getJSONObject(i)
                val event = CalendarRemark.fromJSON(eventJSON)
                calendarEvents.add(event)
            }

        } catch (e: JSONException) {
            Log.e("JSON Parser", e.message)
        }

        return calendarEvents
    }

    fun prepareDays(year: Int, month: Int): List<CalendarDay> {
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

            val dayRemarks = getRemarks(year, month, i)
            val dayEvents = getEventsIfPermissionIsAvailable(year, month, i)
            val isToday = i == mToday && containToday
            val isHoliday = currentDayCalendar.persianWeekDay == 6 || dayRemarks.any { it.isHoliday }

            days.add(
                    CalendarDay(year, month, i
                            , isToday, isHoliday, true
                            , currentDayCalendar.persianLongDate, georgianDate
                            , dayRemarks, dayEvents)
            )
        }

        //Add Leading Month Days
        for (i in 1..(7 - days.size % 7))
            days.add(CalendarDay(year, month + 1, i))

        return days
    }

    fun getToday(): CalendarDay {
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
                , getRemarks(date.persianYear, date.persianMonth, date.persianDay)
                , getEventsIfPermissionIsAvailable(date.persianYear, date.persianMonth, date.persianDay)
        )
    }

    private fun getRemarks(year: Int, month: Int, day: Int): ArrayList<CalendarRemark> =
            RemarkDataStore.getRemarks(year, month, day)

    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    fun getEvents(year: Int, month: Int, day: Int): ArrayList<UserEvent> =
            mCalendarDataStore.getEvents(year, month, day)

    private fun getEventsIfPermissionIsAvailable(year: Int, month: Int, day: Int): ArrayList<UserEvent> {
        return if (ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED)
                    getEvents(year, month, day)
                else
                    ArrayList()
    }

    fun createEventFor(day: CalendarDay): UserEvent {
        val date = (mCalendar.clone() as PersianCalendar).apply {
            setPersianDate(day.mYear, day.mMonth, day.mDay)
        }

        return UserEvent(dtStart = date.timeInMillis)
    }

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    fun saveEvent(event: UserEvent): Int = mCalendarDataStore.saveEvent(event)

    fun deleteEvent(event: UserEvent): Int = mCalendarDataStore.deleteEvent(event.id)
}



