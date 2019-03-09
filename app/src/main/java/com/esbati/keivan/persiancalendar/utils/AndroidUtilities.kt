package com.esbati.keivan.persiancalendar.utils

import android.annotation.TargetApi
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

import com.esbati.keivan.persiancalendar.components.ApplicationController

import java.util.Hashtable

object AndroidUtilities {

    private val density = ApplicationController.getContext().resources.displayMetrics.density
    private val typefaceCache = Hashtable<String, Typeface>()

    @JvmStatic fun dp(value: Float): Int {
        return if (value == 0f) 0 else Math.ceil((density * value).toDouble()).toInt()
    }

    @JvmStatic  fun getTypeface(assetPath: String): Typeface? {
        synchronized(typefaceCache) {
            if (!typefaceCache.containsKey(assetPath)) {
                try {
                    val t = Typeface.createFromAsset(ApplicationController.getContext().assets, assetPath)
                    typefaceCache[assetPath] = t
                } catch (e: Exception) {
                    Log.e("Typefaces", "Could not get typeface '" + assetPath + "' because " + e.message)
                    return null
                }

            }
            return typefaceCache[assetPath]
        }
    }

    @JvmStatic fun showSoftKeyboard(view: View?) {
        if (view != null) {
            val imm = ApplicationController.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    @JvmStatic fun hideSoftKeyboard(view: View?) {
        if (view != null) {
            val imm = ApplicationController.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @JvmStatic fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = ApplicationController.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE))
            if (serviceClass.name == service.service.className)
                return true


        return false
    }

    @TargetApi(17)
    @JvmStatic fun showRTLDialog(dialog: AlertDialog) {
        dialog.show()
        //Set Title Gravity
        val alertTitle = ApplicationController.getContext().resources.getIdentifier("alertTitle", "id", "android")
        val messageText = dialog.findViewById<TextView>(alertTitle)
        messageText.layoutDirection = View.LAYOUT_DIRECTION_RTL
    }
}
