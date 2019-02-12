package com.esbati.keivan.persiancalendar.Features.Home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.esbati.keivan.persiancalendar.Components.Views.CalendarPager;
import com.esbati.keivan.persiancalendar.Features.CalendarPage.CalendarFragment;
import com.esbati.keivan.persiancalendar.Features.Settings.SettingFragment;
import com.esbati.keivan.persiancalendar.POJOs.CalendarDay;
import com.esbati.keivan.persiancalendar.POJOs.CalendarEvent;
import com.esbati.keivan.persiancalendar.POJOs.GoogleEvent;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Features.Notification.NotificationUpdateService;
import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities;
import com.esbati.keivan.persiancalendar.Utils.Constants;
import com.esbati.keivan.persiancalendar.Repository.GoogleCalendarHelper;
import com.esbati.keivan.persiancalendar.Components.Views.CalendarBottomSheet;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/18/2016.
 */

public class HomeFragment extends Fragment {

    private int mDisplayedMonth;
    private int mDisplayedYear;
    private int mBottomSheetMode;
    private int mPreviousBottomSheetState;
    private boolean mShouldUpdateBottomSheet;
    private boolean mShouldExpandBottomSheet;
    private GoogleEvent tempEvent;
    private GoogleEvent mSelectedEvent;
    private CalendarDay mSelectedDay;

    //Toolbar
    private int mToolbarMargin;
    private CoordinatorLayout mCoordinatorLayout;
    private AppBarLayout mAppbar;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private TextView mToolbarSubTitle;
    private ImageSwitcher mToolbarBackground;
    private ImageView mSetting;
    private ImageView mRightBtn;
    private ImageView mLeftBtn;

    //Body
    private CalendarPager mPager;
    private FloatingActionButton mEventActionBtn;
    private FragmentPagerAdapter mPagerAdapter;

    //Bottom Sheet
    private NestedScrollView mBottomSheet;
    private LinearLayout mBottomSheetContainer;
    private BottomSheetBehavior mBottomSheetBehavior;
    private TextView mPersianDate;
    private TextView mGregorianDate;

    //Event Sheet
    private TextView mEventTitle;
    private TextView mEventDesc;

    //Counter
    public static int count;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //Setup Initial Day
        PersianCalendar today = new PersianCalendar();
        mDisplayedYear = today.getPersianYear();
        mDisplayedMonth = today.getPersianMonth();

        //Setup Views
        setupToolbar(rootView);
        setupBottomSheet(rootView);
        setupPager(rootView);

        //Set Viewpager to Show Current Month
        mPager.setRtL(true);
        mPager.setCurrentItem(mDisplayedYear, mDisplayedMonth);
        showDate(new CalendarDay(persianCalendar), false, true);
        runStartAnimation();

