package com.esbati.keivan.persiancalendar.Utils;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.esbati.keivan.persiancalendar.Controllers.ApplicationController;

/**
 * Created by Esbati on 2/13/2016.
 */
public class PreferencesHelper {

    //Setting Toggles Keys
    public final static String KEY_ANIMATION_SELECTION = "showSelectionAnimation";
    public final static String KEY_NOTIFICATION_SHOW = "showNotification";
    public final static String KEY_NOTIFICATION_ACTIONS = "showNotificationAction";
    public final static String KEY_NOTIFICATION_PRIORITY = "notificationPriority";


    public static boolean isOptionActive(String key, boolean defaultValue){
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(ApplicationController.getContext());

        return settings.getBoolean(key, defaultValue);
    }

    public static void setOption(String key, boolean isActive){
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(ApplicationController.getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, isActive);
        editor.commit();
    }

    public static boolean toggleOption(String key, boolean defaultValue){
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(ApplicationController.getContext());
        SharedPreferences.Editor editor = settings.edit();

        boolean value = settings.getBoolean(key, defaultValue);
        editor.putBoolean(key, !value);
        editor.commit();

        return !value;
    }

    public static String loadString(String key, String defaultValue){
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(ApplicationController.getContext());
        String value = settings.getString(key, defaultValue);

        return value;
    }

    public static void saveString(String key, String value){
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(ApplicationController.getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static int loadInt(String key, int defaultValue){
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(ApplicationController.getContext());
        int intValue = settings.getInt(key, defaultValue);

        return intValue;
    }

    public static void saveInt(String key, int intValue){
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(ApplicationController.getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, intValue);
        editor.commit();
    }
}
