package com.esbati.keivan.persiancalendar.features.settings


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.features.home.MainActivity
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {

    @get:Rule
    var activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun setUp() {

    }

    @After
    fun tearDown() {
    }

    @Test
    fun notificationSettingsAreDisabledWhenNotificationIsDisabled(){
        onView(withId(R.id.appbar))
                .perform(swipeUp())

        onView(withId(R.id.toolbar_setting))
                .perform(click())

        onView(withText(R.string.setting_sticky_notification_display))
                .perform(click())

        onView(withText(R.string.setting_sticky_notification_actions))
                .check(matches(not(isEnabled())))

        onView(withText(R.string.setting_sticky_notification_priority))
                .check(matches(not(isEnabled())))
    }
}