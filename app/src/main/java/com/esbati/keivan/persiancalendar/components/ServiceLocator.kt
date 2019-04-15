package com.esbati.keivan.persiancalendar.components

import android.app.Application
import com.esbati.keivan.persiancalendar.repository.CalendarDataStore
import com.esbati.keivan.persiancalendar.repository.RemarkDataStore
import com.esbati.keivan.persiancalendar.repository.Repository
import ir.smartlab.persindatepicker.util.PersianCalendar
import java.util.*

interface ServiceLocator {

    companion object {
        lateinit var instance: ServiceLocator

        fun init(serviceLocator: ServiceLocator) {
            instance = serviceLocator
        }
    }

    fun getCalendar(): Calendar

    fun getRepository(): Repository
}

/**
 * default implementation of ServiceLocator which uses real remark and calendar data stores
 */
class DefaultServiceLocator(val app: Application) : ServiceLocator {

    private val remarkDataStore by lazy { RemarkDataStore(app.resources) }
    private val calendarDataStore by lazy { CalendarDataStore(app.contentResolver) }
    private val repo by lazy { Repository(getCalendar(), remarkDataStore, calendarDataStore) }

    override fun getCalendar() = PersianCalendar().apply {
        // Set time at the middle of the day to prevent shift in days
        // for dates like yyyy/1/1 caused by DST
        set(Calendar.HOUR_OF_DAY, 12)
    }

    override fun getRepository(): Repository = repo
}