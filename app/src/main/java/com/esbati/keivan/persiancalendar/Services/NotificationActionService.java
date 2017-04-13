package com.esbati.keivan.persiancalendar.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.esbati.keivan.persiancalendar.Utils.NotificationHelper;

/**
 * Created by Keivan Esbati on 4/9/2017.
 */

public class NotificationActionService extends IntentService {

    public final static String EXTRA_ACTION = "extra_action";
    public final static int ACTION_DISMISS = 0;

    public NotificationActionService() {
        super("NotificationActionService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int mActionType = intent.getIntExtra(EXTRA_ACTION, -1);

        if(mActionType >= 0)
            switch (mActionType){
                case 0:
                    //Dismiss Notification
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(NotificationHelper.NOTIFICATION_ID);
                    break;
            }
    }
}
