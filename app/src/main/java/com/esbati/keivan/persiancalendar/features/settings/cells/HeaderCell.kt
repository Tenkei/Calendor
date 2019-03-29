package com.esbati.keivan.persiancalendar.features.settings.cells

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.esbati.keivan.persiancalendar.features.settings.ViewMarker
import com.esbati.keivan.persiancalendar.utils.LayoutHelper
import com.esbati.keivan.persiancalendar.utils.toDp

@ViewMarker
class HeaderCell(context: Context) : FrameLayout(context) {

    private val textView: TextView
    var title: String get() = textView.text.toString()
        set(value) {
            textView.text = value
        }

    init {
        textView = TextView(getContext()).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
            setTextColor(-0xc16f31)
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        }
        addView(textView, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT
                , LayoutHelper.MATCH_PARENT.toFloat()
                , Gravity.RIGHT or Gravity.TOP
                , 17f, 15f, 17f, 0f)
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(38.toDp(), View.MeasureSpec.EXACTLY))
    }
}
