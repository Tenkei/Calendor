package com.esbati.keivan.persiancalendar.features.settings.cells

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.appcompat.widget.SwitchCompat
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.features.settings.ViewMarker
import com.esbati.keivan.persiancalendar.utils.LayoutHelper
import com.esbati.keivan.persiancalendar.utils.toDp

@ViewMarker
class TextCheckCell(context: Context) : FrameLayout(context) {

    private val textView: TextView
    private val checkBox: SwitchCompat
    var title: String
        get() = textView.text.toString()
        set(value) {
            textView.text = value
        }
    var isChecked: Boolean
        get() = checkBox.isChecked
        set(value) {
            checkBox.isChecked = value
        }
    var needDivider: Boolean = false
        set(value) {
            field = value
            setWillNotDraw(!value)
        }


    init {
        textView = TextView(context).apply {
            setTextColor(resources.getColorStateList(R.color.text_setting_key))
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            setLines(1)
            maxLines = 1
            setSingleLine(true)
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        }
        addView(textView, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat()
                , Gravity.RIGHT or Gravity.TOP
                , 17f, 0f, 17f, 0f)
        )

        checkBox = SwitchCompat(context).apply {
            isDuplicateParentStateEnabled = false
            isFocusable = false
            isFocusableInTouchMode = false
            isClickable = false
        }
        addView(checkBox, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT.toFloat()
                , Gravity.LEFT or Gravity.CENTER_VERTICAL
                , 14f, 0f, 14f, 0f)
        )
    }

    fun setTextAndCheck(text: String, checked: Boolean, divider: Boolean) {
        textView.text = text
        checkBox.isChecked = checked
        needDivider = divider
        setWillNotDraw(!divider)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(
                48.toDp() + if (needDivider) 1 else 0, View.MeasureSpec.EXACTLY
        ))
    }

    override fun onDraw(canvas: Canvas) {
        if (needDivider)
            canvas.drawLine(
                    paddingLeft.toFloat()
                    , (height - 1).toFloat()
                    , (width - paddingRight).toFloat()
                    , (height - 1).toFloat()
                    , paint
            )
    }

    override fun setEnabled(isEnabled: Boolean) {
        super.setEnabled(isEnabled)
        for (i in 0 until childCount)
            getChildAt(i).isEnabled = isEnabled
    }

    companion object {
        private val paint: Paint by lazy {
            Paint().apply {
                color = -0x262627
                strokeWidth = 1f
            }
        }
    }
}
