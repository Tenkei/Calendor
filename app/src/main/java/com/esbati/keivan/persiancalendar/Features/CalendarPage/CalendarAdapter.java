package com.esbati.keivan.persiancalendar.Features.CalendarPage;

import android.animation.AnimatorInflater;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esbati.keivan.persiancalendar.POJOs.CalendarDay;
import com.esbati.keivan.persiancalendar.POJOs.CalendarEvent;
import com.esbati.keivan.persiancalendar.Repository.CalendarHelper;
import com.esbati.keivan.persiancalendar.Utils.ColorHelper;
import com.esbati.keivan.persiancalendar.Utils.Constants;
import com.esbati.keivan.persiancalendar.Features.Home.HomeFragment;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Repository.GoogleCalendarHelper;
import com.esbati.keivan.persiancalendar.Repository.PreferencesHelper;
import com.esbati.keivan.persiancalendar.Components.SoundManager;

import java.util.ArrayList;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/17/2016.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayHolder> {

    private int mDisplayedYear;
    private int mDisplayedMonth;
    private ArrayList<CalendarDay> mDays;
    private OnCalendarClickListener mListener;


    public interface OnCalendarClickListener {
        void onCalendarClick(CalendarDay day);
    }

    void setOnCalendarClickListener(OnCalendarClickListener listener) {
        this.mListener = listener;
    }

    CalendarAdapter(int year, int month, ArrayList<CalendarDay> days) {
        super();

        mDisplayedYear = year;
        mDisplayedMonth = month;

        mDays = days;
    }

    class DayHolder extends RecyclerView.ViewHolder{

        TextView mCalendarDay;
        TextView mGoogleEvent;

        DayHolder(final View itemView) {
            super(itemView);

            mCalendarDay = (TextView)itemView.findViewById(R.id.calendar_day);
            mGoogleEvent = (TextView)itemView.findViewById(R.id.google_event);

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean isFocused) {
                    if(isFocused)
                        itemView.performClick();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CalendarDay day = mDays.get(getAdapterPosition());
                    if(!day.isCurrentMonth)
                        return;

                    if(mListener != null)
                        mListener.onCalendarClick(day);
                }
            });
        }

        void setupView(CalendarDay day){
            Context context = itemView.getContext();
            itemView.setEnabled(day.isCurrentMonth);

            mCalendarDay.setText(String.valueOf(day.mDayNo));
            if(day.mGoogleEvents != null && day.mGoogleEvents.size() > 0){
                if(!TextUtils.isEmpty(day.mGoogleEvents.get(0).mTITLE))
                    mGoogleEvent.setText(day.mGoogleEvents.get(0).mTITLE);
                else
                    mGoogleEvent.setText(R.string.event_no_title);
            }

            //Set Background Color
            if(day.isHoliday && day.isToday){
                mCalendarDay.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                itemView.setBackgroundResource(R.drawable.bg_calendar_today);

                mGoogleEvent.setTextColor(context.getResources().getColor(android.R.color.white));
            } else if(day.isHoliday){
                mCalendarDay.setTextColor(context.getResources().getColor(android.R.color.white));
                itemView.setBackgroundResource(R.drawable.bg_calendar_holiday);

                mGoogleEvent.setTextColor(context.getResources().getColor(android.R.color.white));
            } else if(day.isToday){
                mCalendarDay.setTextColor(context.getResources().getColor(android.R.color.white));
                itemView.setBackgroundResource(R.drawable.bg_calendar_today);

                mGoogleEvent.setTextColor(context.getResources().getColor(android.R.color.white));
            } else if(day.isCurrentMonth) {
                mCalendarDay.setTextColor(context.getResources().getColor(android.R.color.black));
                itemView.setBackgroundResource(ColorHelper.getSeasonDrawableResource(mDisplayedMonth));

                mGoogleEvent.setTextColor(context.getResources().getColor(android.R.color.black));
            } else {
                mCalendarDay.setTextColor(context.getResources().getColor(android.R.color.white));
                itemView.setBackgroundColor(context.getResources().getColor(R.color.lighter_gray));

                itemView.setEnabled(false);
                mGoogleEvent.setTextColor(context.getResources().getColor(android.R.color.white));
            }
        }
    }
    @Override
    public DayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_calendar, parent, false);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            if(PreferencesHelper.isOptionActive(PreferencesHelper.KEY_ANIMATION_SELECTION, false))
                view.setStateListAnimator(AnimatorInflater.loadStateListAnimator(
                        parent.getContext()
                        , R.animator.cell_animator)
                );

        return new DayHolder(view);
    }

    @Override
    public void onBindViewHolder(final DayHolder holder, final int position) {
        CalendarDay day = mDays.get(position);
        holder.setupView(day);
    }

    @Override
    public int getItemCount() {
        return mDays == null ? 0 : mDays.size();
    }
}
