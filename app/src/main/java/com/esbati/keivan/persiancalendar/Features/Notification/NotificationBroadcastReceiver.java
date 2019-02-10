package com.esbati.keivan.persiancalendar.Features.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities;

/**
 * Created by Keivan Esbati on 4/10/2017.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String event = intent.getAction();
        if(!TextUtils.isEmpty(event)){
            Log.d("BroadcastReceiver", event);
        }

        //Update Notification
        Intent notificationIntent = new Intent(context, NotificationUpdateService.class);
        context.startService(notificationIntent);

        //Start Application Service if Not Running
        if (!AndroidUtilities.isServiceRunning(ApplicationService.class))
            context.startService(new Intent(context, ApplicationService.class));
    }
}
