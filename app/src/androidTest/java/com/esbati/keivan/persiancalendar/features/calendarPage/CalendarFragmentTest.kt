package com.esbati.keivan.persiancalendar.features.calendarPage

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.esbati.keivan.persiancalendar.*
import com.esbati.keivan.persiancalendar.components.ServiceLocator
import com.esbati.keivan.persiancalendar.features.home.MainActivity
import com.esbati.keivan.persiancalendar.pojos.CalendarRemark
import com.esbati.keivan.persiancalendar.pojos.UserEvent
import com.esbati.keivan.persiancalendar.repository.CalendarDataStore
import com.esbati.keivan.persiancalendar.repository.RemarkDataStore
import com.esbati.keivan.persiancalendar.repository.Repository
import com.esbati.keivan.persiancalendar.repository.RepositoryImp
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

    companion object TestObjects{
        const val TEST_TITLE = "TEST_TITLE"
        val TEST_TODAY_CALENDAR = PersianCalendar().setPersianDate(1398, 2, 14)!!
        private val TEST_DATE = PersianCalendar().setPersianDate(1398, 2, 15)!!
        val TEST_EVENT = UserEvent(1, "TEST_EVENT", "DESCRIPTION", TEST_DATE.timeInMillis)
        val TEST_HOLIDAY = CalendarRemark(
                "TEST_HOLIDAY",
                TEST_DATE.persianYear,
                TEST_DATE.persianMonth,
                TEST_DATE.persianDay,
                true
        )
    }

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.WRITE_CALENDAR)
    @get:Rule
    var activityTestRule = object : ActivityTestRule<MainActivity>(MainActivity::class.java, false, true) {

        override fun beforeActivityLaunched() {
            ServiceLocator.getInstance().apply {
                factory { TEST_TODAY_CALENDAR }
                single { RepositoryImp(get(), get(), get()) as Repository}
                single { FakeRemarkDataStore(TEST_HOLIDAY) as RemarkDataStore }
                single { FakeCalendarDataStore(TEST_EVENT ) as CalendarDataStore }
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
    fun todayIsMarked(){
        onView(allOf(withText(TEST_TODAY_CALENDAR.persianDay.toString()), isDisplayed()))
                .check(matches(withTextColor(android.R.color.white)))

        onView(allOf(withId(R.id.calendar_background), hasDescendant(withText(TEST_TODAY_CALENDAR.persianDay.toString())), isDisplayed()))
                .check(matches(withBackground(R.drawable.bg_calendar_today)))
    }

    @Test
    fun holidayIsMarked(){
        onView(allOf(withText(TEST_HOLIDAY.mDay.toString()), isDisplayed()))
                .check(matches(withTextColor(android.R.color.white)))

        onView(allOf(withId(R.id.calendar_background), hasDescendant(withText(TEST_HOLIDAY.mDay.toString())), isDisplayed()))
                .check(matches(withBackground(R.drawable.bg_calendar_holiday)))
    }

    @Test
    fun eventTitleIsShown(){
        onView(allOf(withId(R.id.calendar_events), hasSibling(withText(TEST_EVENT.day.toString())), isDisplayed()))
                .check(matches(withText(TEST_EVENT.title)))
    }

    @Test
    fun cellUpdatedWhenNewEventAdded(){
        onView(withId(R.id.add_event))
                .perform(click())

        //TODO Remove Thread Sleep
        Thread.sleep(400)
        onView(withId(R.id.event_title))
                .perform(typeText(TEST_TITLE))

        onView(withId(R.id.add_event))
                .perform(click())

        //Assert
        //TODO Remove Thread Sleep
        Thread.sleep(400)
        onView(allOf(withId(R.id.calendar_events), hasSibling(withText(TEST_TODAY_CALENDAR.persianDay.toString())), isDisplayed()))
                .check(matches(withText(TEST_TITLE)))
    }
}