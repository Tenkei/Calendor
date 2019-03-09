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
                val calendar = DeviceCalendar()

                // Get the field values
                calendar.id = getLong(PROJECTION_ID_INDEX)
                calendar.displayName = getString(PROJECTION_DISPLAY_NAME_INDEX)
                calendar.accountName = getString(PROJECTION_ACCOUNT_NAME_INDEX)
                calendar.ownerName = getString(PROJECTION_OWNER_ACCOUNT_INDEX)

                calendars.add(calendar)
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
                val event = UserEvent()
                event.mCalendar = calendar

                // Get the field values
                event.id = getLong(0)
                event.organizer = getString(1)
                event.title = getString(2)
                event.eventLocation = getString(3)
                event.description = getString(4)
                event.dtStart = getLong(5)
                event.dtEnd = getLong(6)
                event.eventTimezone = getString(7)
                event.eventEndTimezone = getString(8)
                event.duration = getString(9)
                event.allDay = getString(10)
                event.rRule = getString(11)
                event.rDate = getString(12)

                event.mStartDate = PersianCalendar(event.dtStart)
                event.mEndDate = PersianCalendar(event.dtEnd)

                events.add(event)
            }
        }.close()

        return events
    }

    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    fun getEvents(selectedDate: PersianCalendar): ArrayList<UserEvent> {
        val selectedGoogleEvents = ArrayList<UserEvent>()

        for (gEvent in mEvents)
            if (selectedDate.equals(gEvent.mStartDate))
                selectedGoogleEvents.add(gEvent)

        return selectedGoogleEvents
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
        newEvent.id = uri!!.lastPathSegment!!.toLong()
        mEvents.add(newEvent)

        return 1
    }

    private fun updateEvent(gEvent: UserEvent): Int {
        //Update Event Row
        val values = ContentValues().apply {
            put(CalendarContract.Events.TITLE, gEvent.title)
            put(CalendarContract.Events.DESCRIPTION, gEvent.description)
        }

        val cr = ApplicationController.getContext().contentResolver
        val updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, gEvent.id)
        val rows = cr.update(updateUri, values, null, null)

        //Update Google Events Pool
        for (event in mEvents)
            if (gEvent.id == event.id)
                with(event){
                    title = gEvent.title
                    description = gEvent.description
                }

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