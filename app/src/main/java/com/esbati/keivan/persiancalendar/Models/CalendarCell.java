package com.esbati.keivan.persiancalendar.Models;

import java.util.ArrayList;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/20/2016.
 */

public class CalendarCell {
    public int mDayNo;
    public boolean isToday;
    public boolean isHoliday;
    public boolean isCurrentMonth;
    public PersianCalendar mPersianDate;
    public ArrayList<Event> mEvents;
    public ArrayList<CalendarEvent> mCalendarEvents;

    public CalendarCell(){
        this(new PersianCalendar());
    }

    public CalendarCell(PersianCalendar persianCalendar){
        mPersianDate = persianCalendar;

        if(mPersianDate != null)
            mDayNo = mPersianDate.getPersianDay();
    }

    public CalendarCell(int year, int month, int day){
        mPersianDate = new PersianCalendar(year, month, day);
        mDayNo = day;
    }
}
