package com.esbati.keivan.persiancalendar.refactoring.bases

interface Event {
    val title: String
    val isHoliday: Boolean
    val year: Int
    val month: Int
    val day: Int
    val calendarType: CalendarTypes

    fun inTheSameDate(year: Int, month: Int, day: Int, calendarType: CalendarTypes): Boolean {
        return (this.year == -1 || this.year == year)  && this.month == month && this.day == day
    }
}