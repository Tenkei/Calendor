package com.esbati.keivan.persiancalendar.Utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.esbati.keivan.persiancalendar.Components.ApplicationController;

import java.util.Hashtable;
import java.util.List;

public class AndroidUtilities {

    public static int statusBarHeight = 0;
    public static float density = 1;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static Point displaySize = new Point();
    public static boolean isTablet;
    public static int leftBaseline;
    public static boolean usingHardwareInput;
    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable<>();

    static {
        statusBarHeight = getStatusBarHeight();
        density = ApplicationController.getContext().getResources().getDisplayMetrics().density;
        //leftBaseline = isTablet() ? 80 : 72;
        leftBaseline = 80;
        checkDisplaySize();
    }


    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }
    public static float dpf2(float value) {
        if (value == 0) {
            return 0;
        }
        return density * value;
    }
    public static Typeface getTypeface(String assetPath) {
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(ApplicationController.getContext().getAssets(), assetPath);
                    typefaceCache.put(assetPath, t);
                } catch (Exception e) {
                    Log.e("Typefaces", "Could not get typeface '" + assetPath + "' because " + e.getMessage());
                    return null;
                }
            }
            return typefaceCache.get(assetPath);
        }
    }
    public static Point checkDisplaySize() {
        Point tempDisplaySize = new Point();
        try {
            Configuration configuration = ApplicationController.getContext().getResources().getConfiguration();
            usingHardwareInput = configuration.keyboard != Configuration.KEYBOARD_NOKEYS && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
            WindowManager manager = (WindowManager) ApplicationController.getContext().getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                    tempDisplaySize = displaySize;
                    Log.d("tmessages", "display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
                }
            }
        } catch (Exception e) {
            Log.e("tmessages", e.getMessage(), e);
        }
        return tempDisplaySize;
    }
    public static int getStatusBarHeight() {
        if(statusBarHeight <= 0) {
            int resourceId = ApplicationController.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = ApplicationController.getContext().getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusBarHeight;
    }
    public static boolean isTablet() {
        return isTablet;
    }
    public static boolean isSmallTablet() {
        float minSide = Math.min(displaySize.x, displaySize.y) / density;
        return minSide <= 700;
    }
    public static void showSoftKeyboard(View view){
        showSoftKeyboard(view , InputMethodManager.SHOW_IMPLICIT);
    }
    public static void showSoftKeyboard(View view, int mode){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) ApplicationController.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, mode);
        }
    }
    public static void toggleSoftKeyboard(int mode){
        InputMethodManager imm = (InputMethodManager) ApplicationController.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(mode, 0);
    }
    public static void hideSoftKeyboard(View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)ApplicationController.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static boolean isIntentAvailable(Intent intent) {
        final PackageManager mgr = ApplicationController.getContext().getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
    public static void setBadge(int count) {
        final String launcherClassName = getLauncherClassName();
        if (launcherClassName == null) {
            return;
        }
        final Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count > 0? count : null);
        intent.putExtra("badge_count_package_name", ApplicationController.getContext().getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        ApplicationController.getContext().sendBroadcast(intent);
    }
    private static String getLauncherClassName() {
        final PackageManager pm = ApplicationController.getContext().getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (final ResolveInfo resolveInfo : resolveInfos) {
            final String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(ApplicationController.getContext().getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }
    public static boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ApplicationController.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(17)
    public static void showRTLDialog(AlertDialog dialog){
        dialog.show();
        //Set Title Gravity
        final int alertTitle = ApplicationController.getContext().getResources().getIdentifier("alertTitle", "id", "android");
        TextView messageText = (TextView) dialog.findViewById(alertTitle);
        messageText.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
}
