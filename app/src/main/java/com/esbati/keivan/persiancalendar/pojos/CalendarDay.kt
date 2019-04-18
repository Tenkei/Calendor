package com.esbati.keivan.persiancalendar.pojos

import android.content.Context
import android.text.TextUtils
import com.esbati.keivan.persiancalendar.R
import java.util.*

/**
 * Created by Keivan Esbati on 11/20/2016.
 */

class CalendarDay(
    val mYear: Int,
    val mMonth: Int,
    val mDay: Int,
    val isToday: Boolean = false,
    val isHoliday: Boolean = false,
    val isCurrentMonth: Boolean = false,
    val formattedDate: String = "",
    val formattedDateSecondary: String = "",
    val mRemarks: ArrayList<CalendarRemark> = ArrayList(),
    val mEvents: ArrayList<UserEvent> = ArrayList()){

    fun getEventsSummary(context: Context): String {
        var title = ""
        //Find an event with title
        for (event in mEvents)
            if (!TextUtils.isEmpty(event.title)) {
                title = event.title!!
                break
            }

        //Adjust Content Text
        return when {
            //If an Event with Title is found, add events count if needed
            !TextUtils.isEmpty(title) && mEvents.size > 1 ->
                context.getString(
                        R.string.notification_collapsed_text_with_title
                        , title
                        , mEvents.size - 1
                )

            //If No Event with title is found just show event count if available
            TextUtils.isEmpty(title) && mEvents.size > 0 ->
                context.getString(
                        R.string.notification_collapsed_text_without_title
                        , mEvents.size
                )

            //Show title without any change
            else -> title
        }.trim()
    }
}