package com.esbati.keivan.persiancalendar.Components

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.esbati.keivan.persiancalendar.Features.Notification.NotificationHelper
import com.esbati.keivan.persiancalendar.Features.Notification.NotificationUpdateService
import com.esbati.keivan.persiancalendar.R
import io.fabric.sdk.android.Fabric
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

/**
 * Created by Esbati on 12/22/2015.
 */
class ApplicationController : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        SoundManager.init()
        Fabric.with(this, Crashlytics())
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSans(FaNum).ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )

        //Show Sticky Notification
        NotificationHelper.createNotificationChannelIfRequired(this)
        NotificationUpdateService.enqueueUpdate(this)
    }

    companion object {
        private lateinit var appContext: Context

        @JvmStatic fun getContext() = appContext
    }
}
