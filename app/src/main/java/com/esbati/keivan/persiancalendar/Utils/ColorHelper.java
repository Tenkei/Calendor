package com.esbati.keivan.persiancalendar.Utils;

import com.esbati.keivan.persiancalendar.Controllers.ApplicationController;
import com.esbati.keivan.persiancalendar.R;

/**
 * Created by asus on 11/21/2016.
 */

public class ColorHelper {

    public static int getSeasonColor(int month){
        return ApplicationController.getContext().getResources().getColor(getSeasonColorResource(month));
    }

    public static int getSeasonColorResource(int month){
        switch (month) {
            case 1:
            case 2:
            case 3:
                return R.color.seasonal_green;
            case 4:
            case 5:
            case 6:
                return R.color.seasonal_yellow;
            case 7:
            case 8:
            case 9:
                return R.color.seasonal_red;
            case 10:
            case 11:
            case 12:
            default:
                return R.color.seasonal_blue;
        }
    }

    public static int getSeasonDrawableResource(int month){
        switch (month) {
            case 1:
            case 2:
            case 3:
                return R.drawable.bg_calendar_spring;
            case 4:
            case 5:
            case 6:
                return R.drawable.bg_calendar_summer;
            case 7:
            case 8:
            case 9:
                return R.drawable.bg_calendar_fall;
            case 10:
            case 11:
            case 12:
            default:
                return R.drawable.bg_calendar_winter;
        }
    }
}
