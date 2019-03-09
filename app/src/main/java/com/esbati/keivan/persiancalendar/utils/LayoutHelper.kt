/*
 * This is the source code of Telegram for Android v. 3.x.x
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package com.esbati.keivan.persiancalendar.utils

import android.widget.FrameLayout

object LayoutHelper {

    const val MATCH_PARENT = -1
    const val WRAP_CONTENT = -2

    private fun getSize(size: Float): Int = if (size < 0) size.toInt() else AndroidUtilities.dp(size)

    @JvmStatic fun createFrame(width: Int, height: Float, gravity: Int, leftMargin: Float, topMargin: Float, rightMargin: Float, bottomMargin: Float): FrameLayout.LayoutParams {
        val layoutParams = FrameLayout.LayoutParams(getSize(width.toFloat()), getSize(height), gravity)
        layoutParams.setMargins(AndroidUtilities.dp(leftMargin), AndroidUtilities.dp(topMargin), AndroidUtilities.dp(rightMargin), AndroidUtilities.dp(bottomMargin))
        return layoutParams
    }
}
