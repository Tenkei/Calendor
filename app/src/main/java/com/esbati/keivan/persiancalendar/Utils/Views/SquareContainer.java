package com.esbati.keivan.persiancalendar.Utils.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Tenkei on 12/12/2015.
 */
public class SquareContainer extends FrameLayout {
    public SquareContainer(Context context) {
        super(context);
    }

    public SquareContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        //if Width size is Unknown Scale with Height, else Scale with Width
        if(getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT)
            setMeasuredDimension(getMeasuredHeight(), getMeasuredHeight()); // Snap to height
        else
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); // Snap to width
    }
}