package com.esbati.keivan.persiancalendar.features.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.esbati.keivan.persiancalendar.FakeCalendarDataStore
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.components.ServiceLocator
import com.esbati.keivan.persiancalendar.components.views.CalendarBottomSheet
import com.esbati.keivan.persiancalendar.pojos.CalendarDay
import com.esbati.keivan.persiancalendar.pojos.UserEvent
import com.esbati.keivan.persiancalendar.repository.CalendarDataStore
import com.esbati.keivan.persiancalendar.repository.Repository
import com.esbati.keivan.persiancalendar.repository.RepositoryImp
import ir.smartlab.persindatepicker.util.PersianCalendar
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    companion object TestObjects {
        val TEST_TODAY = CalendarDay(
                1398, 2, 15,
                true, false, true,
                "یک\u200Cشنبه  15  اردی\u200Cبهشت  1398",
                "Sunday, May 5 2019"
        )
        val TEST_TODAY_CALENDAR = PersianCalendar().setPersianDate(TEST_TODAY.mYear, TEST_TODAY.mMonth, TEST_TODAY.mDay)!!
        val TEST_EVENT = UserEvent(1, "TEST_EVENT", "DESCRIPTION", TEST_TODAY_CALENDAR.timeInMillis)
        const val TEST_EVENT_EDITED_TITLE = "EDITED_EVENT"
        const val TEST_EVENT_NEW_TITLE = "NEW_EVENT"
        const val TEST_EVENT_NEW_DESCRIPTION = "DESCRIPTION"

        const val TITLE = "اردیبهشت 1398"
        const val TITLE_SUB = "April - May"
        const val TITLE_PERV_MONTH = "فروردین 1398"
        const val TITLE_NEXT_MONTH = "خرداد 1398"
    }

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.WRITE_CALENDAR)
    @get:Rule
    var activityTestRule = object : ActivityTestRule<MainActivity>(MainActivity::class.java, false, true) {

        override fun beforeActivityLaunched() {
            ServiceLocator.getInstance().apply {
                factory { TEST_TODAY_CALENDAR }
                single { RepositoryImp(get(), get(), get()) as Repository }
                single { FakeCalendarDataStore(TEST_EVENT) as CalendarDataStore }
            }
        }
    }


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
                .check(matches(withText(TEST_TODAY.formattedDate)))

        onView(withId(R.id.date_miladi))
                .check(matches(withText(TEST_TODAY.formattedDateSecondary)))
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
        onView(withId(R.id.add_event))
                .perform(click())

        //TODO Remove Thread Sleep
        Thread.sleep(400)
        onView(withId(R.id.event_title))
                .perform(typeText(TEST_EVENT_NEW_TITLE), pressImeActionButton())

        onView(withId(R.id.event_description))
                .perform(typeText(TEST_EVENT_NEW_DESCRIPTION))

        onView(withId(R.id.add_event))
                .perform(click())

        //TODO Remove Thread Sleep
        Thread.sleep(400)
        onView(withId(R.id.bottom_sheet_content_container))
                .check(matches(hasDescendant(withText(TEST_EVENT_NEW_TITLE))))
    }

    @Test
    fun eventDetailDialogIsShownOnEventClick(){
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        onView(allOf(isDescendantOfA(withId(R.id.bottom_sheet_content_container)), withText(TEST_EVENT.title)))
                .perform(click())

        //TODO Remove Thread Sleep
        Thread.sleep(400)
        onView(withText(TEST_EVENT.description))
                .check(matches(isDisplayed()))
    }

    @Test
    fun editEventDialogIsShownOnEditEventClicked(){
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        onView(allOf(isDescendantOfA(withId(R.id.bottom_sheet_content_container)), withText(TEST_EVENT.title)))
                .perform(click())

        onView(withId(R.id.add_event))
                .perform(click())

        //TODO Remove Thread Sleep
        Thread.sleep(400)
        onView(allOf(withId(R.id.event_title), withText(TEST_EVENT.title)))
                .check(matches(isDisplayed()))

        onView(allOf(withId(R.id.event_description), withText(TEST_EVENT.description)))
                .check(matches(isDisplayed()))
    }

    @Test
    fun editedEventIsShownOnEditedEventSaved(){
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        onView(allOf(isDescendantOfA(withId(R.id.bottom_sheet_content_container)), withText(TEST_EVENT.title)))
                .perform(click())

        onView(withId(R.id.add_event))
                .perform(click())

        //TODO Remove Thread Sleep
        Thread.sleep(400)
        onView(withId(R.id.event_title))
                .perform(clearText(), typeText(TEST_EVENT_EDITED_TITLE))

        onView(withId(R.id.add_event))
                .perform(click())

        //TODO Remove Thread Sleep
        Thread.sleep(400)
        onView(withId(R.id.bottom_sheet_content_container))
                .check(matches(hasDescendant(withText(TEST_EVENT_EDITED_TITLE))))
    }

    @Test
    fun deleteDialogIsShownOnDeleteBtnClicked(){
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        onView(allOf(isDescendantOfA(withId(R.id.bottom_sheet_content_container)), withText(TEST_EVENT.title)))
                .perform(click())

        //TODO remove Thread sleep
        Thread.sleep(400)
        onView(allOf(withEffectiveVisibility(Visibility.VISIBLE), withId(R.id.event_icon)))
                .perform(click())

        onView(withText(R.string.dialog_delete_event_title))
                .check(matches(isDisplayed()))
    }

    @Test
    fun deletedEventRemovedOnEventDeleted(){
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        onView(allOf(isDescendantOfA(withId(R.id.bottom_sheet_content_container)), withText(TEST_EVENT.title)))
                .perform(click())

        //TODO remove Thread sleep
        Thread.sleep(400)
        onView(allOf(withEffectiveVisibility(Visibility.VISIBLE), withId(R.id.event_icon)))
                .perform(click())

        onView(withId(android.R.id.button1))
                .perform(click())

        onView(withId(R.id.bottom_sheet_content_container))
                .check(matches(not(hasDescendant(withText(TEST_EVENT_EDITED_TITLE)))))
    }
}