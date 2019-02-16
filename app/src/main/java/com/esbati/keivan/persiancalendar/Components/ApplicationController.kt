package com.esbati.keivan.persiancalendar.Components

import android.app.Application
import android.content.Context
import android.content.Intent

import com.crashlytics.android.Crashlytics
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.Features.Notification.ApplicationService
import com.esbati.keivan.persiancalendar.Features.Notification.NotificationUpdateService
import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities
import com.onesignal.OneSignal

import io.fabric.sdk.android.Fabric
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

/**
 * Created by Esbati on 12/22/2015.
 */
class ApplicationController : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        Fabric.with(this, Crashlytics())
        OneSignal.startInit(this).init()
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSans(FaNum).ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

        //Start Application Service if Not Running
        if (!AndroidUtilities.isServiceRunning(ApplicationService::class.java))
            startService(Intent(baseContext, ApplicationService::class.java))

        //Show Sticky Notification
        val notificationIntent = Intent(this, NotificationUpdateService::class.java)
        startService(notificationIntent)
    }

    companion object {
        private lateinit var appContext: Context

        @JvmStatic fun getContext() = appContext
    }
}
