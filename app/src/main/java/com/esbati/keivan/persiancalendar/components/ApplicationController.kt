package com.esbati.keivan.persiancalendar.components

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.esbati.keivan.persiancalendar.BuildConfig
import com.esbati.keivan.persiancalendar.features.notification.NotificationHelper
import com.esbati.keivan.persiancalendar.features.notification.NotificationUpdateService
import com.esbati.keivan.persiancalendar.repository.CalendarDataStore
import com.esbati.keivan.persiancalendar.repository.RemarkDataStore
import com.esbati.keivan.persiancalendar.repository.Repository
import io.fabric.sdk.android.Fabric
import ir.smartlab.persindatepicker.util.PersianCalendar
import java.util.*

/**
 * Created by Esbati on 12/22/2015.
 */
class ApplicationController : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        SoundManager.init()
        val crashlyticsKit  = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()
        Fabric.with(this, crashlyticsKit)

        //Show Sticky Notification
        NotificationHelper.createNotificationChannelIfRequired(this)
        NotificationUpdateService.enqueueUpdate(this)
    }

    override fun getSystemService(name: String): Any {
        return super.getSystemService(name)
    }

    companion object {
        private lateinit var appContext: Context

        @JvmStatic fun getContext() = appContext
    }
}
