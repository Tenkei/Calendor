package com.esbati.keivan.persiancalendar.features.calendarPage

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.util.Checks
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.components.ServiceLocator
import com.esbati.keivan.persiancalendar.features.home.MainActivity
import com.esbati.keivan.persiancalendar.repository.Repository
import ir.smartlab.persindatepicker.util.PersianCalendar
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.reflect.AccessibleObject.setAccessible




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
    fun eventTitleIsShown(){
        //Setup
        //TODO improve with mock
        onView(withId(R.id.add_event))
                .perform(ViewActions.click())

        onView(withId(R.id.event_title))
                .perform(ViewActions.typeText(TEST_TITLE), ViewActions.pressImeActionButton())

        onView(withId(R.id.add_event))
                .perform(ViewActions.click())

        //Assert
        onView(allOf(withId(R.id.calendar_events), hasSibling(withText(TODAY)), isDisplayed()))
                .check(matches(withText(TEST_TITLE)))

        //Cleanup
        //TODO Improve with Mocks
        onView(withId(R.id.bottom_sheet_date_container))
                .perform(ViewActions.swipeUp())

        onView(AllOf.allOf(isDescendantOfA(withId(R.id.bottom_sheet_content_container)), withText(TEST_TITLE)))
                .perform(ViewActions.click())

        //TODO remove Thread sleep
        Thread.sleep(400)
        onView(AllOf.allOf(withEffectiveVisibility(Visibility.VISIBLE), withId(R.id.event_icon)))
                .perform(ViewActions.click())

        onView(withId(android.R.id.button1))
                .perform(ViewActions.click())
    }
}

fun withTextColor(@ColorRes color: Int): Matcher<View> {
    Checks.checkNotNull(color)
    return object : BoundedMatcher<View, TextView>(TextView::class.java) {
        public override fun matchesSafely(view: TextView): Boolean {
            return view.context.getColor(color) == view.currentTextColor
        }

        override fun describeTo(description: Description) {
            description.appendText("with text color: ")
        }
    }
}

fun withBackground(res: Int): Matcher<View> {
    Checks.checkNotNull(res)
    return object : BoundedMatcher<View, View>(View::class.java) {
        public override fun matchesSafely(view: View): Boolean {
            val background = view.background
            return when(background){
                is RippleDrawable -> background.sameAs(view.context.getDrawable(res))
                else -> false
            }
        }

        override fun describeTo(description: Description) {
            description.appendText("with background color: ")
        }
    }
}

fun RippleDrawable.sameAs(drawable: Drawable): Boolean {
    return drawable is RippleDrawable && this.getDefaultColor() == drawable.getDefaultColor()
}

fun RippleDrawable.getDefaultColor(): Int {
    var rippleColor: Int = -1
    try {
        val colorField = constantState::class.java.getDeclaredField("mColor")
        colorField.isAccessible = true
        val colorStateList = colorField.get(constantState) as ColorStateList
        rippleColor = colorStateList.defaultColor
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }

    return rippleColor
}