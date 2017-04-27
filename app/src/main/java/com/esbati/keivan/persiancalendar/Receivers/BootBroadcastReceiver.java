package com.esbati.keivan.persiancalendar.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.esbati.keivan.persiancalendar.Models.CalendarDay;
import com.esbati.keivan.persiancalendar.Services.NotificationUpdateService;
import com.esbati.keivan.persiancalendar.Utils.NotificationHelper;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by Keivan Esbati on 4/10/2017.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_DATE_CHANGED:
                //Update Notification
                Intent notificationIntent = new Intent(context, NotificationUpdateService.class);
                context.startService(notificationIntent);

                //Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
        }
    }
}
