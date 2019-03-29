package com.esbati.keivan.persiancalendar.features.settings.cells

import android.content.Context
import android.view.View
import android.view.View.MeasureSpec.*
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.features.settings.ViewMarker
import com.esbati.keivan.persiancalendar.utils.toDp

@ViewMarker
class ShadowSectionCell(context: Context) : View(context) {

    init {
        setBackgroundResource(R.drawable.greydivider)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
                makeMeasureSpec(getSize(widthMeasureSpec), EXACTLY)
                , makeMeasureSpec(12.toDp(), EXACTLY)
        )
    }
}
