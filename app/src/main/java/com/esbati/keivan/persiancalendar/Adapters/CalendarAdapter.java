package com.esbati.keivan.persiancalendar.Adapters;

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

import com.esbati.keivan.persiancalendar.Fragments.HomeFragment;
import com.esbati.keivan.persiancalendar.Models.CalendarCell;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Utils.ColorHelper;
import com.esbati.keivan.persiancalendar.Utils.PreferencesHelper;
import com.esbati.keivan.persiancalendar.Utils.SoundManager;

import java.util.ArrayList;

/**
 * Created by asus on 11/17/2016.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayHolder> {

    private HomeFragment mFragment;
    private Context mContext;
    private int mDisplayedYear;
    private int mDisplayedMonth;
    private ArrayList<CalendarCell> mDays;


    public CalendarAdapter(Fragment fragment, int year, int month, ArrayList<CalendarCell> days) {
        super();

        mFragment = (HomeFragment) fragment;
        mContext = fragment.getActivity();
        mDisplayedYear = year;
        mDisplayedMonth = month;
        mDays = days;
    }

    public class DayHolder extends RecyclerView.ViewHolder{

        public TextView mCalendarDay;
        public TextView mGoogleEvent;

        public DayHolder(View itemView) {
            super(itemView);

            //Set StateListAnimator if Needed
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                if(PreferencesHelper.isOptionActive(PreferencesHelper.KEY_ANIMATION_SELECTION, true))
                    itemView.setStateListAnimator(AnimatorInflater.loadStateListAnimator(
                            itemView.getContext()
                            , R.animator.cell_animator)
                    );

            //Setup Views
            mCalendarDay = (TextView)itemView.findViewById(R.id.calendar_day);
            mGoogleEvent = (TextView)itemView.findViewById(R.id.google_event);
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
        final CalendarCell mDay = mDays.get(position);

        holder.itemView.setEnabled(mDay.isCurrentMonth);
        holder.mCalendarDay.setText(String.valueOf(mDay.mDayNo));

        //Set Events
        if(mDay.mCalendarEvents != null && mDay.mCalendarEvents.size() > 0){
            if(!TextUtils.isEmpty(mDay.mCalendarEvents.get(0).mTITLE))
                holder.mGoogleEvent.setText(mDay.mCalendarEvents.get(0).mTITLE);
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
            holder.itemView.setBackgroundResource(R.drawable.bg_calendar_today);

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
                if(isFocused)
                    holder.itemView.performClick();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mDay.isCurrentMonth)
                    return;

                SoundManager.getInstance().playSound(mDay.mDayNo);
                mFragment.showDate(mDay, true, false);
            }
        });

    }
}