        return rootView;
    }

    public void runStartAnimation(){
        mAppbar.post(new Runnable() {
            @Override
            public void run() {
                mAppbar.setExpanded(true, true);
            }
        });

        mToolbarTitle.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_top));
        mToolbarSubTitle.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_top));
        mPersianDate.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right));
        mGregorianDate.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right));
        mEventActionBtn.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left));
        mPager.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_bottom));
     }

    public void setupToolbar(View rootView){
        mCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinator_layout);
        mAppbar = (AppBarLayout) rootView.findViewById(R.id.appbar);
        mCollapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        mToolbarTitle = (TextView)rootView.findViewById(R.id.toolbar_title);
        mToolbarSubTitle = (TextView)rootView.findViewById(R.id.toolbar_sub_title);
        mToolbarBackground = (ImageSwitcher) rootView.findViewById(R.id.toolbar_background);
        mSetting = (ImageView)rootView.findViewById(R.id.toolbar_setting);
        mRightBtn = (ImageView)rootView.findViewById(R.id.toolbar_right_btn);
        mLeftBtn = (ImageView)rootView.findViewById(R.id.toolbar_left_btn);

        mCollapsingToolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCollapsingToolbar.setScrimVisibleHeightTrigger(mCollapsingToolbar.getHeight() - AndroidUtilities.dp(48));
                mCollapsingToolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment bts = new SettingFragment();
                bts.show(getActivity().getSupportFragmentManager(), SettingFragment.class.getSimpleName());
            }
        });

        mToolbarBackground.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getActivity());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
        mToolbarBackground.setInAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
        mToolbarBackground.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));

        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.loadRightItem();
            }
        });

        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.loadLeftItem();
            }
        });

        mAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float abHeight = mAppbar.getTotalScrollRange();
                final float heightRatio = (abHeight - Math.abs(verticalOffset)) / abHeight; //Range from 1 to 0
                final int newToolbarMarginPixel = (int)(heightRatio * AndroidUtilities.dp(48)); //Range from 48 to 0
                final int newButtonMarginPixel = (int)((1 - heightRatio) * AndroidUtilities.dp(48)); //Range from 0 to 48

                //Set Toolbar and Icon Margin, Since Padding is int Value
                if(mToolbarMargin != newToolbarMarginPixel)
                    mAppbar.post(new Runnable() {
                        @Override
                        public void run() {
                            mToolbarMargin = newToolbarMarginPixel;
                            mToolbar.setPadding(0, 0, 0, newToolbarMarginPixel);
                            //CollapsingToolbarLayout.LayoutParams clp = (CollapsingToolbarLayout.LayoutParams)mToolbar.getLayoutParams();
                            //clp.setMargins(0, 0, 0, newToolbarMarginPixel);
                            //mToolbar.setLayoutParams(clp);

                            FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) mRightBtn.getLayoutParams();
                            flp.setMargins(0, 0, newButtonMarginPixel, 0);
                            mRightBtn.setLayoutParams(flp);

                            flp = (FrameLayout.LayoutParams) mLeftBtn.getLayoutParams();
                            flp.setMargins(newButtonMarginPixel,0 , 0, 0);
                            mLeftBtn.setLayoutParams(flp);

                            flp = (FrameLayout.LayoutParams)mSetting.getLayoutParams();
                            flp.setMargins(0, 0, newButtonMarginPixel - AndroidUtilities.dp(48), 0);
                            mSetting.setLayoutParams(flp);
                        }
                    });

                //Set Extra Views
                mSetting.setAlpha(1 - heightRatio);
                mSetting.setRotation(180 * heightRatio);

                Log.e("Padding", "" + newToolbarMarginPixel);
            }
        });
    }

    public void setupPager(final View rootView){
        mEventActionBtn = (FloatingActionButton) rootView.findViewById(R.id.add_event);
        setFab();


        mPager = (CalendarPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new CalendarPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int year, int month) {
                mDisplayedYear = year;
                mDisplayedMonth = month;

                //Set Toolbar Background
                Resources res = getResources();
                TypedArray icons = res.obtainTypedArray(R.array.months_background);
                Drawable drawable = icons.getDrawable(mDisplayedMonth - 1);
                mToolbarBackground.setImageDrawable(drawable);

                //Set Toolbar Title
                mToolbarTitle.setText(Constants.months[mDisplayedMonth - 1] + " " + mDisplayedYear);
                mToolbarSubTitle.setText(
                        Constants.months_en[(mDisplayedMonth + 1) % 12]
                                + " - "
                                + Constants.months_en[(mDisplayedMonth + 2) % 12]
                );
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    public void setupBottomSheet(View rootView){
        mBottomSheet = (NestedScrollView)rootView.findViewById(R.id.bottom_sheet);
        mBottomSheetContainer = (LinearLayout) rootView.findViewById(R.id.bottom_sheet_content_container);
        mPersianDate = (TextView)rootView.findViewById(R.id.date_shamsi);
        mGregorianDate = (TextView)rootView.findViewById(R.id.date_miladi);

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                //Change BottomSheet Content if Needed
                if(isBottomSheetCollapsed() && mShouldUpdateBottomSheet){
                    Log.e("BTS", "Item " + count + " Updated: Callback");
                    mShouldUpdateBottomSheet = false;

                    setFab();
                    setBottomSheet();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                if(mBottomSheetMode == CalendarBottomSheet.SHEET_MODE_ADD_EVENT){
                    //mAppbar.scroll(mAppbar.getHeight() * v * -1);
                }
            }
        });
    }

    public boolean isBottomSheetCollapsed(){
        return mBottomSheetContainer.getHeight() == 0 || mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED;
    }

    public void proceedToSetupBottomSheet(){
        count++;
        Log.e("BTS", "Item " + count + " Proceed to Updated");

        if(isBottomSheetCollapsed()){
            Log.e("BTS", "Item " + count + " Updated: Normally");

            //If Sheet is Flat or Collapsed Set it Up
            if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED)
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            setFab();
            setBottomSheet();
        } else {
            //FIXME Sometimes if Bottom Sheet is in Settling Mode It won't Change State to Collapsed
            if(mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_SETTLING)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Force Update if BottomSheet got Stuck
                        if(mShouldUpdateBottomSheet){
                            Log.e("BTS", "Item " + count + " Updated: Forced");
                            setFab();
                            setBottomSheet();
                        }
                    }
                }, 300);

            //If Sheet is not Collapsed, Collapse it then Set it Up
            mShouldUpdateBottomSheet = true;
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void setBottomSheet(){
        switch (mBottomSheetMode) {
            case CalendarBottomSheet.SHEET_MODE_VIEW_EVENT:
                //Set Bottom Sheet
                setShowEventSheet(mSelectedEvent);
                break;

            case CalendarBottomSheet.SHEET_MODE_ADD_EVENT:
                //Set Bottom Sheet
                setAddEventSheet(mSelectedEvent, true);
                break;

            case CalendarBottomSheet.SHEET_MODE_DATE:
            default:
                //Set Bottom Sheet
                setDateSheet(mSelectedDay);
                break;
        }

        //Expand View If Needed
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mBottomSheetMode == CalendarBottomSheet.SHEET_MODE_DATE && mPreviousBottomSheetState > 0){
                    //If BottomSheet is Stuck in Settling Set it to Collapse
                    if(mPreviousBottomSheetState == BottomSheetBehavior.STATE_SETTLING)
                        mPreviousBottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;

                    mBottomSheetBehavior.setState(mPreviousBottomSheetState);
                    mPreviousBottomSheetState = 0;
                } else if(mShouldExpandBottomSheet) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        }, 200);
    }

    public void setFab(){
        switch (mBottomSheetMode){
            case CalendarBottomSheet.SHEET_MODE_ADD_EVENT:
                mEventActionBtn.setImageResource(R.drawable.ic_check_white_24dp);
                mEventActionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Return if Add Event Sheet is Not Available
                        if(mEventTitle == null || mEventDesc == null)
                            return;

                        //Close Keyboard
                        if(getView()!=null){
                            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                        }

                        //Save Event
                        tempEvent.mTITLE = mEventTitle.getText().toString();
                        tempEvent.mDESCRIPTION = mEventDesc.getText().toString();
                        if(tempEvent.mDTSTART == 0){
                            tempEvent.mDTSTART = mSelectedDay.mPersianDate.getTimeInMillis();
                            tempEvent.mStartDate = new PersianCalendar(mSelectedDay.mPersianDate.getTimeInMillis());
                        }

                        int msgId;
                        if(tempEvent.mID != 0){
                            msgId = GoogleCalendarHelper.updateEvent(tempEvent);
                        } else {
                            msgId = GoogleCalendarHelper.saveSimpleEvent(tempEvent);
                        }


                        //int msgId = GoogleCalendarHelper.saveSimpleEvent(mEventTitle.getText().toString(), mEventDesc.getText().toString()
                        //        , mSelectedDay.mPersianDate.getTimeInMillis());
                        Toast.makeText(getActivity(), msgId, Toast.LENGTH_SHORT).show();

                        //Refresh UI and show Date if Event Successfully added
                        if(msgId == R.string.event_successfully_added || msgId == R.string.event_successfully_updated){
                            refreshFragment(tempEvent.mStartDate.getPersianYear(), tempEvent.mStartDate.getPersianMonth());
                            showDate(mSelectedDay, true, true);

                            //Update Notification
                            Intent updateNotification = new Intent(getActivity(), NotificationUpdateService.class);
                            getActivity().startService(updateNotification);

                            if(msgId == R.string.event_successfully_added){
                                Answers.getInstance()
                                        .logCustom(new CustomEvent("Add Event")
                                                .putCustomAttribute(
                                                        "Has Detail",
                                                        TextUtils.isEmpty(tempEvent.mDESCRIPTION) ? "false" : "true"
                                                )
                                        );
                            }
                        }
                    }
                });
                break;

            case CalendarBottomSheet.SHEET_MODE_VIEW_EVENT:
                mEventActionBtn.setImageResource(R.drawable.ic_pencil_white_24dp);
                mEventActionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Edit Shown Event
                        addEvent(tempEvent, true);
                    }
                });
                break;

            case CalendarBottomSheet.SHEET_MODE_DATE:
            default:
                mEventActionBtn.setImageResource(R.drawable.ic_calendar_plus_white_24dp);
                mEventActionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Create New Event
                        addEvent(null, true);
                    }
                });
                break;
        }
    }

    public void showDate(final CalendarDay day, final boolean expandSheet, final boolean needUpdate){
        mShouldExpandBottomSheet = expandSheet;

        //Update Day Events in Case of Adding, Updating or Deleting Events
        if(needUpdate)
            day.mGoogleEvents = GoogleCalendarHelper.getEvents(day.mPersianDate);
        mSelectedDay = day;

        //Set Mode and Setup Sheet
        mBottomSheetMode = CalendarBottomSheet.SHEET_MODE_DATE;
        proceedToSetupBottomSheet();
    }

    public void setDateSheet(CalendarDay day){
        //Set Persian Date
        mPersianDate.setText(day.mPersianDate.getPersianLongDate());

        //Set Gregorian Date
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(day.mPersianDate.getTime());
        mGregorianDate.setText(
                Constants.weekdays_en[gregorianCalendar.get(Calendar.DAY_OF_WEEK) - 1] + ", " +
                        Constants.months_en[(gregorianCalendar.get(Calendar.MONTH))] + " " +
                        gregorianCalendar.get(Calendar.DAY_OF_MONTH) + " " +
                        gregorianCalendar.get(Calendar.YEAR)
        );

        //Set Google Calendar Events
        //mBottomSheetContainer.removeViews(1, mBottomSheetContainer.getChildCount() - 1);
        mBottomSheetContainer.removeAllViews();
        if(day.mGoogleEvents != null)
            for(final GoogleEvent googleEvent : day.mGoogleEvents){
                View eventView = LayoutInflater.from(getActivity()).inflate(R.layout.cell_bottom_sheet_day, mBottomSheetContainer, false);
                TextView eventTitle = (TextView)eventView.findViewById(R.id.event_title);

                eventView.setBackgroundResource(R.drawable.bg_calendar_today);
                if(!TextUtils.isEmpty(googleEvent.mTITLE))
                    eventTitle.setText(googleEvent.mTITLE);
                else
                    eventTitle.setText(R.string.event_no_title);

                eventView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showEvent(googleEvent);
                    }
                });

                mBottomSheetContainer.addView(eventView);
            }

        //Set Calendar Events
        if(day.mCalendarEvents != null && day.mCalendarEvents.size() > 0){
            View eventHeader = LayoutInflater.from(getActivity()).inflate(R.layout.cell_bottom_sheet_header, mBottomSheetContainer, false);
            ((TextView) eventHeader.findViewById(R.id.header_title)).setText("رویداد های روز:");
            mBottomSheetContainer.addView(eventHeader);
        }

        if(day.mCalendarEvents != null)
            for(CalendarEvent calendarEvent : day.mCalendarEvents){
                View eventView = LayoutInflater.from(getActivity()).inflate(R.layout.cell_bottom_sheet_day, mBottomSheetContainer, false);
                TextView eventTitle = (TextView)eventView.findViewById(R.id.event_title);

                eventView.setBackgroundResource(R.drawable.bg_calendar_today);
                eventTitle.setText(calendarEvent.mTitle);
                if(calendarEvent.isHoliday){
                    eventView.setBackgroundResource(R.drawable.bg_calendar_holiday);
                } else {
                    eventView.setBackgroundResource(R.drawable.bg_calendar_today);
                }
                mBottomSheetContainer.addView(eventView);
            }
    }

    public void addEvent(final GoogleEvent gEvent, final boolean isEditable){
        mShouldExpandBottomSheet = true;

        //Save Current BottomSheet State to Restore Later
        if(mBottomSheetMode == CalendarBottomSheet.SHEET_MODE_DATE)
            mPreviousBottomSheetState = mBottomSheetBehavior.getState();

        mSelectedEvent = gEvent;

        //Set Mode and Setup Sheet
        mBottomSheetMode = isEditable ? CalendarBottomSheet.SHEET_MODE_ADD_EVENT : CalendarBottomSheet.SHEET_MODE_VIEW_EVENT;
        proceedToSetupBottomSheet();
    }

    public void setAddEventSheet(GoogleEvent gEvent, boolean isEditable){
        //mBottomSheetContainer.removeViews(1, mBottomSheetContainer.getChildCount() - 1);
        mBottomSheetContainer.removeAllViews();

        View eventSheet = LayoutInflater.from(getActivity()).inflate(R.layout.cell_event_sheet, mBottomSheetContainer, false);
        mEventTitle = (TextView)eventSheet.findViewById(R.id.event_title);
        mEventDesc = (TextView)eventSheet.findViewById(R.id.event_description);
        mEventTitle.setEnabled(isEditable);
        mEventDesc.setEnabled(isEditable);

        if(gEvent != null){
            //Set Event in Case of Updating Available Event
            if(!TextUtils.isEmpty(gEvent.mTITLE))
                mEventTitle.setText(gEvent.mTITLE);
            else
                mEventTitle.setHint(R.string.event_no_title);

            if(!TextUtils.isEmpty(gEvent.mDESCRIPTION))
                mEventDesc.setText(gEvent.mDESCRIPTION);
            else
                mEventDesc.setHint(R.string.event_no_desc);
        } else {
            //Create Temp Event in Case of Adding New Event
            tempEvent = new GoogleEvent();
        }

        mBottomSheetContainer.addView(eventSheet);
    }

    public void showEvent(final GoogleEvent gEvent){
        mShouldExpandBottomSheet = true;

        //Save Current BottomSheet State to Restore Later
        if(mBottomSheetMode == CalendarBottomSheet.SHEET_MODE_DATE)
            mPreviousBottomSheetState = mBottomSheetBehavior.getState();

        //Create Temp Event
        tempEvent = gEvent.clone();
        mSelectedEvent = gEvent;

        //Set Mode and Setup Sheet
        mBottomSheetMode = CalendarBottomSheet.SHEET_MODE_VIEW_EVENT;
        proceedToSetupBottomSheet();
    }

    public void setShowEventSheet(final GoogleEvent gEvent){
        //Set Bottom Sheet
        //mBottomSheetContainer.removeViews(1, mBottomSheetContainer.getChildCount() - 1);
        mBottomSheetContainer.removeAllViews();

        //Set Event Title
        View eventTitle = LayoutInflater.from(getActivity()).inflate(R.layout.cell_bottom_sheet_day, mBottomSheetContainer, false);
        TextView eventTitleTV = (TextView)eventTitle.findViewById(R.id.event_title);
        ImageView eventTitleIcon = (ImageView)eventTitle.findViewById(R.id.event_icon);

        //Set Event Background
        eventTitle.setBackgroundResource(R.drawable.bg_calendar_today);

        //Set Event Title
        if(!TextUtils.isEmpty(gEvent.mTITLE))
            eventTitleTV.setText(gEvent.mTITLE);
        else
            eventTitleTV.setText(R.string.event_no_title);

        //Set Event Icon
        eventTitleIcon.setVisibility(View.VISIBLE);
        eventTitleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Exit Confirmation Dialog
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        //.setView(mDialogView)
                        .setTitle(getResources().getString(R.string.dialog_delete_event_title))
                        .setMessage(getResources().getString(R.string.dialog_delete_event_body))
                        .setNegativeButton(getResources().getString(R.string.dialog_button_return), null)
                        .setPositiveButton(getResources().getString(R.string.dialog_button_confirm), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                //Delete Event
                                int msgId = GoogleCalendarHelper.deleteEvent(gEvent);
                                Toast.makeText(getActivity(), msgId, Toast.LENGTH_SHORT).show();

                                //Refresh UI and show Date if Event Successfully added
                                if(msgId == R.string.event_successfully_deleted){
                                    //refreshFragment(gEvent.mStartDate.getPersianMonth() - 1);
                                    refreshFragment(gEvent.mStartDate.getPersianYear(), gEvent.mStartDate.getPersianMonth());
                                    showDate(mSelectedDay, true, true);

                                    //Update Notification
                                    Intent updateNotification = new Intent(getActivity(), NotificationUpdateService.class);
                                    getActivity().startService(updateNotification);
                                }
                            }
                        }).create();
                AndroidUtilities.showRTLDialog(dialog);
            }
        });

        mBottomSheetContainer.addView(eventTitle);

        //Set Event Description
        View eventDescription = LayoutInflater.from(getActivity()).inflate(R.layout.cell_bottom_sheet_day, mBottomSheetContainer, false);
        TextView eventDescTV = (TextView)eventDescription.findViewById(R.id.event_title);

        eventDescription.setBackgroundResource(R.drawable.bg_calendar_today);
        if(!TextUtils.isEmpty(gEvent.mDESCRIPTION))
            eventDescTV.setText(gEvent.mDESCRIPTION);
        else
            eventDescTV.setText(R.string.event_no_desc);

        mBottomSheetContainer.addView(eventDescription);
    }


    public void refreshFragment(int year, int month){
        //if Page is not in Pager Stack return since the Pager will create the Updated Page when Needed
        if (mPager.isPageShown(year, month)){
            Fragment selectedFragment = mPager.getPage(year, month, getChildFragmentManager());
            if(selectedFragment instanceof CalendarFragment)
                ((CalendarFragment) selectedFragment).refreshCalendar();
        }
    }

    public boolean onBackPressed(){

        if(mBottomSheetMode != CalendarBottomSheet.SHEET_MODE_DATE){
            //Return Bottom Sheet to Show Date Mode & Expand Bottom Sheet if it Was in Show Event Mode
            showDate(mSelectedDay, mBottomSheetMode == CalendarBottomSheet.SHEET_MODE_VIEW_EVENT, false);
            return true;
        } else if(!isBottomSheetCollapsed()){
            //Close Bottom Sheet if has Content and is Expanded
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return true;
        } else {
            return false;
        }
    }

    public class HomePagerAdapter extends FragmentPagerAdapter{

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int year = mPager.getYearAndMonth(position).getFirst();
            int month = mPager.getYearAndMonth(position).getSecond();
            return CalendarFragment.Companion.newInstance(year, month);
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }
    }
}
