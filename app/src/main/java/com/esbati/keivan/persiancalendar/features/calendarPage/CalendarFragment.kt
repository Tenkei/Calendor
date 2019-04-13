package com.esbati.keivan.persiancalendar.features.calendarPage

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.VERTICAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esbati.keivan.persiancalendar.components.SoundManager
import com.esbati.keivan.persiancalendar.features.home.HomeFragment
import com.esbati.keivan.persiancalendar.pojos.CalendarDay
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.repository.Repository

class CalendarFragment: Fragment() {

    private val mYear by lazy { arguments!!.get(EXTRA_YEAR) as Int }
    private val mMonth by lazy { arguments!!.get(EXTRA_MONTH) as Int }

    private lateinit var mRecyclerView: RecyclerView
    private val mAdapter by lazy {
        CalendarAdapter(mYear, mMonth, Repository.INSTANCE.prepareDays(mYear, mMonth)).apply {
            onCalendarClickListener = object: CalendarAdapter.OnCalendarClickListener {
                override fun onCalendarClick(day: CalendarDay) {
                    SoundManager.playSound(day.mDay)
                    (parentFragment as HomeFragment).showDate(day, true)
                }
            }
        }
    }

    companion object {

        private const val EXTRA_YEAR = "extra_year"
        private const val EXTRA_MONTH = "extra_month"

        fun newInstance(year: Int, month: Int) = CalendarFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_YEAR, year)
                    putInt(EXTRA_MONTH, month)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)?.also {
            mRecyclerView = it.findViewById(R.id.list) as RecyclerView
            mRecyclerView.layoutManager = GridLayoutManager(activity, 7, VERTICAL, false)
            mRecyclerView.adapter = mAdapter
        }
    }

    fun refreshCalendar() {
        mAdapter.refresh(Repository.INSTANCE.prepareDays(mYear, mMonth))
    }
}