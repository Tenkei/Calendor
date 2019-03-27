package com.esbati.keivan.persiancalendar.features.settings.cells

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.features.settings.ViewMarker
import com.esbati.keivan.persiancalendar.utils.LayoutHelper
import com.esbati.keivan.persiancalendar.utils.toDp

@ViewMarker
class TextSettingsCell(context: Context) : FrameLayout(context) {

    private val textView: TextView
    private val valueTextView: TextView
    private val valueImageView: ImageView
    private var needDivider: Boolean = false

    init {
        textView = TextView(context).apply {
            setTextColor(resources.getColorStateList(R.color.text_setting_key))
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            setLines(1)
            maxLines = 1
            setSingleLine(true)
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        }
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat(), Gravity.RIGHT or Gravity.TOP, 17f, 0f, 17f, 0f))

        valueTextView = TextView(context).apply {
            setTextColor(resources.getColorStateList(R.color.text_setting_value))
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            setLines(1)
            maxLines = 1
            setSingleLine(true)
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
        }
        addView(valueTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT.toFloat(), Gravity.LEFT or Gravity.TOP, 17f, 0f, 17f, 0f))

        valueImageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER
            visibility = View.INVISIBLE
        }
        addView(valueImageView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT.toFloat(), Gravity.LEFT or Gravity.CENTER_VERTICAL, 17f, 0f, 17f, 0f))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), 48.toDp() + if (needDivider) 1 else 0)

        val availableWidth = measuredWidth - paddingLeft - paddingRight - 34.toDp()
        var width = availableWidth / 2
        if (valueImageView.visibility == View.VISIBLE)
            valueImageView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(measuredHeight, View.MeasureSpec.EXACTLY))

        if (valueTextView.visibility == View.VISIBLE) {
            valueTextView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(measuredHeight, View.MeasureSpec.EXACTLY))
            width = availableWidth - valueTextView.measuredWidth - 8.toDp()
        } else {
            width = availableWidth
        }
        textView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(measuredHeight, View.MeasureSpec.EXACTLY))
    }

    fun setTextColor(color: Int) {
        textView.setTextColor(color)
    }

    fun setText(text: String, divider: Boolean) {
        textView.text = text
        valueTextView.visibility = View.INVISIBLE
        valueImageView.visibility = View.INVISIBLE
        needDivider = divider
        setWillNotDraw(!divider)
    }

    fun setTextAndValue(text: String, value: String?, divider: Boolean) {
        textView.text = text
        valueImageView.visibility = View.INVISIBLE
        if (value != null) {
            valueTextView.text = value
            valueTextView.visibility = View.VISIBLE
        } else {
            valueTextView.visibility = View.INVISIBLE
        }
        needDivider = divider
        setWillNotDraw(!divider)
        requestLayout()
    }

    fun setTextAndIcon(text: String, resId: Int, divider: Boolean) {
        textView.text = text
        valueTextView.visibility = View.INVISIBLE
        if (resId != 0) {
            valueImageView.visibility = View.VISIBLE
            valueImageView.setImageResource(resId)
        } else {
            valueImageView.visibility = View.INVISIBLE
        }
        needDivider = divider
        setWillNotDraw(!divider)
    }

    override fun onDraw(canvas: Canvas) {
        if (needDivider) {
            canvas.drawLine(paddingLeft.toFloat(), (height - 1).toFloat(), (width - paddingRight).toFloat(), (height - 1).toFloat(), paint!!)
        }
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
