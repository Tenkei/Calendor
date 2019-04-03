package com.esbati.keivan.persiancalendar.refactoring.bases

import com.esbati.keivan.persiancalendar.pojos.CalendarDay

interface MutableEventDataSource : EventDataSource {

    fun createEventFor(day: CalendarDay): Event

    fun saveEvent(event: Event): Int

    fun deleteEvent(id: Long): Int

}