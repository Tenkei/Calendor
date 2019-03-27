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
        header { setText(getString(R.string.setting_animation)) }

        textCheck {
            isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

            setTextAndCheck(getString(R.string.setting_animation_selection), PreferencesHelper.isOptionActive(PreferencesHelper.KEY_ANIMATION_SELECTION, false), false)
            setOnClickListener {
                //Toggle Setting and Set Notification Settings
                val isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_ANIMATION_SELECTION, false)
                setChecked(isChecked)
            }
        }

        shadowDivider()

        //Sticky Notification
        header { setText(getString(R.string.setting_sticky_notification)) }

        textCheck {
            setTextAndCheck(getString(R.string.setting_sticky_notification_display), PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_SHOW, true), true)
            setOnClickListener{
                //Toggle Setting and Set Notification Settings
                val isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_NOTIFICATION_SHOW, true)
                setChecked(isChecked)
                mNotificationAction.isEnabled = isChecked
                mNotificationPriority.isEnabled = isChecked

                //Update Notification
                NotificationUpdateService.enqueueUpdate(context!!)
            }
        }

        mNotificationAction = textCheck {
            isEnabled = PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_SHOW, true)

            setTextAndCheck(getString(R.string.setting_sticky_notification_actions), PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_ACTIONS, true), true)
            setOnClickListener {
                //Toggle Setting
                val isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_NOTIFICATION_ACTIONS, true)
                setChecked(isChecked)

                //Update Notification
                NotificationUpdateService.enqueueUpdate(context!!)
            }
        }

        mNotificationPriority = textSetting {
            isEnabled = PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_SHOW, true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelImportanceIndex = Math.max(0, NotificationHelper.getChannelImportance(context!!) - 1)
                setTextAndValue(getString(R.string.setting_sticky_notification_priority), mPriorityTitles[channelImportanceIndex], false)
                setOnClickListener { NotificationHelper.openChannelSetting(context) }
            } else {
                setTextAndValue(getString(R.string.setting_sticky_notification_priority), mPriorityTitles[PreferencesHelper.loadInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY, 2)], false)
                setOnClickListener { showNotificationPriorityPicker() }
            }
        }

        shadowDivider()

        //Application Version
        textInfo{
            setTextColor(Color.WHITE)
            setBackgroundResource(R.color.colorPrimary)
            setText("${getString(R.string.app_name)} ${BuildConfig.VERSION_NAME}")
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
