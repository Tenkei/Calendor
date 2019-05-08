package com.esbati.keivan.persiancalendar

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable


fun Drawable.sameAs(drawable: Drawable): Boolean {
    return this.getBitmap().sameAs(drawable.getBitmap())
}

fun RippleDrawable.sameAs(drawable: Drawable): Boolean {
    return drawable is RippleDrawable && this.getDefaultColor() == drawable.getDefaultColor()
}

fun Drawable.getBitmap(): Bitmap {
    val result: Bitmap
    if (this is BitmapDrawable) {
        result = this.bitmap
    } else {
        // Some drawables have no intrinsic width - e.g. solid colours.
        val width = Math.max(this.intrinsicWidth, 10)
        val height = Math.max(this.intrinsicHeight, 10)

        result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        this.setBounds(0, 0, canvas.width, canvas.height)
        this.draw(canvas)
    }

    return result
}

fun RippleDrawable.getDefaultColor(): Int {
    var rippleColor: Int = -1
    try {
        val colorField = constantState::class.java.getDeclaredField("mColor")
        colorField.isAccessible = true
        val colorStateList = colorField.get(constantState) as ColorStateList
        rippleColor = colorStateList.defaultColor
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }

    return rippleColor
}