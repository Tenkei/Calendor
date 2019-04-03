package com.esbati.keivan.persiancalendar.refactoring.eventdatasource

import android.annotation.SuppressLint
import com.esbati.keivan.persiancalendar.pojos.CalendarDay
import com.esbati.keivan.persiancalendar.pojos.GoogleEvent
import com.esbati.keivan.persiancalendar.refactoring.bases.CalendarTypes
import com.esbati.keivan.persiancalendar.refactoring.bases.Event
import com.esbati.keivan.persiancalendar.refactoring.bases.MutableEventDataSource
import com.esbati.keivan.persiancalendar.refactoring.events.UserEvent
import com.esbati.keivan.persiancalendar.repository.GoogleCalendarEventHelper
import ir.smartlab.persindatepicker.util.PersianCalendar
import java.util.*

@SuppressLint("MissingPermission")
object GoogleEventDataSource : MutableEventDataSource {

    private val mCalendar: PersianCalendar = PersianCalendar().apply {
        // Set time at the middle of the day to prevent shift in days
        // for dates like yyyy/1/1 caused by DST
        set(Calendar.HOUR_OF_DAY, 12)
    }

    //not implemented
    override fun getMonthEvents(year: Int, month: Int): List<Event> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDayEvents(year: Int, month: Int, day: Int): List<Event> {
        return GoogleCalendarEventHelper.getEvents(year, month, day)
                .map {
                    UserEvent(it.id,
                            it.title ?: "",
                            it.description ?: "",
                            it.year,
                            it.month,
                            it.day,
                            false,
                            CalendarTypes.SOLAR_HIJRI)
                }
                .map {
                    it as Event
                }
    }

    override fun createEventFor(day: CalendarDay): Event {
        return UserEvent(0,
                "",
                "",
                day.mDay,
                day.mMonth,
                day.mYear,
                false,
                CalendarTypes.SOLAR_HIJRI)
    }

    override fun saveEvent(event: Event): Int {
        if (event is UserEvent) {
            val date = (mCalendar.clone() as PersianCalendar).apply {
                setPersianDate(event.year, event.month, event.day)
            }

            val googleEvent = GoogleEvent(event.id,
                    event.title,
                    event.description,
                    date.timeInMillis)
            return GoogleCalendarEventHelper.saveEvent(googleEvent)
        }
        return -1;

    }

    override fun deleteEvent(id: Long): Int {
        return GoogleCalendarEventHelper.deleteEvent(id)

    }


}