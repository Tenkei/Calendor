package com.esbati.keivan.persiancalendar

import com.esbati.keivan.persiancalendar.pojos.CalendarDay
import com.esbati.keivan.persiancalendar.pojos.CalendarRemark
import com.esbati.keivan.persiancalendar.pojos.UserEvent
import com.esbati.keivan.persiancalendar.repository.CalendarDataStore
import com.esbati.keivan.persiancalendar.repository.RemarkDataStore
import com.esbati.keivan.persiancalendar.repository.Repository

class FakeRepository(): Repository {
    override fun getToday(): CalendarDay
            = CalendarDay(
                1398, 2, 15,
                true, false, true,
                "یک\u200Cشنبه  15  اردی\u200Cبهشت  1398",
                "Sunday, May 5 2019"
            )

    override fun getDays(year: Int, month: Int): List<CalendarDay> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRemarks(year: Int, month: Int, day: Int): ArrayList<CalendarRemark> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getEvents(year: Int, month: Int, day: Int): ArrayList<UserEvent> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createEvent(day: CalendarDay): UserEvent {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveEvent(event: UserEvent): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteEvent(event: UserEvent): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

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
        //Find and replace event
        for (i in 0 until events.size)
            if (event.id == events[i].id)
                events[i] = event
        return 1
    }

    override fun deleteEvent(eventId: Long): Int {
        //Remove From Events Pool
        events.removeAll { it.id == eventId }
        return 1
    }
}