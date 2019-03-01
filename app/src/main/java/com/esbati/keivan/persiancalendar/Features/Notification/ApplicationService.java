package com.esbati.keivan.persiancalendar.Features.Notification;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by asus on 5/2/2017.
 */

public class ApplicationService extends Service{

    private static WeakReference<ApplicationService> instance;
    private NotificationBroadcastReceiver receiver;
    private IntentFilter intentFilter;

    @Nullable
    public static ApplicationService getInstance() {
        return instance == null ? null : instance.get();
    }

    @Override
    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ApplicationService.class.getSimpleName(), "created");

        //Register Notification Update Receiver with Date Related Broadcasts
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

        receiver = new NotificationBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = new WeakReference<>(this);
        Log.d(ApplicationService.class.getSimpleName(), "started");

        registerReceiver(receiver, intentFilter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(ApplicationService.class.getSimpleName(), "destroyed");

        unregisterReceiver(receiver);
    }
}
