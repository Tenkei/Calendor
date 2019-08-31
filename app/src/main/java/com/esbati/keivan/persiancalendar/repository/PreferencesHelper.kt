package com.esbati.keivan.persiancalendar.repository

import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import com.esbati.keivan.persiancalendar.components.ApplicationController

/**
 * Created by Shubham on 4/15/2019.
 */
object PreferencesHelper {

    //region Setting Toggles Keys
    const val KEY_ANIMATION_SELECTION = "showSelectionAnimation"
    const val KEY_NOTIFICATION_SHOW = "showNotification"
    const val KEY_NOTIFICATION_ACTIONS = "showNotificationAction"
    const val KEY_NOTIFICATION_PRIORITY = "notificationPriority"
    const val KEY_PLAY_NOTE = "playNote"
    //endregion

    private val preferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(ApplicationController.getContext())

    private fun save(operation: (SharedPreferences.Editor) -> Unit) {
        preferences.edit()
                .also(operation::invoke)
                .apply()
    }

    //region Specific operations
    var isAnimationSelectionActive: Boolean
        get() = preferences.getBoolean(KEY_ANIMATION_SELECTION, false)
        set(value) = save { it.putBoolean(KEY_ANIMATION_SELECTION, value) }

    var shouldShowNotification: Boolean
        get() = preferences.getBoolean(KEY_NOTIFICATION_SHOW, true)
        set(value) = save { it.putBoolean(KEY_NOTIFICATION_SHOW, value) }

    var isNotificationActionsActive: Boolean
        get() = preferences.getBoolean(KEY_NOTIFICATION_ACTIONS, true)
        set(value) = save { it.putBoolean(KEY_NOTIFICATION_ACTIONS, value) }

    var notificationPriority: Int
        get() = preferences.getInt(KEY_NOTIFICATION_PRIORITY, NotificationCompat.PRIORITY_MAX)
        set(value) = save { it.putInt(KEY_NOTIFICATION_PRIORITY, value) }

    var playNote : Boolean
        get() = preferences.getBoolean(KEY_PLAY_NOTE, true)
        set(value) = save { it.putBoolean(KEY_PLAY_NOTE, value) }

    //endregion

    //region Public methods
    fun isOptionActive(key: String, defaultValue: Boolean = false) = preferences.getBoolean(key, defaultValue)

    fun setOption(key: String, isActive: Boolean) = save { it.putBoolean(key, isActive) }

    fun toggleOption(key: String, defaultValue: Boolean = true): Boolean {
        return preferences.getBoolean(key, defaultValue).not()
                .also { setOption(key, it) }
    }

    fun loadString(key: String, defaultValue: String? = null) =
            preferences.getString(key, null) ?: defaultValue

    fun saveString(key: String, value: String) = save { it.putString(key, value) }

    fun loadInt(key: String, defaultValue: Int = 0) = preferences.getInt(key, defaultValue)

    fun saveInt(key: String, value: Int) = save { it.putInt(key, value) }

    //endregion
}