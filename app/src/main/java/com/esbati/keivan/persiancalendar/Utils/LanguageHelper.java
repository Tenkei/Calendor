package com.esbati.keivan.persiancalendar.Utils;

import android.text.TextUtils;

/**
 * Created by asus on 4/16/2017.
 */

public class LanguageHelper {

    public static final char[] PERSIAN_DIGITS = {'۰', '۱', '۲', '۳', '۴', '۵', '۶',
            '۷', '۸', '۹'};

    public static String formatNumberInPersian(int number){
        return formatStringInPersian(Integer.toString(number));
    }

    public static String formatStringInPersian(String text){

        char[] formattedText = text.toCharArray();
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if(Character.isDigit(c))
                c = PERSIAN_DIGITS[Character.getNumericValue(c)];

            formattedText[i] = c;
        }

        return String.valueOf(formattedText);
    }


}
