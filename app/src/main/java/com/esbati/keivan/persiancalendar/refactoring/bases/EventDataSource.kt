package com.esbati.keivan.persiancalendar.refactoring.bases


interface EventDataSource {

    fun getMonthEvents(year: Int, month: Int): List<Event>

    fun getDayEvents(year: Int, month: Int, day: Int): List<Event>
}