package com.esbati.keivan.persiancalendar.refactoring.events

import com.esbati.keivan.persiancalendar.refactoring.bases.CalendarTypes
import com.esbati.keivan.persiancalendar.refactoring.bases.Event

data class UserEvent(
        val id: Long,
        override val title: String,
        val description: String,
        override val year: Int,
        override val month: Int,
        override val day: Int,
        override val isHoliday: Boolean,
        override val calendarType: CalendarTypes) : Event