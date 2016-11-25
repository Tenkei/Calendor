package com.esbati.keivan.persiancalendar.Models;

import java.util.ArrayList;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/20/2016.
 */

public class CalendarDay {
    public int mDayNo;
    public boolean isToday;
    public boolean isHoliday;
    public boolean isCurrentMonth;
    public PersianCalendar mPersianDate;
    public ArrayList<CalendarEvent> mCalendarEvents;
    public ArrayList<GoogleEvent> mGoogleEvents;

    public CalendarDay(PersianCalendar persianCalendar){
        mPersianDate = persianCalendar;

        if(mPersianDate != null)
            mDayNo = mPersianDate.getPersianDay();
    }

    public CalendarDay(int day){
        mDayNo = day;
    }
}
