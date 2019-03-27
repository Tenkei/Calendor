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
import com.esbati.keivan.persiancalendar.features.settings.Container.Companion.container
import com.esbati.keivan.persiancalendar.features.settings.cells.TextCheckCell
import com.esbati.keivan.persiancalendar.features.settings.cells.TextSettingsCell
import com.esbati.keivan.persiancalendar.repository.PreferencesHelper

/**
 * Created by asus on 4/16/2017.
 */

class SettingFragment : BottomSheetDialogFragment() {

    private val mPriorityTitles = arrayOf("کمترین", "پایین", "پیش فرض", "بالا", "بیشترین")

    private lateinit var mNotificationAction: TextCheckCell
    private lateinit var mNotificationPriority: TextSettingsCell


    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?) = container(context!!) {
        //Selection Animation
        header(R.string.setting_animation)
        textCheck {
            text = getString(R.string.setting_animation_selection)
            isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            isChecked = PreferencesHelper.isOptionActive(PreferencesHelper.KEY_ANIMATION_SELECTION, false)
            needDivider = false
            onClick {
                isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_ANIMATION_SELECTION, false)
            }
        }
        shadowDivider()

        //Sticky Notification
        header(R.string.setting_sticky_notification)
        textCheck {
            text = getString(R.string.setting_sticky_notification_display)
            isChecked = PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_SHOW, true)
            needDivider = true
            onClick {
                //Toggle Setting and Set Notification Settings
                isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_NOTIFICATION_SHOW, true)
                mNotificationAction.isEnabled = isChecked
                mNotificationPriority.isEnabled = isChecked

                //Update Notification
                NotificationUpdateService.enqueueUpdate(context!!)
            }
        }

        textCheck {
            mNotificationAction = this

            text = getString(R.string.setting_sticky_notification_actions)
            isEnabled = PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_SHOW, true)
            isChecked = PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_ACTIONS, true)
            needDivider = true
            onClick {
                //Toggle Setting & Update Notification
                isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_NOTIFICATION_ACTIONS, true)
                NotificationUpdateService.enqueueUpdate(context!!)
            }
        }

        textSetting {
            mNotificationPriority = this

            needDivider = false
            isEnabled = PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_SHOW, true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelImportanceIndex = Math.max(0, NotificationHelper.getChannelImportance(context!!) - 1)
                text = getString(R.string.setting_sticky_notification_priority)
                value = mPriorityTitles[channelImportanceIndex]
                onClick { NotificationHelper.openChannelSetting(context) }
            } else {
                text = getString(R.string.setting_sticky_notification_priority)
                value = mPriorityTitles[PreferencesHelper.loadInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY, 2)]
                onClick { showNotificationPriorityPicker() }
            }
        }

        shadowDivider()

        //Application Version
        textInfo{
            text = "${getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}"
            textColor = Color.WHITE
        }
    }

    private fun showNotificationPriorityPicker() {
        val numberPicker = NumberPicker(context!!).apply {
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            minValue = 0
            maxValue = mPriorityTitles.size - 1
            wrapSelectorWheel = false
            displayedValues = mPriorityTitles
            value = PreferencesHelper.loadInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY, 2)
        }

        AlertDialog.Builder(context!!)
                .setTitle("")
                .setView(numberPicker)
                .setPositiveButton(R.string.dialog_button_confirm) { _, _ ->
                    //Toggle Setting
                    PreferencesHelper.saveInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY, numberPicker.value)
                    mNotificationPriority.setTextAndValue(getString(R.string.setting_sticky_notification_priority), mPriorityTitles[numberPicker.value], false)

                    //Update Notification
                    NotificationUpdateService.enqueueUpdate(context!!)
                }.create()
                .show()
    }
}
