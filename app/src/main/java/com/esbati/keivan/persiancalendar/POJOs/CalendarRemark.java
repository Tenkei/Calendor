package com.esbati.keivan.persiancalendar.POJOs;

import org.json.JSONException;
import org.json.JSONObject;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/21/2016.
 */

public class CalendarRemark {

    public String mTitle;
    public int mYear;
    public int mMonth;
    public int mDay;
    public boolean isHoliday;
    public PersianCalendar mPersianDate;

    public CalendarRemark(){

    }

    public CalendarRemark fromJSON(JSONObject eventJSON) throws JSONException{
        mTitle = eventJSON.getString("title");
        mYear = eventJSON.optInt("year", -1);
        mMonth = eventJSON.getInt("month");
        mDay = eventJSON.getInt("day");
        isHoliday = eventJSON.getBoolean("holiday");
        mPersianDate = new PersianCalendar().setPersianDate(mYear, mMonth, mDay);
        return this;
    }
}
