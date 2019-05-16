package com.esbati.keivan.persiancalendar

import com.esbati.keivan.persiancalendar.pojos.CalendarRemark
import com.esbati.keivan.persiancalendar.pojos.UserEvent
import com.esbati.keivan.persiancalendar.repository.CalendarDataStore
import com.esbati.keivan.persiancalendar.repository.RemarkDataStore

class FakeRemarkDataStore(vararg testRemarks: CalendarRemark) : RemarkDataStore  {

    private val remarks = testRemarks.toList()

    override fun getRemarks(year: Int, month: Int, day: Int): ArrayList<CalendarRemark>
            = remarks.filter { it.inTheSameDate(year, month, day) } as ArrayList
}

class FakeCalendarDataStore(vararg testEvents: UserEvent): CalendarDataStore {

    private val events = testEvents.toMutableList()

    override fun getEvents(year: Int, month: Int, day: Int)
            = events.filter { it.inTheSameDate(year, month, day) } as ArrayList<UserEvent>

    override fun saveEvent(event: UserEvent): Int {
        return if (event.id != 0L)
            updateEvent(event)
        else
            saveSimpleEvent(event)
    }

    override fun saveSimpleEvent(newEvent: UserEvent): Int {
        events.add(newEvent)
        return 1
    }

    override fun updateEvent(event: UserEvent): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteEvent(eventId: Long): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}