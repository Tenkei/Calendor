package com.esbati.keivan.persiancalendar.refactoring.bases

import com.esbati.keivan.persiancalendar.pojos.CalendarDay

interface CalendarManager {
    fun provideMonth(year: Int, month: Int): List<CalendarDay>

    fun provideToday(): CalendarDay
}