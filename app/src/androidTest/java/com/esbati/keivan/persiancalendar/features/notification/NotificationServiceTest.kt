package com.esbati.keivan.persiancalendar.features.notification

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ServiceTestRule
import com.esbati.keivan.persiancalendar.FakeRepository
import com.esbati.keivan.persiancalendar.components.ServiceLocator
import com.esbati.keivan.persiancalendar.repository.Repository
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationServiceTest {
    
    @get:Rule
    val serviceTestRule = object: ServiceTestRule(){
        override fun beforeService() {
            ServiceLocator.getInstance().single { FakeRepository() as Repository }
        }
    }
    
    var appContext: Context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun serviceIsStartedWhenAskedTo(){
        NotificationService.startService(appContext)

        assertTrue(NotificationService.isServiceRunning(appContext))
    }

    @Test
    fun serviceIsStoppedWhenAskedTo(){
        NotificationService.startService(appContext)
        Thread.sleep(5000)

        NotificationService.stopService(appContext)

        assertFalse(NotificationService.isServiceRunning(appContext))
    }

    @Test
    fun checkIfServiceIsPromotedToForeground(){
        NotificationService.startService(appContext)
        Thread.sleep(5000)

        assertTrue(NotificationService.getServiceInfo(appContext)!!.foreground)
    }
}