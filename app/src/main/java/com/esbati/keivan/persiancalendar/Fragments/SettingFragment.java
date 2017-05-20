package com.esbati.keivan.persiancalendar.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.esbati.keivan.persiancalendar.BuildConfig;
import com.esbati.keivan.persiancalendar.Components.Cell.HeaderCell;
import com.esbati.keivan.persiancalendar.Components.Cell.ShadowSectionCell;
import com.esbati.keivan.persiancalendar.Components.Cell.TextCheckCell;
import com.esbati.keivan.persiancalendar.Components.Cell.TextColorCell;
import com.esbati.keivan.persiancalendar.Components.Cell.TextInfoCell;
import com.esbati.keivan.persiancalendar.Components.Cell.TextSettingsCell;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Services.NotificationUpdateService;
import com.esbati.keivan.persiancalendar.Utils.PreferencesHelper;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        setupView(rootView);
        return rootView;
    }

    private void setupView(View rootView){
        mSettingContainer = (LinearLayout)rootView.findViewById(R.id.main_container);
        addSettings(mSettingContainer);
    }

    private void addSettings(LinearLayout settingContainer){

        //Sticky Notification
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mAnimation = new HeaderCell(getActivity());
            mAnimation.setText(getString(R.string.setting_animation));
            settingContainer.addView(mAnimation);

            mSelectionAnimation = new TextCheckCell(getActivity());
            mSelectionAnimation.setTextAndCheck(getString(R.string.setting_animation_selection)
                    , PreferencesHelper.isOptionActive(PreferencesHelper.KEY_ANIMATION_SELECTION, true)
                    , false);
            settingContainer.addView(mSelectionAnimation);
            mSelectionAnimation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toggle Setting and Set Notification Settings
                    boolean isChecked = PreferencesHelper.toggleOption(PreferencesHelper.KEY_ANIMATION_SELECTION, true);
                    ((TextCheckCell) view).setChecked(isChecked);
                }
            });

            settingContainer.addView(new ShadowSectionCell(getActivity()));
        }

        //Sticky Notification
        mStickyNotification = new HeaderCell(getActivity());
        mStickyNotification.setText(getString(R.string.setting_sticky_notification));
        settingContainer.addView(mStickyNotification);

        mShowNotification = new TextCheckCell(getActivity());
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
                Intent updateNotification = new Intent(getActivity(), NotificationUpdateService.class);
                getActivity().startService(updateNotification);
            }
        });

        mNotificationAction = new TextCheckCell(getActivity());
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
                Intent updateNotification = new Intent(getActivity(), NotificationUpdateService.class);
                getActivity().startService(updateNotification);
            }
        });

        mNotificationPriority = new TextSettingsCell(getActivity());
        mNotificationPriority.setTextAndValue(getString(R.string.setting_sticky_notification_priority)
                , "" + mPriorityTitles[PreferencesHelper.loadInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY, 2)]
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
                numberPicker.setValue(PreferencesHelper.loadInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY,  2));
                builder.setView(numberPicker);
                builder.setPositiveButton(R.string.dialog_button_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toggle Setting
                        PreferencesHelper.saveInt(PreferencesHelper.KEY_NOTIFICATION_PRIORITY, numberPicker.getValue());
                        mNotificationPriority.setTextAndValue(getString(R.string.setting_sticky_notification_priority)
                                , "" + mPriorityTitles[numberPicker.getValue()]
                                , false);

                        //Update Notification
                        Intent updateNotification = new Intent(getActivity(), NotificationUpdateService.class);
                        getActivity().startService(updateNotification);
                    }
                });
                builder.create().show();
            }
        });
        settingContainer.addView(mNotificationPriority);
        settingContainer.addView(new ShadowSectionCell(getActivity()));

        //Setup Notification Options
        boolean isChecked = PreferencesHelper.isOptionActive(PreferencesHelper.KEY_NOTIFICATION_SHOW, true);
        mNotificationAction.setEnabled(isChecked);
        mNotificationPriority.setEnabled(isChecked);

        //Application Version
        TextInfoCell mApplicationVersion = new TextInfoCell(getActivity());
        mApplicationVersion.setBackgroundResource(R.color.colorPrimary);
        mApplicationVersion.setTextColor(Color.WHITE);
        mApplicationVersion.setText(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
        settingContainer.addView(mApplicationVersion);
    }
}
