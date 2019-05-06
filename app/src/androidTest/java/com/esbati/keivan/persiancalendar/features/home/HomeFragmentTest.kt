package com.esbati.keivan.persiancalendar.features.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.components.views.CalendarBottomSheet
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.hamcrest.core.AllOf
import org.hamcrest.core.AllOf.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    val TITLE = "اردیبهشت 1398"
    val TITLE_SUB = "April - May"
    val TITLE_PERV_MONTH = "فروردین 1398"
    val TITLE_NEXT_MONTH = "خرداد 1398"
    val DATE_PERSIAN = "یک\u200Cشنبه  15  اردی\u200Cبهشت  1398"
    val DATE_GREGORIAN = "Sunday, May 5 2019"

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun titleIsDisplayed(){
        onView(withId(R.id.toolbar_title))
                .check(matches(withText(TITLE)))

        onView(withId(R.id.toolbar_sub_title))
                .check(matches(withText(TITLE_SUB)))
    }

    @Test
    fun todayDateIsDisplayed(){
        onView(withId(R.id.date_shamsi))
                .check(matches(withText(DATE_PERSIAN)))

        onView(withId(R.id.date_miladi))
                .check(matches(withText(DATE_GREGORIAN)))
    }

    @Test
    fun settingIsShownOnSettingBtnClicked(){
        onView(withId(R.id.appbar))
                .perform(swipeUp())

        onView(withId(R.id.toolbar_setting))
                .perform(click())

        onView(withText(R.string.setting_animation))
                .check(matches(isDisplayed()))
    }

    @Test
    fun previousMonthIsShownOnRightArrowClicked(){
        onView(withId(R.id.toolbar_right_btn))
                .perform(click())

        onView(withId(R.id.toolbar_title))
                .check(matches(withText(TITLE_PERV_MONTH)))
    }

    @Test
    fun nextMonthIsShownOnLeftArrowClicked(){
        onView(withId(R.id.toolbar_left_btn))
                .perform(click())

        onView(withId(R.id.toolbar_title))
                .check(matches(withText(TITLE_NEXT_MONTH)))
    }

    @Test
    fun previousMonthIsShownWhenSwipedLeft(){
        onView(withId(R.id.pager))
                .perform(swipeLeft())

        onView(withId(R.id.toolbar_title))
                .check(matches(withText(TITLE_PERV_MONTH)))
    }

    @Test
    fun nextMonthIsShownWhenSwipedRight(){
        onView(withId(R.id.pager))
                .perform(swipeRight())

        onView(withId(R.id.toolbar_title))
                .check(matches(withText(TITLE_NEXT_MONTH)))
    }

    @Test
    fun toolbarCollapseWhenSwipedUp(){
        onView(withId(R.id.appbar))
                .perform(swipeUp())

        onView(withId(R.id.toolbar_setting))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun toolbarCollapseWhenPagerSwipedUp(){
        onView(allOf(withId(R.id.list), isDisplayed()))
                .perform(swipeUp())

        onView(withId(R.id.toolbar_setting))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun bottomSheetExpandWhenSwipedUp(){
        onView(withId(R.id.bottom_sheet_Date_container))
                .perform(swipeUp())

        assertFalse(activityTestRule.activity.findViewById<CalendarBottomSheet>(R.id.bottom_sheet).isCollapsed())
    }

    @Test
    fun bottomSheetCollapseOnBackPressed(){
        onView(withId(R.id.bottom_sheet_Date_container))
                .perform(swipeUp())

        Thread.sleep(400)
        onView(withId(android.R.id.content))
                .perform(pressBack())

        assertTrue(activityTestRule.activity.findViewById<CalendarBottomSheet>(R.id.bottom_sheet).isCollapsed())
    }

    @Test
    fun newEventDialogIsDisplayedWhenFabClicked(){
        onView(withId(R.id.add_event))
                .perform(click())

        onView(withId(R.id.event_title))
                .check(matches(isDisplayed()))

        onView(withId(R.id.event_description))
                .check(matches(isDisplayed()))
    }

    @Test
    fun newEventIsShownOnNewEventSaved(){

    }

    @Test
    fun eventDetailDialogIsShownOnEventClick(){

    }

    @Test
    fun editEventDialogIsShownOnEditEventClicked(){

    }

    @Test
    fun editedEventIsShownOnEditedEventSaved(){

    }

    @Test
    fun deleteDialogIsShownOnDeleteBtnClicked(){

    }

    @Test
    fun deletedEventRemovedOnEventDeleted(){

    }

    //Test Back presses
}