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
import com.esbati.keivan.persiancalendar.R;

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

        mAdapter = new CalendarAdapter(getParentFragment(), mYear, mMonth);
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

        mAdapter = new CalendarAdapter(getParentFragment(), mYear, mMonth);
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
}
