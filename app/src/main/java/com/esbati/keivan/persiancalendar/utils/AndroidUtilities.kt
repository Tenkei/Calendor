package com.esbati.keivan.persiancalendar.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.support.annotation.FontRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.components.ApplicationController
import java.util.*

object AndroidUtilities {

    private val density = ApplicationController.getContext().resources.displayMetrics.density
    private val typefaceCache = Hashtable<String, Typeface>()

    @JvmStatic
    fun dp(value: Float): Int {
        return if (value == 0f) 0 else Math.ceil((density * value).toDouble()).toInt()
    }

    @JvmStatic
    fun getTypeface(assetPath: String): Typeface? {
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

    @JvmStatic
    fun showSoftKeyboard(view: View?) {
        if (view != null) {
            val imm = ApplicationController.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    @JvmStatic
    fun hideSoftKeyboard(view: View?) {
        if (view != null) {
            val imm = ApplicationController.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @JvmStatic
    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = ApplicationController.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE))
            if (serviceClass.name == service.service.className)
                return true


        return false
    }
}

@SuppressLint("NewApi")
fun AlertDialog.setDefaultTheme(): AlertDialog {
    findViewById<TextView>(R.id.alertTitle)?.layoutDirection = View.LAYOUT_DIRECTION_RTL
    setDialogFont(
            title = R.font.iransans_fa_num_bold,
            buttons = R.font.iran_sans,
            message = R.font.iran_sans)
    return this
}

fun AlertDialog.setDialogFont(@FontRes title: Int = -1,
                              @FontRes message: Int = -1,
                              @FontRes buttons: Int = -1): AlertDialog {

    if (message != -1)
        findViewById<TextView>(android.R.id.message)?.typeface = ResourcesCompat.getFont(context, message)
    if (buttons != -1) {
        findViewById<TextView>(android.R.id.button1)?.typeface = ResourcesCompat.getFont(context, buttons)
        findViewById<TextView>(android.R.id.button2)?.typeface = ResourcesCompat.getFont(context, buttons)
        findViewById<TextView>(android.R.id.button3)?.typeface = ResourcesCompat.getFont(context, buttons)
    }
    if (title != -1)
        findViewById<TextView>(R.id.alertTitle)?.typeface = ResourcesCompat.getFont(context, title)

    return this
}

@SuppressLint("NewApi")
fun AlertDialog.showThemedDialog(): AlertDialog {
    show()
    //Set Title Gravity
    setDefaultTheme()
    return this
}

@SuppressLint("NewApi")
fun AlertDialog.Builder.showThemedDialog(): AlertDialog {
    val dialog = show()
    dialog.setDefaultTheme()
    return dialog
}



