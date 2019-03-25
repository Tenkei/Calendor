/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package com.esbati.keivan.persiancalendar.components.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.SwitchCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.utils.LayoutHelper;

import static com.esbati.keivan.persiancalendar.utils.AndroidUtilitiesKt.toDp;

public class TextCheckCell extends FrameLayout {

    private TextView textView;
    private SwitchCompat checkBox;
    private static Paint paint;
    private boolean needDivider;

    public TextCheckCell(Context context) {
        super(context);

        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        textView = new TextView(context);
        textView.setTextColor(getResources().getColorStateList(R.color.text_setting_key));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.RIGHT | Gravity.TOP, 17, 0, 17, 0));

        checkBox = new SwitchCompat(context);
        checkBox.setDuplicateParentStateEnabled(false);
        checkBox.setFocusable(false);
        checkBox.setFocusableInTouchMode(false);
        checkBox.setClickable(false);
        addView(checkBox, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 14, 0, 14, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(toDp(48) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
    }

    public void setTextAndCheck(String text, boolean checked, boolean divider) {
        textView.setText(text);
        if (Build.VERSION.SDK_INT < 11) {
            checkBox.requestLayout();
        }
        checkBox.setChecked(checked);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }

    @Override
    public void setEnabled(boolean isEnabled){
        super.setEnabled(isEnabled);
        for(int i = 0 ; i < getChildCount() ; i++)
            getChildAt(i).setEnabled(isEnabled);
    }
}
