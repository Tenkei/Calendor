package com.esbati.keivan.persiancalendar.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esbati.keivan.persiancalendar.Adapters.CalendarAdapter;
import com.esbati.keivan.persiancalendar.Models.CalendarCell;
import com.esbati.keivan.persiancalendar.Models.Event;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Utils.CalendarHelper;
import com.esbati.keivan.persiancalendar.Utils.EventHelper;
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

    private ArrayList<CalendarCell> mDays;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public void refreshCalendar(int newYear){
        mYear = newYear;

        prepareDays();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            mYear = getArguments().getInt(EXTRA_YEAR);
            mMonth = getArguments().getInt(EXTRA_MONTH);
        }

        if(mDays == null || mDays.isEmpty()){
            prepareDays();
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

        mAdapter = new CalendarAdapter(getParentFragment(), mYear, mMonth, mDays);
        mRecyclerView.setAdapter(mAdapter);

        mLayoutManager = new GridLayoutManager(getActivity(), 7, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }


    public void prepareDays(){
        if(mDays == null)
            mDays = new ArrayList<>();
        else
            mDays.clear();

        //Calculate if This Month Contain Today
        PersianCalendar thisYearCalendar = new PersianCalendar();
        boolean containToday = thisYearCalendar.getPersianYear() == mYear && thisYearCalendar.getPersianMonth() == mMonth;
        int mToday = thisYearCalendar.getPersianDay();

        //Calculate First Day of Week
        thisYearCalendar.setPersianDate(mYear, mMonth, 1);
        boolean isLeapYear = thisYearCalendar.isPersianLeapYear();
        int dayOfWeek = thisYearCalendar.getPersianWeekDay() % 7;

        int currentMonthDays = Constants.daysOfMonth[(mMonth - 1)];
        int previousMonthDays = Constants.daysOfMonth[(mMonth - 2 + 12) % 12];

        //Add Extra Day in Case of Leap Year
        if(isLeapYear && mMonth == 12)
            currentMonthDays++;

        //Add Extra Day to Previous Month in Case of Leap Year
        if(mMonth == 1){
            PersianCalendar lastYearCalendar = new PersianCalendar();
            lastYearCalendar.setPersianDate(mYear - 1, mMonth, 1);

            if(lastYearCalendar.isPersianLeapYear())
                previousMonthDays++;
        }

        //Add Trailing Days from Last Month if Needed
        if(dayOfWeek > 0)
            for(int i = dayOfWeek - 1 ; i >= 0 ; i--){
                mDays.add(new CalendarCell(mYear, mMonth - 1, previousMonthDays - i));
            }

        //Add Month Days
        for(int i = 1; i <= currentMonthDays ; i++){
            PersianCalendar persianCalendar = new PersianCalendar(mYear, mMonth, i);
            CalendarCell day = new CalendarCell(mYear, mMonth, i);
            day.isToday = (i == mToday && containToday);
            day.isCurrentMonth = true;

            //Get Events for Current Day
            day.mEvents = EventHelper.getEvents(persianCalendar);
            day.mCalendarEvents = CalendarHelper.getEventsOfDay(persianCalendar);

            if(persianCalendar.getPersianWeekDay() == 6)
                day.isHoliday = true;
            else if(day.mEvents != null)
                for(Event event : day.mEvents)
                    if(event.isHoliday){
                        day.isHoliday = true;
                        break;
                    }

            mDays.add(day);
        }

        //Add Leading Month Days
        for(int i = 1 ; i <= mDays.size() % 7 ; i++){
            mDays.add(new CalendarCell(mYear, mMonth + 1, i));
        }
    }

    public static CalendarFragment newInstance(int year, int month) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_YEAR, year);
        args.putInt(EXTRA_MONTH, month);

        CalendarFragment fragment = new CalendarFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
