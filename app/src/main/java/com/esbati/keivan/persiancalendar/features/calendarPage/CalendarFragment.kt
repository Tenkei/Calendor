package com.esbati.keivan.persiancalendar.features.calendarPage

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.VERTICAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.components.SoundManager
import com.esbati.keivan.persiancalendar.components.locate
import com.esbati.keivan.persiancalendar.features.home.HomeFragment
import com.esbati.keivan.persiancalendar.pojos.CalendarDay
import com.esbati.keivan.persiancalendar.repository.Repository
import com.esbati.keivan.persiancalendar.utils.bindView

class CalendarFragment: Fragment() {

    private val mYear by lazy { arguments!!.get(EXTRA_YEAR) as Int }
    private val mMonth by lazy { arguments!!.get(EXTRA_MONTH) as Int }
    private val repository: Repository by locate()

    private val mRecyclerView: RecyclerView by bindView(R.id.list)
    private val mAdapter by lazy {
        CalendarAdapter(mYear, mMonth, repository.prepareDays(mYear, mMonth)).apply {
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
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRecyclerView.layoutManager = GridLayoutManager(activity, 7, VERTICAL, false)
        mRecyclerView.adapter = mAdapter
    }

    fun refreshCalendar() {
        mAdapter.refresh(repository.prepareDays(mYear, mMonth))
    }
}