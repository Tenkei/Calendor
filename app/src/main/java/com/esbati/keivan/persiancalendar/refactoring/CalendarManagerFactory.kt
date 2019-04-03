package com.esbati.keivan.persiancalendar.refactoring

import com.esbati.keivan.persiancalendar.refactoring.bases.CalendarManager
import com.esbati.keivan.persiancalendar.refactoring.eventdatasource.GoogleEventDataSource
import com.esbati.keivan.persiancalendar.refactoring.eventdatasource.SolarHijriEventsDataSource

class CalendarManagerFactory {

    companion object {
        fun create():CalendarManager {
            return PersianCalendarManager(SolarHijriEventsDataSource, GoogleEventDataSource)
        }
    }
}