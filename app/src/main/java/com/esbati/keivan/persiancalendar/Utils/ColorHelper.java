package com.esbati.keivan.persiancalendar.Utils;

import com.esbati.keivan.persiancalendar.Controllers.ApplicationController;
import com.esbati.keivan.persiancalendar.R;

/**
 * Created by asus on 11/21/2016.
 */

public class ColorHelper {

    private static int[] mSeasonalColorsResId = {
            R.color.seasonal_green,
            R.color.seasonal_yellow,
            R.color.seasonal_red,
            R.color.seasonal_blue
    };

    private static int[] mSeasonalDrawablesResId = {
            R.drawable.bg_calendar_spring,
            R.drawable.bg_calendar_summer,
            R.drawable.bg_calendar_fall,
            R.drawable.bg_calendar_winter
    };


    public static int getSeasonColor(int month){
        return ApplicationController.getContext().getResources().getColor(getSeasonColorResource(month));
    }

    public static int getSeasonColorResource(int month){
        int monthIndex = month - 1; //Convert Month Number to Index 0 - 11
        int seasonIndex = monthIndex / 3; //Convert Month Index to Season Index 0 - 3
        return mSeasonalColorsResId[seasonIndex];
    }

    public static int getSeasonDrawableResource(int month){
        int monthIndex = month - 1; //Convert Month Number to Index 0 - 11
        int seasonIndex = monthIndex / 3; //Convert Month Index to Season Index 0 - 3
        return mSeasonalDrawablesResId[seasonIndex];
    }
}
