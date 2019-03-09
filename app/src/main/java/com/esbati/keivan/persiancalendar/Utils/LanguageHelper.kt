package com.esbati.keivan.persiancalendar.Utils

/**
 * Created by asus on 4/16/2017.
 */

object LanguageHelper {
    private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun formatNumberInPersian(number: Int) = formatStringInPersian(Integer.toString(number))

    fun formatStringInPersian(text: String): String {
        val formattedText = text.toCharArray()
        for (i in 0 until text.length) {
            var c = text[i]
            if (Character.isDigit(c))
                c = PERSIAN_DIGITS[Character.getNumericValue(c)]

            formattedText[i] = c
        }

        return String(formattedText)
    }
}
