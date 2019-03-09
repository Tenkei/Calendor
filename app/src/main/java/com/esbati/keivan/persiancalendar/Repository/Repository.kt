package com.esbati.keivan.persiancalendar.Repository

import android.Manifest
import android.content.pm.PackageManager
import android.support.annotation.RawRes
import android.support.annotation.RequiresPermission
import android.support.v4.content.ContextCompat
import android.util.Log
import com.esbati.keivan.persiancalendar.Components.ApplicationController
import com.esbati.keivan.persiancalendar.POJOs.CalendarDay
import com.esbati.keivan.persiancalendar.POJOs.CalendarRemark
import com.esbati.keivan.persiancalendar.POJOs.UserEvent
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.Utils.Constants
import ir.smartlab.persindatepicker.util.PersianCalendar
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


object Repository{

    private val remarks: List<CalendarRemark>

    init {
        remarks = readEventsFromJSON()
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
        val calendar = PersianCalendar()
        val containToday = calendar.persianYear == year && calendar.persianMonth == month
        val mToday = calendar.persianDay
        calendar.setPersianDate(year, month, 1)
        val isLeapYear = calendar.isPersianLeapYear
        val dayOfWeek = calendar.persianWeekDay % 7

        val days = ArrayList<CalendarDay>()

        var currentMonthDays = Constants.daysOfMonth[month - 1]
        var previousMonthDays = Constants.daysOfMonth[(month - 2 + 12) % 12]

        //Add Extra Day to current month in Case of Leap Year
        if (isLeapYear && month == 12)
            currentMonthDays++

        //Add Extra Day to Previous Month in Case of Leap Year
        if (month == 1) {
            val lastYearCalendar = PersianCalendar()
            lastYearCalendar.setPersianDate(year - 1, month, 1)

            if (lastYearCalendar.isPersianLeapYear)
                previousMonthDays++
        }

        //Add Trailing Days from Last Month if Needed
        if (dayOfWeek > 0)
            for (i in dayOfWeek - 1 downTo 0)
                days.add(CalendarDay(PersianCalendar().setPersianDate(
                        1, 1, previousMonthDays - i
                )))



        //Add Month Days
        for (i in 1..currentMonthDays) {
            val day = CalendarDay(PersianCalendar().setPersianDate(year, month, i))
            day.isToday = i == mToday && containToday
            day.isCurrentMonth = true

            //Get Events for Current Day
            day.mRemarks = this.getRemarks(day.mPersianDate)
            day.mEvents =
                    if(ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.WRITE_CALENDAR)
                            == PackageManager.PERMISSION_GRANTED)
                        CalendarDataStore.getEvents(day.mPersianDate)
                    else
                        ArrayList()


            if (day.mPersianDate.persianWeekDay == 6)
                day.isHoliday = true
            else if (day.mRemarks != null)
                for (calendarEvent in day.mRemarks)
                    if (calendarEvent.isHoliday) {
                        day.isHoliday = true
                        break
                    }

            days.add(day)
        }

        //Add Leading Month Days
        for (i in 1..(7 - days.size % 7))
            days.add(CalendarDay(PersianCalendar().setPersianDate(
                    1, 1, i
            )))


        return days
    }

    fun getToday(): CalendarDay {
        val today = CalendarDay(PersianCalendar())
        if (ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED)
            today.mEvents = Repository.getEvents(today.mPersianDate)

        return today
    }

    private fun getRemarks(date: PersianCalendar): ArrayList<CalendarRemark> {
        val selectedCalendarEvents = ArrayList<CalendarRemark>()

        for (calendarEvent in remarks)
            if (calendarEvent.mPersianDate.equals(date))
                selectedCalendarEvents.add(calendarEvent)

        return selectedCalendarEvents
    }

    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    fun getEvents(selectedDate: PersianCalendar): ArrayList<UserEvent> = CalendarDataStore.getEvents(selectedDate)

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    fun saveEvent(event: UserEvent): Int = CalendarDataStore.saveEvent(event)

    fun deleteEvent(event: UserEvent): Int = CalendarDataStore.deleteEvent(event)
}



