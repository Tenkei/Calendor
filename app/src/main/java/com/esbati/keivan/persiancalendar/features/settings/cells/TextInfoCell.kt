package com.esbati.keivan.persiancalendar.features.settings.cells

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.esbati.keivan.persiancalendar.utils.LayoutHelper
import com.esbati.keivan.persiancalendar.utils.toDp

class TextInfoCell(context: Context) : FrameLayout(context) {

    private val textView: TextView

    init {

        textView = TextView(context).apply {
            setTextColor(-0x5c5c5d)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
            gravity = Gravity.CENTER
            setPadding(0, 19.toDp(), 0, 19.toDp())
        }
        addView(textView, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT
                , LayoutHelper.WRAP_CONTENT.toFloat()
                , Gravity.CENTER
                , 17f, 0f, 17f, 0f)
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun setTextColor(textColor: Int) {
        textView.setTextColor(textColor)
    }
}
