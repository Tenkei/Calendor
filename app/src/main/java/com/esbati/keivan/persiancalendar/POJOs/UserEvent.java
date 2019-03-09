package com.esbati.keivan.persiancalendar.POJOs;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/23/2016.
 */

public class UserEvent {
    public long id;
    public String organizer;
    public String title;
    public String eventLocation;
    public String description;
    public long dtStart;
    public long dtEnd;
    public String eventTimezone;
    public String eventEndTimezone;
    public String duration;
    public String allDay;
    public String rRule;
    public String rDate;

    public PersianCalendar mStartDate;
    public PersianCalendar mEndDate;
    public DeviceCalendar mCalendar;

    public UserEvent(){}

    public UserEvent(CalendarDay selectedDay){
        dtStart = selectedDay.getMPersianDate().getTimeInMillis();
        mStartDate = new PersianCalendar(selectedDay.getMPersianDate().getTimeInMillis());
    }

    public UserEvent(String title, String desc, long startMillis){
        this.title = title;
        description = desc;
        dtStart = startMillis;
        mStartDate = new PersianCalendar(startMillis);
    }

    public UserEvent clone(){
        UserEvent tempEvent = new UserEvent();
        tempEvent.id = id;
        tempEvent.organizer = organizer;
        tempEvent.title = title;
        tempEvent.eventLocation = eventLocation;
        tempEvent.description = description;
        tempEvent.dtStart = dtStart;
        tempEvent.dtEnd = dtEnd;
        tempEvent.eventTimezone = eventTimezone;
        tempEvent.eventEndTimezone = eventEndTimezone;
        tempEvent.duration = duration;
        tempEvent.allDay = allDay;
        tempEvent.rRule = rRule;
        tempEvent.rDate = rDate;
        tempEvent.mStartDate = new PersianCalendar(dtStart);
        tempEvent.mEndDate = new PersianCalendar(dtEnd);
        tempEvent.mCalendar = mCalendar;

        return tempEvent;
    }
}
