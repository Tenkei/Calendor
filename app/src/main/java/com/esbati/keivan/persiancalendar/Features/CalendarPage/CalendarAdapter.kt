package com.esbati.keivan.persiancalendar.Features.CalendarPage

import android.animation.AnimatorInflater
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.esbati.keivan.persiancalendar.POJOs.CalendarDay
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.Repository.PreferencesHelper
import com.esbati.keivan.persiancalendar.Utils.ColorHelper

class CalendarAdapter(val year: Int, val month: Int, days: List<CalendarDay>): RecyclerView.Adapter<CalendarAdapter.DayHolder>(){

    var onCalendarClickListener: OnCalendarClickListener? = null
    val calendarDays = ArrayList<CalendarDay>(days)

    interface OnCalendarClickListener{
        fun onCalendarClick(day: CalendarDay)
    }

    inner class DayHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private val mCalendarDay = itemView.findViewById(R.id.calendar_day) as TextView
        private val mGoogleEvent = itemView.findViewById(R.id.google_event) as TextView

        init {
            itemView.onFocusChangeListener = View.OnFocusChangeListener { view, isFocused ->
                if(isFocused) view.performClick()
            }

            itemView.setOnClickListener {
                val selectedDay = calendarDays[adapterPosition]
                if (selectedDay.isCurrentMonth)
                    onCalendarClickListener?.onCalendarClick(selectedDay)
            }
        }

        fun setupView(day: CalendarDay) {
            val context = itemView.context
            itemView.isEnabled = day.isCurrentMonth

            mCalendarDay.text = day.mDayNo.toString()
            if (day.mEvents != null && day.mEvents.size > 0) {
                if (!TextUtils.isEmpty(day.mEvents[0].title))
                    mGoogleEvent.text = day.mEvents[0].title
                else
                    mGoogleEvent.setText(R.string.event_no_title)
            }

            //Set Background Color
            when {
                day.isHoliday && day.isToday -> {
                    mCalendarDay.setTextColor(context.resources.getColor(android.R.color.holo_red_dark))
                    itemView.setBackgroundResource(R.drawable.bg_calendar_today)
                    mGoogleEvent.setTextColor(context.resources.getColor(android.R.color.white))
                }

                day.isHoliday -> {
                    mCalendarDay.setTextColor(context.resources.getColor(android.R.color.white))
                    itemView.setBackgroundResource(R.drawable.bg_calendar_holiday)
                    mGoogleEvent.setTextColor(context.resources.getColor(android.R.color.white))
                }

                day.isToday -> {
                    mCalendarDay.setTextColor(context.resources.getColor(android.R.color.white))
                    itemView.setBackgroundResource(R.drawable.bg_calendar_today)
                    mGoogleEvent.setTextColor(context.resources.getColor(android.R.color.white))
                }

                day.isCurrentMonth -> {
                    mCalendarDay.setTextColor(context.resources.getColor(android.R.color.black))
                    itemView.setBackgroundResource(ColorHelper.getSeasonDrawableResource(month))
                    mGoogleEvent.setTextColor(context.resources.getColor(android.R.color.black))
                }

                else -> {
                    itemView.isEnabled = false
                    mCalendarDay.setTextColor(context.resources.getColor(android.R.color.white))
                    itemView.setBackgroundColor(context.resources.getColor(R.color.lighter_gray))
                    mGoogleEvent.setTextColor(context.resources.getColor(android.R.color.white))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_calendar, parent, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val animateSelection = PreferencesHelper.isOptionActive(PreferencesHelper.KEY_ANIMATION_SELECTION, false)
            if (animateSelection)
                view.stateListAnimator = AnimatorInflater
                        .loadStateListAnimator(parent?.context, R.animator.cell_animator)
        }

        return DayHolder(view)
    }

    override fun onBindViewHolder(holder: DayHolder, position: Int){
        holder.setupView(calendarDays[position])
    }

    override fun getItemCount() = calendarDays.size

    fun refresh(newDays: List<CalendarDay>) {
        calendarDays.clear()
        calendarDays.addAll(newDays)

        notifyDataSetChanged()
    }
}