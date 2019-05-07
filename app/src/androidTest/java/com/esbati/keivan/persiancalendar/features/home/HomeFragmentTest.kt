package com.esbati.keivan.persiancalendar.features.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.components.ServiceLocator
import com.esbati.keivan.persiancalendar.components.views.CalendarBottomSheet
import com.esbati.keivan.persiancalendar.repository.Repository
import ir.smartlab.persindatepicker.util.PersianCalendar
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.IsNot.not
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@LargeTest
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class HomeFragmentTest {

    @get:Rule
    var activityTestRule = object : ActivityTestRule<MainActivity>(MainActivity::class.java, false, true) {

        override fun beforeActivityLaunched() {
            ServiceLocator.getInstance().apply {
                factory { PersianCalendar().setPersianDate(1398, 2, 15) }
                single { Repository(get(), get(), get()) }
            }
        }
    }

    val TITLE = "اردیبهشت 1398"
    val TITLE_SUB = "April - May"
    val TITLE_PERV_MONTH = "فروردین 1398"
    val TITLE_NEXT_MONTH = "خرداد 1398"
    val DATE_PERSIAN = "یک\u200Cشنبه  15  اردی\u200Cبهشت  1398"
    val DATE_GREGORIAN = "Sunday, May 5 2019"
    val TEST_TITLE_EVENT_NEW = "NEW_EVENT"
    val TEST_DESCRIPTION_EVENT_NEW = "DESCRIPTION"
    val TEST_TITLE_EVENT_TO_EDIT = "EDIT_ME"
    val TEST_TITLE_EVENT_EDITED = "EDITED_EVENT"

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
    fun toolbarCollapseWhenListSwipedUp(){
        onView(allOf(withId(R.id.list), isDisplayed()))
                .perform(swipeUp())

        onView(withId(R.id.toolbar_setting))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun bottomSheetExpandWhenSwipedUp(){
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        assertFalse(activityTestRule.activity.findViewById<CalendarBottomSheet>(R.id.bottom_sheet).isCollapsed())
    }

    @Test
    fun bottomSheetCollapseOnBackPressed(){
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        Thread.sleep(400)
        onView(withId(android.R.id.content))
                .perform(pressBack())

        assertTrue(activityTestRule.activity.findViewById<CalendarBottomSheet>(R.id.bottom_sheet).isCollapsed())
    }

    @Test
    fun a_newEventDialogIsDisplayedWhenFabClicked(){
        onView(withId(R.id.add_event))
                .perform(click())

        onView(withId(R.id.event_title))
                .check(matches(isDisplayed()))

        onView(withId(R.id.event_description))
                .check(matches(isDisplayed()))
    }

    @Test
    fun a_newEventIsShownOnNewEventSaved(){
        onView(withId(R.id.add_event))
                .perform(click())

        onView(withId(R.id.event_title))
                .perform(typeText(TEST_TITLE_EVENT_NEW), pressImeActionButton())

        onView(withId(R.id.event_description))
                .perform(typeText(TEST_DESCRIPTION_EVENT_NEW))

        onView(withId(R.id.add_event))
                .perform(click())

        onView(withId(R.id.bottom_sheet_content_container))
                .check(matches(hasDescendant(withText(TEST_TITLE_EVENT_NEW))))
    }

    @Test
    fun b_eventDetailDialogIsShownOnEventClick(){
        //TODO Improve with Mocks
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        onView(allOf(isDescendantOfA(withId(R.id.bottom_sheet_content_container)), withText(TEST_TITLE_EVENT_NEW)))
                .perform(click())

        //TODO Remove Thread Sleep
        Thread.sleep(500)
        onView(withText(TEST_DESCRIPTION_EVENT_NEW))
                .check(matches(isDisplayed()))
    }

    @Test
    fun c_editEventDialogIsShownOnEditEventClicked(){
        //TODO Improve with Mocks
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        onView(allOf(isDescendantOfA(withId(R.id.bottom_sheet_content_container)), withText(TEST_TITLE_EVENT_NEW)))
                .perform(click())

        onView(withId(R.id.add_event))
                .perform(click())

        onView(allOf(withId(R.id.event_title), withText(TEST_TITLE_EVENT_NEW)))
                .check(matches(isDisplayed()))

        onView(allOf(withId(R.id.event_description), withText(TEST_DESCRIPTION_EVENT_NEW)))
                .check(matches(isDisplayed()))
    }

    @Test
    fun c_editedEventIsShownOnEditedEventSaved(){
        //TODO Improve with Mocks
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        onView(allOf(isDescendantOfA(withId(R.id.bottom_sheet_content_container)), withText(TEST_TITLE_EVENT_NEW)))
                .perform(click())

        onView(withId(R.id.add_event))
                .perform(click())

        onView(withId(R.id.event_title))
                .perform(clearText(), typeText(TEST_TITLE_EVENT_EDITED))

        onView(withId(R.id.add_event))
                .perform(click())

        onView(withId(R.id.bottom_sheet_content_container))
                .check(matches(hasDescendant(withText(TEST_TITLE_EVENT_EDITED))))
    }

    @Test
    fun d_deleteDialogIsShownOnDeleteBtnClicked(){
        //TODO Improve with Mocks
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        onView(allOf(isDescendantOfA(withId(R.id.bottom_sheet_content_container)), withText(TEST_TITLE_EVENT_EDITED)))
                .perform(click())

        //TODO remove Thread sleep
        Thread.sleep(400)
        onView(allOf(withEffectiveVisibility(Visibility.VISIBLE), withId(R.id.event_icon)))
                .perform(click())

        onView(withText(R.string.dialog_delete_event_title))
                .check(matches(isDisplayed()))
    }

    @Test
    fun d_deletedEventRemovedOnEventDeleted(){
        //TODO Improve with Mocks
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        onView(allOf(isDescendantOfA(withId(R.id.bottom_sheet_content_container)), withText(TEST_TITLE_EVENT_EDITED)))
                .perform(click())

        //TODO remove Thread sleep
        Thread.sleep(400)
        onView(allOf(withEffectiveVisibility(Visibility.VISIBLE), withId(R.id.event_icon)))
                .perform(click())

        onView(withId(android.R.id.button1))
                .perform(click())

        onView(withId(R.id.bottom_sheet_content_container))
                .check(matches(not(hasDescendant(withText(TEST_TITLE_EVENT_EDITED)))))
    }
}