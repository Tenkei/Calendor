package com.esbati.keivan.persiancalendar.features.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.esbati.keivan.persiancalendar.BuildConfig;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.components.views.HeaderCell;
import com.esbati.keivan.persiancalendar.components.views.ShadowSectionCell;
import com.esbati.keivan.persiancalendar.components.views.TextCheckCell;
import com.esbati.keivan.persiancalendar.components.views.TextInfoCell;
import com.esbati.keivan.persiancalendar.components.views.TextSettingsCell;
import com.esbati.keivan.persiancalendar.features.notification.NotificationHelper;
import com.esbati.keivan.persiancalendar.features.notification.NotificationUpdateService;
import com.esbati.keivan.persiancalendar.repository.PreferencesHelper;

import org.jetbrains.annotations.NotNull;

/**
 * Created by asus on 4/16/2017.
 */

public class SettingFragment extends BottomSheetDialogFragment {

    //Global Views
    private View rootView;
    private LinearLayout mSettingContainer;

    //Animation
    private HeaderCell mAnimation;
    private TextCheckCell mSelectionAnimation;

    //Sticky Notification
    private HeaderCell mStickyNotification;
    private TextCheckCell mShowNotification;
    private TextCheckCell mNotificationAction;
    private TextSettingsCell mNotificationPriority;

    //Setting Values
    public final static String[] mPriorityTitles = {"کمترین", "پایین", "پیش فرض", "بالا", "بیشترین"};

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        setupView(rootView);
        return rootView;
    }

    private void setupView(View rootView) {
        mSettingContainer = rootView.findViewById(R.id.main_container);
        addSettings(mSettingContainer);
    }

    private void addSettings(LinearLayout settingContainer) {
        Context activity =  settingContainer.getContext();
        //Sticky Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAnimation = new HeaderCell(activity);
            mAnimation.setText(getString(R.string.setting_animation));
            settingContainer.addView(mAnimation);

            mSelectionAnimation = new TextCheckCell(activity);
            mSelectionAnimation.setTextAndCheck(getString(R.string.setting_animation_selection)
                    , PreferencesHelper.isOptionActive(PreferencesHelper.KEY_ANIMATION_SELECTION, false)
                    , false);
            settingContainer.addView(mSelectionAnimation);
            mSelectionAnimation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toggle Setting and Set Notification Settings
                    boolean isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_ANIMATION_SELECTION, false);
                    ((TextCheckCell) view).setChecked(isChecked);
                }
            });

            settingContainer.addView(new ShadowSectionCell(activity));
        }

        //Sticky Notification
        mStickyNotification = new HeaderCell(activity);
        mStickyNotification.setText(getString(R.string.setting_sticky_notification));
        settingContainer.addView(mStickyNotification);

        mShowNotification = new TextCheckCell(activity);
        mShowNotification.setTextAndCheck(getString(R.string.setting_sticky_notification_display)
                , PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_SHOW, true)
                , true);
        settingContainer.addView(mShowNotification);
        mShowNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toggle Setting and Set Notification Settings
                boolean isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_NOTIFICATION_SHOW, true);
                ((TextCheckCell) view).setChecked(isChecked);
                mNotificationAction.setEnabled(isChecked);
                mNotificationPriority.setEnabled(isChecked);

                //Update Notification
                NotificationUpdateService.Companion.enqueueUpdate(view.getContext());
            }
        });

        mNotificationAction = new TextCheckCell(activity);
        mNotificationAction.setTextAndCheck(getString(R.string.setting_sticky_notification_actions)
                , PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_ACTIONS, true)
                , true);
        settingContainer.addView(mNotificationAction);
        mNotificationAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toggle Setting
                boolean isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_NOTIFICATION_ACTIONS, true);
                ((TextCheckCell) view).setChecked(isChecked);

                //Update Notification
                NotificationUpdateService.Companion.enqueueUpdate(view.getContext());
            }
        });

        mNotificationPriority = new TextSettingsCell(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int channelImportanceIndex = NotificationHelper.getChannelImportance(activity) - 1;
            channelImportanceIndex = channelImportanceIndex <= 0 ? 0 : channelImportanceIndex;
            mNotificationPriority.setTextAndValue(getString(R.string.setting_sticky_notification_priority)
                    , mPriorityTitles[channelImportanceIndex]
                    , false);

            mNotificationPriority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NotificationHelper.openChannelSetting(view.getContext());
                }
            });
        } else {
            mNotificationPriority.setTextAndValue(getString(R.string.setting_sticky_notification_priority)
                    , mPriorityTitles[PreferencesHelper.loadInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY, 2)]
                    , false);

            mNotificationPriority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Show User Number Picker to Set Notification Priority
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("");
                    final NumberPicker numberPicker = new NumberPicker(getActivity());
                    numberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                    numberPicker.setMinValue(0);
                    numberPicker.setMaxValue(mPriorityTitles.length - 1);
                    numberPicker.setWrapSelectorWheel(false);
                    numberPicker.setDisplayedValues(mPriorityTitles);
                    numberPicker.setValue(PreferencesHelper.loadInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY, 2));
                    builder.setView(numberPicker);
                    builder.setPositiveButton(R.string.dialog_button_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toggle Setting
                            PreferencesHelper.saveInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY, numberPicker.getValue());
                            mNotificationPriority.setTextAndValue(getString(R.string.setting_sticky_notification_priority)
                                    , mPriorityTitles[numberPicker.getValue()]
                                    , false);

                            //Update Notification
                            Context context = getContext();
                            if (context != null) {
                                NotificationUpdateService.Companion.enqueueUpdate(context);
                            }
                        }
                    });
                    builder.create().show();
                }
            });

        }

        settingContainer.addView(mNotificationPriority);
        settingContainer.addView(new ShadowSectionCell(activity));

        //Setup Notification Options
        boolean isChecked = PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_SHOW, true);
        mNotificationAction.setEnabled(isChecked);
        mNotificationPriority.setEnabled(isChecked);

        //Application Version
        TextInfoCell mApplicationVersion = new TextInfoCell(activity);
        mApplicationVersion.setBackgroundResource(R.color.colorPrimary);
        mApplicationVersion.setTextColor(Color.WHITE);
        mApplicationVersion.setText(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
        settingContainer.addView(mApplicationVersion);
    }
}
