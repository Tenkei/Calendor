/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package com.esbati.keivan.persiancalendar.components.views;

import android.content.Context;
import android.view.View;

import com.esbati.keivan.persiancalendar.R;

import static com.esbati.keivan.persiancalendar.utils.AndroidUtilitiesKt.toDp;

public class ShadowSectionCell extends View {

    public ShadowSectionCell(Context context) {
        super(context);
        setBackgroundResource(R.drawable.greydivider);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(toDp(12), MeasureSpec.EXACTLY));
    }
}
