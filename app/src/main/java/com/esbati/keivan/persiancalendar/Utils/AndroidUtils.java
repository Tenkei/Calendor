package com.esbati.keivan.persiancalendar.Utils;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.esbati.keivan.persiancalendar.ApplicationController;

/**
 * Created by asus on 11/25/2016.
 */

public class AndroidUtils {
    @TargetApi(17)
    public static void showCustomDialog(AlertDialog dialog) {
        dialog.show();

        //Set Title Gravity
        final int alertTitle = ApplicationController.getContext().getResources().getIdentifier("alertTitle", "id", "android");
        TextView messageText = (TextView) dialog.findViewById(alertTitle);

        messageText.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
}
