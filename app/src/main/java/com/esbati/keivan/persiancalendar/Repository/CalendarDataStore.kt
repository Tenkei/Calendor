package com.esbati.keivan.persiancalendar.Repository

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.provider.CalendarContract
import android.support.annotation.RequiresPermission
import com.esbati.keivan.persiancalendar.Components.ApplicationController
import com.esbati.keivan.persiancalendar.POJOs.GoogleCalendar
import com.esbati.keivan.persiancalendar.POJOs.GoogleEvent
import ir.smartlab.persindatepicker.util.PersianCalendar

object CalendarDataStore {

    private val mCalendars: ArrayList<GoogleCalendar>
    private val mEvents: ArrayList<GoogleEvent>

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
    private fun getCalendars(): ArrayList<GoogleCalendar> {
        val calendars = ArrayList<GoogleCalendar>()

        val cr = ApplicationController.getContext().contentResolver
        val cur = cr.query(CalendarContract.Calendars.CONTENT_URI, CALENDAR_PROJECTION, null, null, null)

        cur!!.apply {
            while (moveToNext()) {
                val gCalendar = GoogleCalendar()

                // Get the field values
                gCalendar.calID = getLong(PROJECTION_ID_INDEX)
                gCalendar.displayName = getString(PROJECTION_DISPLAY_NAME_INDEX)
                gCalendar.accountName = getString(PROJECTION_ACCOUNT_NAME_INDEX)
                gCalendar.ownerName = getString(PROJECTION_OWNER_ACCOUNT_INDEX)

                calendars.add(gCalendar)
            }

        }.close()

        return calendars
    }

    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    private fun getEvents(calendar: GoogleCalendar): ArrayList<GoogleEvent> {
        val events = ArrayList<GoogleEvent>()

        // Submit the query and get a Cursor object back.
        val selection = "((" + CalendarContract.Events.CALENDAR_ID + " = ?))"
        val selectionArgs = arrayOf("" + calendar.calID)

        val cr = ApplicationController.getContext().contentResolver
        val cur = cr.query(CalendarContract.Events.CONTENT_URI, EVENT_PROJECTION, selection, selectionArgs, null)

        cur!!.apply {
            while (moveToNext()) {
                val gEvent = GoogleEvent()
                gEvent.mCalendar = calendar

                // Get the field values
                gEvent.mID = getLong(0)
                gEvent.mORGANIZER = getString(1)
                gEvent.mTITLE = getString(2)
                gEvent.mEVENT_LOCATION = getString(3)
                gEvent.mDESCRIPTION = getString(4)
                gEvent.mDTSTART = getLong(5)
                gEvent.mDTEND = getLong(6)
                gEvent.mEVENT_TIMEZONE = getString(7)
                gEvent.mEVENT_END_TIMEZONE = getString(8)
                gEvent.mDURATION = getString(9)
                gEvent.mALL_DAY = getString(10)
                gEvent.mRRULE = getString(11)
                gEvent.mRDATE = getString(12)

                gEvent.mStartDate = PersianCalendar(gEvent.mDTSTART)
                gEvent.mEndDate = PersianCalendar(gEvent.mDTEND)

                events.add(gEvent)
            }
        }.close()

        return events
    }

    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    fun getEvents(selectedDate: PersianCalendar): ArrayList<GoogleEvent> {
        val selectedGoogleEvents = ArrayList<GoogleEvent>()

        for (gEvent in mEvents)
            if (selectedDate.equals(gEvent.mStartDate))
                selectedGoogleEvents.add(gEvent)

        return selectedGoogleEvents
    }

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    fun saveEvent(event: GoogleEvent): Int {
        //Return if No Calendar is Available
        if (mCalendars.size <= 0)
            return -1

        return if (event.mID != 0L)
            updateEvent(event)
        else
            saveSimpleEvent(event)
    }

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    private fun saveSimpleEvent(newEvent: GoogleEvent): Int {
        val defaultCalendar = mCalendars[0]

        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, defaultCalendar.calID)
            put(CalendarContract.Events.TITLE, newEvent.mTITLE)
            put(CalendarContract.Events.DESCRIPTION, newEvent.mDESCRIPTION)
            put(CalendarContract.Events.DTSTART, newEvent.mDTSTART)
            put(CalendarContract.Events.DTEND, newEvent.mDTSTART + 1000 * 60 * 60)
            put(CalendarContract.Events.EVENT_TIMEZONE, PersianCalendar().timeZone.displayName)
        }

        // Submit the query and get a Cursor object back.
        val cr = ApplicationController.getContext().contentResolver
        val uri = cr.insert(CalendarContract.Events.CONTENT_URI, values)

        //Get the event ID and Add it to Google Events Pool
        newEvent.mID = uri!!.lastPathSegment!!.toLong()
        mEvents.add(newEvent)

        return 1
    }

    private fun updateEvent(gEvent: GoogleEvent): Int {
        //Update Event Row
        val values = ContentValues().apply {
            put(CalendarContract.Events.TITLE, gEvent.mTITLE)
            put(CalendarContract.Events.DESCRIPTION, gEvent.mDESCRIPTION)
        }

        val cr = ApplicationController.getContext().contentResolver
        val updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, gEvent.mID)
        val rows = cr.update(updateUri, values, null, null)

        //Update Google Events Pool
        for (event in mEvents)
            if (gEvent.mID == event.mID)
                with(event){
                    mTITLE = gEvent.mTITLE
                    mDESCRIPTION = gEvent.mDESCRIPTION
                }

        return rows
    }

    fun deleteEvent(gEvent: GoogleEvent): Int {
        //Delete Event Row
        val cr = ApplicationController.getContext().contentResolver
        val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, gEvent.mID)
        val rows = cr.delete(deleteUri, null, null)

        //Remove From Google Events Pool
        mEvents.remove(gEvent)

        return rows
    }
}