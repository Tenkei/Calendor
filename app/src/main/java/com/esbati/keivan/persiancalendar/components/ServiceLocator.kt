package com.esbati.keivan.persiancalendar.components

import android.app.Application
import android.content.Context
import com.esbati.keivan.persiancalendar.repository.CalendarDataStore
import com.esbati.keivan.persiancalendar.repository.RemarkDataStore
import com.esbati.keivan.persiancalendar.repository.Repository
import ir.smartlab.persindatepicker.util.PersianCalendar
import java.util.*

class ServiceLocator(val app: Application) {

    private val remarkDataStore by lazy { RemarkDataStore(app.resources) }
    private val calendarDataStore by lazy { CalendarDataStore(app.contentResolver) }
    private val repo: Repository by lazy { Repository(getCalendar(), remarkDataStore, calendarDataStore) }

    companion object {
        private var instance: ServiceLocator? = null
        fun instance(context: Context): ServiceLocator {
            synchronized(ServiceLocator::class.java) {
                if (instance == null)
                    instance = ServiceLocator(context.applicationContext as Application)

                return instance!!
            }
        }
    }

    fun getCalendar() = PersianCalendar().apply {
        // Set time at the middle of the day to prevent shift in days
        // for dates like yyyy/1/1 caused by DST
        set(Calendar.HOUR_OF_DAY, 12)
    }

    fun getRepository(): Repository = repo
}