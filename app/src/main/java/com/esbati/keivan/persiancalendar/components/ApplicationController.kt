package com.esbati.keivan.persiancalendar.components

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.esbati.keivan.persiancalendar.BuildConfig
import com.esbati.keivan.persiancalendar.features.notification.NotificationHelper
import com.esbati.keivan.persiancalendar.features.notification.NotificationUpdateService
import com.esbati.keivan.persiancalendar.repository.*
import io.fabric.sdk.android.Fabric
import ir.smartlab.persindatepicker.util.PersianCalendar
import java.util.*

/**
 * Created by Keivan Esbati on 12/22/2015.
 */
class ApplicationController : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        SoundManager.init()
        ServiceLocator.init(ServiceLocator().apply {
            single { RepositoryImp(get(), get(), get()) as Repository }
            single { LocalRemarkDataStore(this@ApplicationController.resources) as RemarkDataStore }
            single { DeviceCalendarDataStore(this@ApplicationController.contentResolver) as CalendarDataStore }
            factory { PersianCalendar().apply {
                // Set time at the middle of the day to prevent shift in days
                // for dates like yyyy/1/1 caused by DST
                set(Calendar.HOUR_OF_DAY, 12)
            }}
        })

        val crashlyticsKit  = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()
        Fabric.with(this, crashlyticsKit)

        //Show Sticky Notification
        NotificationHelper.createNotificationChannelIfRequired(this)
        NotificationUpdateService.enqueueUpdate(this)
    }

    companion object {
        private lateinit var appContext: Context

        @JvmStatic fun getContext() = appContext
    }
}
