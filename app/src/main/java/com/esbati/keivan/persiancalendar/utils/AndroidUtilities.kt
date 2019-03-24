package com.esbati.keivan.persiancalendar.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Build
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

    @JvmStatic
    fun dp(value: Float): Int {
        return if (value == 0f) 0 else Math.ceil((density * value).toDouble()).toInt()
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

fun AlertDialog.Builder.showThemedDialog(): AlertDialog {
    val dialog = this.create()
    dialog.showThemedDialog()
    return dialog
}

fun AlertDialog.showThemedDialog() {
    show()
    applyDefaultTheme()
}

fun AlertDialog.applyDefaultTheme(): AlertDialog {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
        findViewById<TextView>(R.id.alertTitle)?.layoutDirection = View.LAYOUT_DIRECTION_RTL

    applyFont()
    return this
}

fun AlertDialog.applyFont(@FontRes title: Int = R.font.iransans_fa_num_bold,
                              @FontRes message: Int = R.font.iran_sans,
                              @FontRes buttons: Int = R.font.iran_sans): AlertDialog {

    findViewById<TextView>(R.id.alertTitle)?.typeface = ResourcesCompat.getFont(context, title)
    findViewById<TextView>(android.R.id.message)?.typeface = ResourcesCompat.getFont(context, message)
    findViewById<TextView>(android.R.id.button1)?.typeface = ResourcesCompat.getFont(context, buttons)
    findViewById<TextView>(android.R.id.button2)?.typeface = ResourcesCompat.getFont(context, buttons)
    findViewById<TextView>(android.R.id.button3)?.typeface = ResourcesCompat.getFont(context, buttons)

    return this
}



