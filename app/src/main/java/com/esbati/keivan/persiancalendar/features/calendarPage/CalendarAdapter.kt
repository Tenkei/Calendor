package com.esbati.keivan.persiancalendar.features.calendarPage

import android.animation.AnimatorInflater
import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.pojos.CalendarDay
import com.esbati.keivan.persiancalendar.repository.PreferencesHelper
import com.esbati.keivan.persiancalendar.utils.ColorHelper
import com.esbati.keivan.persiancalendar.utils.setTextColorResource

class CalendarAdapter(val year: Int, val month: Int, days: List<CalendarDay>) : androidx.recyclerview.widget.RecyclerView.Adapter<CalendarAdapter.DayHolder>() {

    var onCalendarClickListener: OnCalendarClickListener? = null
    val calendarDays = ArrayList<CalendarDay>(days)

    interface OnCalendarClickListener {
        fun onCalendarClick(day: CalendarDay)
    }

    inner class DayHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        private val mDayNo = itemView.findViewById(R.id.calendar_day_no) as TextView
        private val mEvents = itemView.findViewById(R.id.calendar_events) as TextView

        init {
            itemView.onFocusChangeListener = View.OnFocusChangeListener { view, isFocused ->
                if (isFocused) view.performClick()
            }

            itemView.setOnClickListener {
                val selectedDay = calendarDays[adapterPosition]
                if (selectedDay.isCurrentMonth)
                    onCalendarClickListener?.onCalendarClick(selectedDay)
            }
        }

        fun setupView(day: CalendarDay) {
            itemView.isEnabled = day.isCurrentMonth

            mDayNo.text = day.mDay.toString()
            if (day.mEvents.size > 0) {
                if (!TextUtils.isEmpty(day.mEvents[0].title))
                    mEvents.text = day.mEvents[0].title
                else
                    mEvents.setText(R.string.event_no_title)
            }

            //Set Background Color
            when {
                day.isHoliday && day.isToday -> {
                    mDayNo.setTextColorResource(android.R.color.holo_red_dark)
                    itemView.setBackgroundResource(R.drawable.bg_calendar_today)
                    mEvents.setTextColorResource(android.R.color.white)
                }

                day.isHoliday -> {
                    mDayNo.setTextColorResource(android.R.color.white)
                    itemView.setBackgroundResource(R.drawable.bg_calendar_holiday)
                    mEvents.setTextColorResource(android.R.color.white)
                }

                day.isToday -> {
                    mDayNo.setTextColorResource(android.R.color.white)
                    itemView.setBackgroundResource(R.drawable.bg_calendar_today)
                    mEvents.setTextColorResource(android.R.color.white)
                }

                day.isCurrentMonth -> {
                    mDayNo.setTextColorResource(android.R.color.black)
                    itemView.setBackgroundResource(ColorHelper.getSeasonDrawableResource(month))
                    mEvents.setTextColorResource(android.R.color.black)
                }

                else -> {
                    itemView.isEnabled = false
                    mDayNo.setTextColorResource(android.R.color.white)
                    itemView.setBackgroundResource(R.color.lighter_gray)
                    mEvents.setTextColorResource(android.R.color.white)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_calendar, parent, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (PreferencesHelper.isAnimationSelectionActive) {
                view.stateListAnimator = AnimatorInflater
                        .loadStateListAnimator(parent.context, R.animator.cell_animator)
            }
        }

        return DayHolder(view)
    }

    override fun onBindViewHolder(holder: DayHolder, position: Int) {
        holder.setupView(calendarDays[position])
    }

    override fun getItemCount() = calendarDays.size

    fun refresh(newDays: List<CalendarDay>) {
        calendarDays.clear()
        calendarDays.addAll(newDays)

        notifyDataSetChanged()
    }
}