package com.esbati.keivan.persiancalendar.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import com.esbati.keivan.persiancalendar.pojos.UserEvent
import java.util.ArrayList

interface CalendarDataStore {
    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    fun getEvents(year: Int, month: Int, day: Int): ArrayList<UserEvent>

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    fun saveEvent(event: UserEvent): Int

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    fun saveSimpleEvent(newEvent: UserEvent): Int

    fun updateEvent(event: UserEvent): Int
    fun deleteEvent(eventId: Long): Int
}