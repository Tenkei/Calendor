package com.esbati.keivan.persiancalendar

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.internal.util.Checks
import org.hamcrest.Description
import org.hamcrest.Matcher


fun withTextColor(@ColorRes color: Int): Matcher<View> {
    Checks.checkNotNull(color)
    return object : BoundedMatcher<View, TextView>(TextView::class.java) {
        public override fun matchesSafely(view: TextView): Boolean {
            return view.context.getColor(color) == view.currentTextColor
        }

        override fun describeTo(description: Description) {
            description.appendText("with text color: ")
        }
    }
}

fun withBackground(res: Int): Matcher<View> {
    Checks.checkNotNull(res)
    return object : BoundedMatcher<View, View>(View::class.java) {
        public override fun matchesSafely(view: View): Boolean {
            val drawable = view.context.getDrawable(res)
            val background = view.background
            return drawable != null && background.sameAs(drawable)
        }

        override fun describeTo(description: Description) {
            description.appendText("with background color: ")
        }
    }
}