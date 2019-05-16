package com.esbati.keivan.persiancalendar.repository

import com.esbati.keivan.persiancalendar.pojos.CalendarRemark
import java.util.ArrayList

interface RemarkDataStore {
    fun getRemarks(year: Int, month: Int, day: Int): ArrayList<CalendarRemark>
}