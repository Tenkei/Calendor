package com.esbati.keivan.persiancalendar.Repository

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.provider.CalendarContract
import android.support.annotation.RequiresPermission
import com.esbati.keivan.persiancalendar.Components.ApplicationController
import com.esbati.keivan.persiancalendar.POJOs.DeviceCalendar
import com.esbati.keivan.persiancalendar.POJOs.UserEvent
import ir.smartlab.persindatepicker.util.PersianCalendar

object CalendarDataStore {

    private val mCalendars: ArrayList<DeviceCalendar>
    private val mEvents: ArrayList<UserEvent>

    // The indices for the projection array above.
    private const val PROJECTION_ID_INDEX = 0
    private const val PROJECTION_ACCOUNT_NAME_INDEX = 1
    private const val PROJECTION_DISPLAY_NAME_INDEX = 2
    private const val PROJECTION_OWNER_ACCOUNT_INDEX = 3

    private val CALENDAR_PROJECTION = arrayOf(
            CalendarContract.Calendars._ID,                     // 0
            CalendarContract.Calendars.ACCOUNT_NAME,            // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
            CalendarContract.Calendars.OWNER_ACCOUNT)           // 3


    private val EVENT_PROJECTION = arrayOf(
            CalendarContract.Events._ID,                // 0
            CalendarContract.Events.ORGANIZER,          // 1
            CalendarContract.Events.TITLE,              // 2
            CalendarContract.Events.EVENT_LOCATION,     // 3
            CalendarContract.Events.DESCRIPTION,        // 4
            CalendarContract.Events.DTSTART,            // 5
            CalendarContract.Events.DTEND,              // 6
            CalendarContract.Events.EVENT_TIMEZONE,     // 7
            CalendarContract.Events.EVENT_END_TIMEZONE, // 8
            CalendarContract.Events.DURATION,           // 9
            CalendarContract.Events.ALL_DAY,            // 10
            CalendarContract.Events.RRULE,              // 11
            CalendarContract.Events.RDATE)              // 12

    init {
        mCalendars = getCalendars()
        mEvents = ArrayList()
        for (calendar in mCalendars) {
            mEvents.addAll(getEvents(calendar))
        }
    }

    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    private fun getCalendars(): ArrayList<DeviceCalendar> {
        val calendars = ArrayList<DeviceCalendar>()

        val cr = ApplicationController.getContext().contentResolver
        val cur = cr.query(CalendarContract.Calendars.CONTENT_URI, CALENDAR_PROJECTION, null, null, null)

        cur!!.apply {
            while (moveToNext()) {
                val id = getLong(PROJECTION_ID_INDEX)
                val displayName = getString(PROJECTION_DISPLAY_NAME_INDEX)
                val accountName = getString(PROJECTION_ACCOUNT_NAME_INDEX)
                val ownerName = getString(PROJECTION_OWNER_ACCOUNT_INDEX)

                calendars.add(DeviceCalendar(id, displayName, accountName, ownerName))
            }
        }.close()

        return calendars
    }

    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    private fun getEvents(calendar: DeviceCalendar): ArrayList<UserEvent> {
        val events = ArrayList<UserEvent>()

        // Submit the query and get a Cursor object back.
        val selection = "((" + CalendarContract.Events.CALENDAR_ID + " = ?))"
        val selectionArgs = arrayOf("" + calendar.id)

        val cr = ApplicationController.getContext().contentResolver
        val cur = cr.query(CalendarContract.Events.CONTENT_URI, EVENT_PROJECTION, selection, selectionArgs, null)

        cur!!.apply {
            while (moveToNext()) {
                val id = getLong(0)
                val organizer = getString(1)
                val title = getString(2)
                val eventLocation = getString(3)
                val description = getString(4)
                val dtStart = getLong(5)
                val dtEnd = getLong(6)
                val eventTimezone = getString(7)
                val eventEndTimezone = getString(8)
                val duration = getString(9)
                val allDay = getString(10)
                val rRule = getString(11)
                val rDate = getString(12)

                events.add(
                        UserEvent(id, organizer, title
                                , eventLocation, description
                                , dtStart, dtEnd, eventTimezone, eventEndTimezone
                                , duration, allDay, rRule, rDate
                        )
                )
            }
        }.close()

        return events
    }

    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    fun getEvents(selectedDate: PersianCalendar): ArrayList<UserEvent> {
        val selectedEvents = ArrayList<UserEvent>()

        for (event in mEvents)
            if (selectedDate.equals(event.mStartDate))
                selectedEvents.add(event)

        return selectedEvents
    }

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    fun saveEvent(event: UserEvent): Int {
        //Return if No Calendar is Available
        if (mCalendars.size <= 0)
            return -1

        return if (event.id != 0L)
            updateEvent(event)
        else
            saveSimpleEvent(event)
    }

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    private fun saveSimpleEvent(newEvent: UserEvent): Int {
        val defaultCalendar = mCalendars[0]

        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, defaultCalendar.id)
            put(CalendarContract.Events.TITLE, newEvent.title)
            put(CalendarContract.Events.DESCRIPTION, newEvent.description)
            put(CalendarContract.Events.DTSTART, newEvent.dtStart)
            put(CalendarContract.Events.DTEND, newEvent.dtStart + 1000 * 60 * 60)
            put(CalendarContract.Events.EVENT_TIMEZONE, PersianCalendar().timeZone.displayName)
        }

        // Submit the query and get a Cursor object back.
        val cr = ApplicationController.getContext().contentResolver
        val uri = cr.insert(CalendarContract.Events.CONTENT_URI, values)

        //Get the event ID and Add it to Google Events Pool
        val id = uri!!.lastPathSegment!!.toLong()
        mEvents.add(newEvent.copy(id = id))

        return 1
    }

    private fun updateEvent(event: UserEvent): Int {
        //Update Event Row
        val values = ContentValues().apply {
            put(CalendarContract.Events.TITLE, event.title)
            put(CalendarContract.Events.DESCRIPTION, event.description)
        }

        val cr = ApplicationController.getContext().contentResolver
        val updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.id)
        val rows = cr.update(updateUri, values, null, null)

        //Update Google Events Pool
        for(i in 0 until mEvents.size)
            if (event.id == mEvents[i].id)
                mEvents[i] = event

        return rows
    }

    fun deleteEvent(event: UserEvent): Int {
        //Delete Event Row
        val cr = ApplicationController.getContext().contentResolver
        val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.id)
        val rows = cr.delete(deleteUri, null, null)

        //Remove From Google Events Pool
        mEvents.remove(event)

        return rows
    }
}