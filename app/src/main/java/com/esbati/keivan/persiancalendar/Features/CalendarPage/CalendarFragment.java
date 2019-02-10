package com.esbati.keivan.persiancalendar.Features.CalendarPage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esbati.keivan.persiancalendar.Components.SoundManager;
import com.esbati.keivan.persiancalendar.Features.Home.HomeFragment;
import com.esbati.keivan.persiancalendar.POJOs.CalendarDay;
import com.esbati.keivan.persiancalendar.POJOs.CalendarEvent;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Repository.CalendarHelper;
import com.esbati.keivan.persiancalendar.Repository.GoogleCalendarHelper;
import com.esbati.keivan.persiancalendar.Utils.Constants;

import java.util.ArrayList;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/17/2016.
 */

public class CalendarFragment extends Fragment {

    private final static String EXTRA_YEAR = "extra_year";
    private final static String EXTRA_MONTH = "extra_month";

    private int mYear;
    private int mMonth;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public void refreshCalendar(int newYear){
        mYear = newYear;

        mAdapter = new CalendarAdapter(mYear, mMonth, prepareDays(mYear, mMonth));
        ((CalendarAdapter) mAdapter).setOnCalendarClickListener(new CalendarAdapter.OnCalendarClickListener() {
            @Override
            public void onCalendarClick(CalendarDay day) {
                SoundManager.getInstance().playSound(day.mDayNo);
                ((HomeFragment) getParentFragment()).showDate(day, true, false);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            mYear = getArguments().getInt(EXTRA_YEAR);
            mMonth = getArguments().getInt(EXTRA_MONTH);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        setupView(rootView);
        return rootView;
    }

    public void setupView(View rootView){
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.list);
        //mRecyclerView.setNestedScrollingEnabled(false);

        mAdapter = new CalendarAdapter(mYear, mMonth, prepareDays(mYear, mMonth));
        ((CalendarAdapter) mAdapter).setOnCalendarClickListener(new CalendarAdapter.OnCalendarClickListener() {
            @Override
            public void onCalendarClick(CalendarDay day) {
                SoundManager.getInstance().playSound(day.mDayNo);
                ((HomeFragment) getParentFragment()).showDate(day, true, false);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new GridLayoutManager(getActivity(), 7, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public static CalendarFragment newInstance(int year, int month) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_YEAR, year);
        args.putInt(EXTRA_MONTH, month);

        CalendarFragment fragment = new CalendarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ArrayList<CalendarDay> prepareDays(int year, int month){

        PersianCalendar calendar = new PersianCalendar();
        boolean containToday = calendar.getPersianYear() == year && calendar.getPersianMonth() == month;
        int mToday = calendar.getPersianDay();
        calendar.setPersianDate(year, month, 1);
        boolean isLeapYear = calendar.isPersianLeapYear();
        int dayOfWeek = calendar.getPersianWeekDay() % 7;

        ArrayList<CalendarDay> days = new ArrayList<>();

        int currentMonthDays = Constants.daysOfMonth[(month - 1)];
        int previousMonthDays = Constants.daysOfMonth[(month - 2 + 12) % 12];

        //Add Extra Day in Case of Leap Year
        if(isLeapYear && month == 12)
            currentMonthDays++;

        //Add Extra Day to Previous Month in Case of Leap Year
        if(month == 1){
            PersianCalendar lastYearCalendar = new PersianCalendar();
            lastYearCalendar.setPersianDate(year - 1, month, 1);

            if(lastYearCalendar.isPersianLeapYear())
                previousMonthDays++;
        }

        //Add Trailing Days from Last Month if Needed
        if(dayOfWeek > 0)
            for(int i = dayOfWeek - 1 ; i >= 0 ; i--){
                days.add(new CalendarDay(previousMonthDays - i));
            }

        //Add Month Days
        for(int i = 1; i <= currentMonthDays ; i++){
            CalendarDay day = new CalendarDay(new PersianCalendar().setPersianDate(year, month, i));
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

            days.add(day);
        }

        //Add Leading Month Days
        for(int i = 1 ; i <= days.size() % 7 ; i++){
            days.add(new CalendarDay(i));
        }

        return days;
    }
}
