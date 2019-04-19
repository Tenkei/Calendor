package com.esbati.keivan.persiancalendar.features.settings

import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.NumberPicker
import com.esbati.keivan.persiancalendar.BuildConfig
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.features.notification.NotificationHelper
import com.esbati.keivan.persiancalendar.features.notification.NotificationUpdateService
import com.esbati.keivan.persiancalendar.features.settings.cells.TextCheckCell
import com.esbati.keivan.persiancalendar.features.settings.cells.TextSettingsCell
import com.esbati.keivan.persiancalendar.repository.PreferencesHelper

/**
 * Created by Keivan Esbati on 4/16/2017.
 */

class SettingsFragment : BottomSheetDialogFragment() {

    private val mPriorityTitles by lazy {
        arrayOf(
                getString(R.string.least),
                getString(R.string.down),
                getString(R.string.priority_default),
                getString(R.string.up),
                getString(R.string.the_most)
        )
    }

    private lateinit var mNotificationAction: TextCheckCell
    private lateinit var mNotificationPriority: TextSettingsCell


    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?) = container(context!!) {
        //Selection Animation
        header(R.string.setting_animation)
        textCheck {
            title = getString(R.string.setting_animation_selection)
            isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            isChecked = PreferencesHelper.isAnimationSelectionActive
            needDivider = false
            onClick {
                isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_ANIMATION_SELECTION, false)
            }
        }
        shadowDivider()

        //Sticky Notification
        header(R.string.setting_sticky_notification)
        textCheck {
            title = getString(R.string.setting_sticky_notification_display)
            isChecked = PreferencesHelper.shouldShowNotification
            needDivider = true
            onClick {
                //Toggle Setting and Set Notification Settings
                isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_NOTIFICATION_SHOW)
                mNotificationAction.isEnabled = isChecked
                mNotificationPriority.isEnabled = isChecked

                //Update Notification
                NotificationUpdateService.enqueueUpdate(context!!)
            }
        }

        textCheck {
            mNotificationAction = this

            title = getString(R.string.setting_sticky_notification_actions)
            isEnabled = PreferencesHelper.shouldShowNotification
            isChecked = PreferencesHelper.isNotificationActionsActive
            needDivider = true
            onClick {
                //Toggle Setting & Update Notification
                isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_NOTIFICATION_ACTIONS)
                NotificationUpdateService.enqueueUpdate(context!!)
            }
        }

        textSetting {
            mNotificationPriority = this

            needDivider = false
            isEnabled = PreferencesHelper.shouldShowNotification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelImportanceIndex = Math.max(0, NotificationHelper.getChannelImportance(context!!) - 1)
                title = getString(R.string.setting_sticky_notification_priority)
                value = mPriorityTitles[channelImportanceIndex]
                onClick { NotificationHelper.openChannelSetting(context) }
            } else {
                title = getString(R.string.setting_sticky_notification_priority)
                value = mPriorityTitles[PreferencesHelper.notificationPriority]
                onClick { showNotificationPriorityPicker() }
            }
        }

        shadowDivider()

        //Application Version
        textInfo {
            text = "${getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}"
            textColor = Color.WHITE
            setBackgroundResource(R.color.colorPrimary)
        }
    }

    private fun showNotificationPriorityPicker() {
        val numberPicker = NumberPicker(context!!).apply {
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            minValue = 0
            maxValue = mPriorityTitles.size - 1
            wrapSelectorWheel = false
            displayedValues = mPriorityTitles
            value = PreferencesHelper.notificationPriority
        }

        AlertDialog.Builder(context!!)
                .setTitle("")
                .setView(numberPicker)
                .setPositiveButton(R.string.dialog_button_confirm) { _, _ ->
                    //Toggle Setting
                    PreferencesHelper.notificationPriority = numberPicker.value
                    mNotificationPriority.value = mPriorityTitles[numberPicker.value]

                    //Update Notification
                    NotificationUpdateService.enqueueUpdate(context!!)
                }.create()
                .show()
    }
}
