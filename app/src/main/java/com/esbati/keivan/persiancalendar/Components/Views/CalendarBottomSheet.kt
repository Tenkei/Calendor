package com.esbati.keivan.persiancalendar.Components.Views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.NestedScrollView
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.esbati.keivan.persiancalendar.POJOs.CalendarDay
import com.esbati.keivan.persiancalendar.POJOs.GoogleEvent
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities
import com.esbati.keivan.persiancalendar.Utils.Constants
import java.util.*

/**
 * Created by asus on 11/25/2016.
 */

class CalendarBottomSheet @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null
    ) : FrameLayout(context, attrs) {

    @JvmField var mBottomSheetMode = Mode.SHEET_MODE_DATE

    @JvmField var mBottomSheet: NestedScrollView
    @JvmField var mBottomSheetContainer: LinearLayout
    @JvmField var mPersianDate: TextView
    @JvmField var mGregorianDate: TextView
    @JvmField var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    @JvmField var mEventActionBtn: FloatingActionButton? = null

    //Event Sheet
    @JvmField var mEventTitle: TextView? = null
    @JvmField var mEventDesc: TextView? = null

    enum class Mode{
        SHEET_MODE_DATE,
        SHEET_MODE_EDIT_EVENT,
        SHEET_MODE_VIEW_EVENT
    }

    init {
        View.inflate(this.context, R.layout.component_bottom_sheet, this)

        mBottomSheet = findViewById(R.id.scroll_view) as NestedScrollView
        mBottomSheetContainer = findViewById(R.id.bottom_sheet_content_container) as LinearLayout
        mPersianDate = findViewById(R.id.date_shamsi) as TextView
        mGregorianDate = findViewById(R.id.date_miladi) as TextView
    }

    fun isCollapsed(): Boolean {
        return mBottomSheetContainer.height == 0 || mBottomSheetBehavior?.state == BottomSheetBehavior.STATE_COLLAPSED
    }

    @SuppressLint("SetTextI18n")
    fun setDateSheet(day: CalendarDay, onEventClick: (GoogleEvent) -> Unit) {
        //Set Date
        mPersianDate.text = day.mPersianDate.persianLongDate
        val gregorianCalendar = GregorianCalendar().apply {
            time = day.mPersianDate.time
        }
        mGregorianDate.text = Constants.weekdays_en[gregorianCalendar.get(Calendar.DAY_OF_WEEK) - 1] + ", " +
                Constants.months_en[gregorianCalendar.get(Calendar.MONTH)] + " " +
                gregorianCalendar.get(Calendar.DAY_OF_MONTH) + " " +
                gregorianCalendar.get(Calendar.YEAR)

        //Set Google Calendar Events
        mBottomSheetContainer.removeAllViews()
        if (day.mGoogleEvents != null)
            for (googleEvent in day.mGoogleEvents) {
                val eventView = LayoutInflater.from(context).inflate(R.layout.cell_bottom_sheet_day, mBottomSheetContainer, false)
                val eventTitle = eventView.findViewById(R.id.event_title) as TextView

                eventView.setBackgroundResource(R.drawable.bg_calendar_today)
                if (!TextUtils.isEmpty(googleEvent.mTITLE))
                    eventTitle.text = googleEvent.mTITLE
                else
                    eventTitle.setText(R.string.event_no_title)

                eventView.setOnClickListener { onEventClick(googleEvent)}
                mBottomSheetContainer.addView(eventView)
            }

        //Set Calendar Events
        if (day.mCalendarEvents != null && day.mCalendarEvents.size > 0) {
            //Add header
            val eventHeader = LayoutInflater.from(context).inflate(R.layout.cell_bottom_sheet_header, mBottomSheetContainer, false)
            (eventHeader.findViewById(R.id.header_title) as TextView).text = "رویداد های روز:"
            mBottomSheetContainer.addView(eventHeader)

            //Add events
            for (calendarEvent in day.mCalendarEvents) {
                val eventView = LayoutInflater.from(context).inflate(R.layout.cell_bottom_sheet_day, mBottomSheetContainer, false)
                val eventTitle = eventView.findViewById(R.id.event_title) as TextView

                eventTitle.text = calendarEvent.mTitle
                eventView.setBackgroundResource(if (calendarEvent.isHoliday) R.drawable.bg_calendar_holiday else R.drawable.bg_calendar_today)
                mBottomSheetContainer.addView(eventView)
            }
        }
    }

    fun setShowEventSheet(gEvent: GoogleEvent, onDeleteEvent: () -> Unit) {
        //Set Bottom Sheet
        mBottomSheetContainer.removeAllViews()

        //Set Event Title
        val eventTitle = LayoutInflater.from(context).inflate(R.layout.cell_bottom_sheet_day, mBottomSheetContainer, false)
        val eventTitleText = eventTitle.findViewById(R.id.event_title) as TextView
        val eventTitleIcon = eventTitle.findViewById(R.id.event_icon) as ImageView

        eventTitle.setBackgroundResource(R.drawable.bg_calendar_today)
        if (!TextUtils.isEmpty(gEvent.mTITLE))
            eventTitleText.text = gEvent.mTITLE
        else
            eventTitleText.setText(R.string.event_no_title)

        eventTitleIcon.visibility = View.VISIBLE
        eventTitleIcon.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
                    //.setView(mDialogView)
                    .setTitle(resources.getString(R.string.dialog_delete_event_title))
                    .setMessage(resources.getString(R.string.dialog_delete_event_body))
                    .setNegativeButton(resources.getString(R.string.dialog_button_return), null)
                    .setPositiveButton(resources.getString(R.string.dialog_button_confirm)) { _, _ ->
                        onDeleteEvent()
                    }.create()
            AndroidUtilities.showRTLDialog(dialog)
        }

        //Set Event Description
        val eventDescription = LayoutInflater.from(context).inflate(R.layout.cell_bottom_sheet_day, mBottomSheetContainer, false)
        val eventDescTV = eventDescription.findViewById(R.id.event_title) as TextView

        eventDescription.setBackgroundResource(R.drawable.bg_calendar_today)
        if (!TextUtils.isEmpty(gEvent.mDESCRIPTION))
            eventDescTV.text = gEvent.mDESCRIPTION
        else
            eventDescTV.setText(R.string.event_no_desc)

        mBottomSheetContainer.addView(eventTitle)
        mBottomSheetContainer.addView(eventDescription)
    }

    fun setEditEventSheet(gEvent: GoogleEvent) {
        mBottomSheetContainer.removeAllViews()

        val eventSheet = LayoutInflater.from(context).inflate(R.layout.cell_event_sheet, mBottomSheetContainer, false)
        mEventTitle = eventSheet.findViewById(R.id.event_title) as TextView
        mEventDesc = eventSheet.findViewById(R.id.event_description) as TextView

        //Set Event in Case of Updating Available Event
        if (!TextUtils.isEmpty(gEvent.mTITLE))
            mEventTitle!!.text = gEvent.mTITLE
        else
            mEventTitle!!.setHint(R.string.event_no_title)

        if (!TextUtils.isEmpty(gEvent.mDESCRIPTION))
            mEventDesc!!.text = gEvent.mDESCRIPTION
        else
            mEventDesc!!.setHint(R.string.event_no_desc)

        mBottomSheetContainer.addView(eventSheet)
    }
}
