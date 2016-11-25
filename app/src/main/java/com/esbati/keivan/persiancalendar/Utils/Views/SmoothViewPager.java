package com.esbati.keivan.persiancalendar.Utils.Views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by asus on 11/18/2016.
 */

public class SmoothViewPager extends ViewPager {

    Field mMyScroller;

    private static final Interpolator sInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    public SmoothViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        initScroller();
    }

    public void initScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            mMyScroller = viewpager.getDeclaredField("mScroller");
            mMyScroller.setAccessible(true);

            mMyScroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMyScroller(boolean isSmooth) {
        try {
            if(isSmooth)
                mMyScroller.set(this, new MyScroller(getContext()));
            else
                mMyScroller.set(this, new Scroller(getContext(), sInterpolator));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyScroller extends Scroller {
        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 500 /*0.5 secs*/);
        }
    }
}