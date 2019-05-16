package com.esbati.keivan.persiancalendar.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import com.esbati.keivan.persiancalendar.pojos.CalendarDay
import com.esbati.keivan.persiancalendar.pojos.CalendarRemark
import com.esbati.keivan.persiancalendar.pojos.UserEvent

interface Repository {
    fun getToday(): CalendarDay

    fun getDays(year: Int, month: Int): List<CalendarDay>

    fun getRemarks(year: Int, month: Int, day: Int): ArrayList<CalendarRemark>

    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    fun getEvents(year: Int, month: Int, day: Int): ArrayList<UserEvent>

    fun createEvent(day: CalendarDay): UserEvent

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    fun saveEvent(event: UserEvent): Int

    fun deleteEvent(event: UserEvent): Int
}