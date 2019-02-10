package com.esbati.keivan.persiancalendar.Components;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Features.Notification.ApplicationService;
import com.esbati.keivan.persiancalendar.Features.Notification.NotificationUpdateService;
import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities;
import com.onesignal.OneSignal;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Esbati on 12/22/2015.
 */
public class ApplicationController extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        Fabric.with(this, new Crashlytics());
        OneSignal.startInit(this).init();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSans(FaNum).ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //Start Application Service if Not Running
        if (!AndroidUtilities.isServiceRunning(ApplicationService.class))
            startService(new Intent(getBaseContext(), ApplicationService.class));

        //Show Sticky Notification
        Intent notificationIntent = new Intent(this, NotificationUpdateService.class);
        startService(notificationIntent);
    }

    public static Context getContext(){
        return mContext;
    }
}
