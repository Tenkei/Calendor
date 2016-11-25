package com.esbati.keivan.persiancalendar.Models;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/23/2016.
 */

public class GoogleEvent {
    public long   mID;
    public String mORGANIZER;
    public String mTITLE;
    public String mEVENT_LOCATION;
    public String mDESCRIPTION;
    public long mDTSTART;
    public long mDTEND;
    public String mEVENT_TIMEZONE;
    public String mEVENT_END_TIMEZONE;
    public String mDURATION;
    public String mALL_DAY;
    public String mRRULE;
    public String mRDATE;
    public PersianCalendar mStartDate;
    public PersianCalendar mEndDate;
    public GoogleCalendar mCalendar;

    public GoogleEvent(){}

    public GoogleEvent(String title, String desc, long startMillis){
        mTITLE = title;
        mDESCRIPTION = desc;
        mDTSTART = startMillis;
        mStartDate = new PersianCalendar(startMillis);
    }
}
