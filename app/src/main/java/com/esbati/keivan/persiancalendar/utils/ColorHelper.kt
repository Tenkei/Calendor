package com.esbati.keivan.persiancalendar.utils

import android.content.Context
import com.esbati.keivan.persiancalendar.R

/**
 * Created by Keivan Esbati on 11/21/2016.
 */

object ColorHelper {

    private val mSeasonalColorsResId = intArrayOf(
            R.color.seasonal_green
            , R.color.seasonal_yellow
            , R.color.seasonal_orange
            , R.color.seasonal_blue
    )

    private val mSeasonalDrawablesResId = intArrayOf(
            R.drawable.bg_calendar_spring
            , R.drawable.bg_calendar_summer
            , R.drawable.bg_calendar_fall
            , R.drawable.bg_calendar_winter
    )

    fun getSeasonColor(month: Int, context: Context): Int {
        return context.resources.getColor(getSeasonColorResource(month))
    }

    fun getSeasonColorResource(month: Int): Int {
        val monthIndex = month - 1 //Convert Month Number to Index 0 - 11
        val seasonIndex = monthIndex / 3 //Convert Month Index to Season Index 0 - 3
        return mSeasonalColorsResId[seasonIndex]
    }

    fun getSeasonDrawableResource(month: Int): Int {
        val monthIndex = month - 1 //Convert Month Number to Index 0 - 11
        val seasonIndex = monthIndex / 3 //Convert Month Index to Season Index 0 - 3
        return mSeasonalDrawablesResId[seasonIndex]
    }
}
