package com.esbati.keivan.persiancalendar.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esbati.keivan.persiancalendar.Models.CalendarDay;
import com.esbati.keivan.persiancalendar.Models.CalendarEvent;
import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities;
import com.esbati.keivan.persiancalendar.Utils.CalendarHelper;
import com.esbati.keivan.persiancalendar.Utils.ColorHelper;
import com.esbati.keivan.persiancalendar.Utils.Constants;
import com.esbati.keivan.persiancalendar.Fragments.HomeFragment;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Utils.GoogleCalendarHelper;
import com.esbati.keivan.persiancalendar.Utils.NotificationHelper;
import com.esbati.keivan.persiancalendar.Utils.SoundManager;

import java.util.ArrayList;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/17/2016.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayHolder> {

    private HomeFragment mFragment;
    private Context mContext;
    private int mDisplayedYear;
    private int mDisplayedMonth;
    private int mToday;
    private ArrayList<CalendarDay> mDays;

    private boolean containToday;
    private boolean isLeapYear;
    private int dayOfWeek;


    public CalendarAdapter(Fragment fragment, int year, int month) {
        super();

        mFragment = (HomeFragment) fragment;
        mContext = fragment.getActivity();
        mDisplayedYear = year;
        mDisplayedMonth = month;

        PersianCalendar thisYearCalendar = new PersianCalendar();
        containToday = thisYearCalendar.getPersianYear() == mDisplayedYear && thisYearCalendar.getPersianMonth() == mDisplayedMonth;
        mToday = thisYearCalendar.getPersianDay();

        thisYearCalendar.setPersianDate(mDisplayedYear, mDisplayedMonth, 1);
        isLeapYear = thisYearCalendar.isPersianLeapYear();
        dayOfWeek = thisYearCalendar.getPersianWeekDay() % 7;

        prepareDays();
    }

    public void prepareDays(){

        mDays = new ArrayList();

        int currentMonthDays = Constants.daysOfMonth[(mDisplayedMonth - 1)];
        int previousMonthDays = Constants.daysOfMonth[(mDisplayedMonth - 2 + 12) % 12];

        //Add Extra Day in Case of Leap Year
        if(isLeapYear && mDisplayedMonth == 12)
            currentMonthDays++;

        //Add Extra Day to Previous Month in Case of Leap Year
        if(mDisplayedMonth == 1){
            PersianCalendar lastYearCalendar = new PersianCalendar();
            lastYearCalendar.setPersianDate(mDisplayedYear - 1, mDisplayedMonth, 1);

            if(lastYearCalendar.isPersianLeapYear())
                previousMonthDays++;
        }

        //Add Trailing Days from Last Month if Needed
        if(dayOfWeek > 0)
            for(int i = dayOfWeek - 1 ; i >= 0 ; i--){
                mDays.add(new CalendarDay(previousMonthDays - i));
            }

        //Add Month Days
        for(int i = 1; i <= currentMonthDays ; i++){
            CalendarDay day = new CalendarDay(new PersianCalendar().setPersianDate(mDisplayedYear, mDisplayedMonth, i));
            day.isToday = (i == mToday && containToday);
            day.isCurrentMonth = true;

            //Get Events for Current Day
            day.mCalendarEvents = CalendarHelper.getEvents(day.mPersianDate);
            day.mGoogleEvents = GoogleCalendarHelper.getEvents(day.mPersianDate);

            if(day.mPersianDate.getPersianWeekDay() == 6)
                day.isHoliday = true;
            else if(day.mCalendarEvents != null)
                for(CalendarEvent calendarEvent : day.mCalendarEvents)
                    if(calendarEvent.isHoliday){
                        day.isHoliday = true;
                        break;
                    }

            mDays.add(day);
        }

        //Add Leading Month Days
        for(int i = 1 ; i <= mDays.size() % 7 ; i++){
            mDays.add(new CalendarDay(i));
        }
    }

    public static class DayHolder extends RecyclerView.ViewHolder{

        public TextView mCalendarDay;
        public TextView mGoogleEvent;
        public View mCalendarBackground;

        public DayHolder(View itemView) {
            super(itemView);

            mCalendarDay = (TextView)itemView.findViewById(R.id.calendar_day);
            mGoogleEvent = (TextView)itemView.findViewById(R.id.google_event);
            mCalendarBackground = itemView.findViewById(R.id.calendar_background);
        }
    }

    @Override
    public int getItemCount() {
        return mDays == null ? 0 : mDays.size();
    }

    @Override
    public DayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_calendar, parent, false);
        DayHolder dh = new DayHolder(view);
        return dh;
    }

    @Override
    public void onBindViewHolder(final DayHolder holder, final int position) {
        final CalendarDay mDay = mDays.get(position);
        holder.itemView.setEnabled(mDay.isCurrentMonth);

        holder.mCalendarDay.setText(String.valueOf(mDay.mDayNo));
        if(mDay.mGoogleEvents != null && mDay.mGoogleEvents.size() > 0){
            if(!TextUtils.isEmpty(mDay.mGoogleEvents.get(0).mTITLE))
                holder.mGoogleEvent.setText(mDay.mGoogleEvents.get(0).mTITLE);
            else
                holder.mGoogleEvent.setText(R.string.event_no_title);
        }

        //Set Background Color
        if(mDay.isHoliday && mDay.isToday){
            holder.mCalendarDay.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
            holder.itemView.setBackgroundResource(R.drawable.bg_calendar_today);

            holder.mGoogleEvent.setTextColor(mContext.getResources().getColor(android.R.color.white));
        } else if(mDay.isHoliday){
            holder.mCalendarDay.setTextColor(mContext.getResources().getColor(android.R.color.white));
            holder.itemView.setBackgroundResource(R.drawable.bg_calendar_holiday);

            holder.mGoogleEvent.setTextColor(mContext.getResources().getColor(android.R.color.white));
        } else if(mDay.isToday){
            holder.mCalendarDay.setTextColor(mContext.getResources().getColor(android.R.color.white));
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_dark));

            holder.mGoogleEvent.setTextColor(mContext.getResources().getColor(android.R.color.white));
        } else if(mDay.isCurrentMonth) {
            holder.mCalendarDay.setTextColor(mContext.getResources().getColor(android.R.color.black));
            holder.itemView.setBackgroundResource(ColorHelper.getSeasonDrawableResource(mDisplayedMonth));

            holder.mGoogleEvent.setTextColor(mContext.getResources().getColor(android.R.color.black));
        } else {
            holder.mCalendarDay.setTextColor(mContext.getResources().getColor(android.R.color.white));
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.lighter_gray));
            holder.itemView.setEnabled(false);

            holder.mGoogleEvent.setTextColor(mContext.getResources().getColor(android.R.color.white));
        }

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                //FIXME May Slow BottomSheet
                //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                //    holder.itemView.setElevation(AndroidUtilities.dp(isFocused ? 2 : 0));

                if(isFocused)
                    holder.itemView.performClick();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mDay.isCurrentMonth)
                    return;

                //FIXME Remove This Notification on Release
                NotificationHelper.showStickyNotification(mDay);
                SoundManager.getInstance().playSound(mDay.mDayNo);
                mFragment.showDate(mDay, true, false);
            }
        });
    }
}
