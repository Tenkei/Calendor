/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package com.esbati.keivan.persiancalendar.components.views;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.utils.AndroidUtilities;
import com.esbati.keivan.persiancalendar.utils.LayoutHelper;

public class HeaderCell extends FrameLayout {

    private TextView textView;

    public HeaderCell(Context context) {
        super(context);

        textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.iran_sans));
        textView.setTextColor(0xff3e90cf);
        textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.RIGHT | Gravity.TOP, 17, 15, 17, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(38), MeasureSpec.EXACTLY));
    }

    public void setText(String text) {
        textView.setText(text);
    }
}
