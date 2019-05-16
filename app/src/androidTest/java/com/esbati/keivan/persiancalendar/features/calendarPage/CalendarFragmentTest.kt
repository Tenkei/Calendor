package com.esbati.keivan.persiancalendar.features.calendarPage

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.components.ServiceLocator
import com.esbati.keivan.persiancalendar.features.home.MainActivity
import com.esbati.keivan.persiancalendar.repository.Repository
import com.esbati.keivan.persiancalendar.withBackground
import com.esbati.keivan.persiancalendar.withTextColor
import ir.smartlab.persindatepicker.util.PersianCalendar
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class CalendarFragmentTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.WRITE_CALENDAR)
    @get:Rule
    var activityTestRule = object : ActivityTestRule<MainActivity>(MainActivity::class.java, false, true) {

        override fun beforeActivityLaunched() {
            ServiceLocator.getInstance().apply {
                factory { PersianCalendar().setPersianDate(1398, 2, TODAY.toInt()) }
                single { Repository(get(), get(), get()) }
            }
        }
    }

    val HOLIDAY = "13"
    val TODAY = "14"
    val TEST_TITLE = "TEST_TITLE"

    @Before
    fun setUp() {

    }

    @After
    fun tearDown() {

    }

    @Test
    fun todayIsMarked(){
        onView(allOf(withText(TODAY), isDisplayed()))
                .check(matches(withTextColor(android.R.color.white)))

        onView(allOf(withId(R.id.calendar_background), hasDescendant(withText(TODAY)), isDisplayed()))
                .check(matches(withBackground(R.drawable.bg_calendar_today)))
    }

    @Test
    fun holidayIsMarked(){
        onView(allOf(withText(HOLIDAY), isDisplayed()))
                .check(matches(withTextColor(android.R.color.white)))

        onView(allOf(withId(R.id.calendar_background), hasDescendant(withText(HOLIDAY)), isDisplayed()))
                .check(matches(withBackground(R.drawable.bg_calendar_holiday)))
    }

    @Test
    fun cellUpdatedWhenNewEventAdded(){
        //Setup
        //TODO improve with mock and create a test-case eventTitleIsShown()
        onView(withId(R.id.add_event))
                .perform(click())

        onView(withId(R.id.event_title))
                .perform(typeText(TEST_TITLE))

        onView(withId(R.id.add_event))
                .perform(click())

        //Assert
        onView(allOf(withId(R.id.calendar_events), hasSibling(withText(TODAY)), isDisplayed()))
                .check(matches(withText(TEST_TITLE)))

        //Cleanup
        //TODO Improve with Mocks
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(swipeUp())

        onView(allOf(isDescendantOfA(withId(R.id.bottom_sheet_content_container)), hasDescendant(withText(TEST_TITLE))))
                .perform(click())

        //TODO remove Thread sleep
        Thread.sleep(1000)
        onView(allOf(withId(R.id.event_icon), hasSibling(withText(TEST_TITLE))))
                .perform(click())

        onView(withId(android.R.id.button1))
                .perform(click())
    }
}