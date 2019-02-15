package com.esbati.keivan.persiancalendar.Features.Home;

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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
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
import com.esbati.keivan.persiancalendar.POJOs.GoogleEvent;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Features.Notification.NotificationUpdateService;
import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities;
import com.esbati.keivan.persiancalendar.Utils.Constants;
import com.esbati.keivan.persiancalendar.Repository.GoogleCalendarHelper;
import com.esbati.keivan.persiancalendar.Components.Views.CalendarBottomSheet;

import ir.smartlab.persindatepicker.util.PersianCalendar;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/**
 * Created by asus on 11/18/2016.
 */

public class HomeFragment extends Fragment {

    private int mDisplayedMonth;
    private int mDisplayedYear;
    private int mPreviousBottomSheetState;
    private boolean mShouldUpdateBottomSheet;
    private boolean mShouldExpandBottomSheet;
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

    //Pager
    private CalendarPager mPager;
    private FragmentPagerAdapter mPagerAdapter;

    //Bottom Sheet
    private CalendarBottomSheet mBottomSheet;
    private FloatingActionButton mEventActionBtn;

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
        mSelectedDay = new CalendarDay(today);
        mSelectedDay.mGoogleEvents = GoogleCalendarHelper.getEvents(mSelectedDay.mPersianDate);

        //Setup Views
        setupToolbar(rootView);
        setupBottomSheet(rootView);
        setupPager(rootView);

        //Set Viewpager to Show Current Month
        mPager.setRtL(true);
        mPager.setCurrentItem(mDisplayedYear, mDisplayedMonth);
        showDate(mSelectedDay, false);
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
        mBottomSheet.mPersianDate.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right));
        mBottomSheet.mGregorianDate.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right));
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
                mBottomSheet.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    public void setupBottomSheet(View rootView){
        mEventActionBtn = (FloatingActionButton) rootView.findViewById(R.id.add_event);
        mBottomSheet = (CalendarBottomSheet) rootView.findViewById(R.id.bottom_sheet);
        mBottomSheet.mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheet.mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                //Change BottomSheet Content if Needed
                if(mBottomSheet.isCollapsed() && mShouldUpdateBottomSheet){
                    Log.e("BTS", "Item " + count + " Updated: Callback");
                    mShouldUpdateBottomSheet = false;

                    setBottomSheetMode(mBottomSheet.mBottomSheetMode);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                /*if(mBottomSheet.mBottomSheetMode == CalendarBottomSheet.Mode.SHEET_MODE_EDIT_EVENT){
                    mAppbar.scroll(mAppbar.getHeight() * v * -1);
                }*/
            }
        });
    }

    public void showDate(final CalendarDay day, final boolean expandSheet){
        mShouldExpandBottomSheet = expandSheet;
        mSelectedDay = day;

        proceedToSetupBottomSheet(CalendarBottomSheet.Mode.SHEET_MODE_DATE);
    }

    public void showEvent(final GoogleEvent gEvent){
        mShouldExpandBottomSheet = true;
        mSelectedEvent = gEvent;

        proceedToSetupBottomSheet(CalendarBottomSheet.Mode.SHEET_MODE_VIEW_EVENT);
    }

    public void editEvent(final GoogleEvent gEvent, final boolean isEditable){
        mShouldExpandBottomSheet = true;
        mSelectedEvent = gEvent;

        proceedToSetupBottomSheet(isEditable ? CalendarBottomSheet.Mode.SHEET_MODE_EDIT_EVENT : CalendarBottomSheet.Mode.SHEET_MODE_VIEW_EVENT);
    }

    public void proceedToSetupBottomSheet(final CalendarBottomSheet.Mode mode){
        //Save Current state to restore later if moving from main mode
        if(mode != CalendarBottomSheet.Mode.SHEET_MODE_DATE
                && mBottomSheet.mBottomSheetMode == CalendarBottomSheet.Mode.SHEET_MODE_DATE)
            mPreviousBottomSheetState = mBottomSheet.mBottomSheetBehavior.getState();

        mBottomSheet.mBottomSheetMode = mode;

        count++;
        Log.e("BTS", "Item " + count + " Proceed to Updated");

        //If Sheet is Flat or Collapsed Set it Up
        if(mBottomSheet.isCollapsed()){
            Log.e("BTS", "Item " + count + " Updated: Normally");

            if(mBottomSheet.mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED)
                mBottomSheet.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            setBottomSheetMode(mode);
        } else {
            //FIXME Sometimes if Item is in Settling Mode It won't Change Mode to Collapsed
            if(mBottomSheet.mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_SETTLING)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Force Update if BottomSheet got Stuck
                        if(mShouldUpdateBottomSheet){
                            Log.e("BTS", "Item " + count + " Updated: Forced");
                            setBottomSheetMode(mode);
                        }
                    }
                }, 300);

            //If Sheet is not Collapsed, Collapse it then Set it Up
            mShouldUpdateBottomSheet = true;
            mBottomSheet.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void setBottomSheetMode(CalendarBottomSheet.Mode mode){
        switch (mode) {
            case SHEET_MODE_DATE:
            default:
                mBottomSheet.setDateSheet(mSelectedDay, new Function1<GoogleEvent, Unit>() {
                    @Override
                    public Unit invoke(GoogleEvent googleEvent) {
                        showEvent(googleEvent);
                        return null;
                    }
                });

                mEventActionBtn.setImageResource(R.drawable.ic_calendar_plus_white_24dp);
                mEventActionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editEvent(null, true);
                    }
                });
                break;

            case SHEET_MODE_VIEW_EVENT:
                mBottomSheet.setShowEventSheet(mSelectedEvent, new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        int msgId = GoogleCalendarHelper.deleteEvent(mSelectedEvent);

                        //Refresh UI and show Date if Event Successfully added
                        if(msgId == R.string.event_successfully_deleted){
                            refreshFragment(mSelectedEvent.mStartDate.getPersianYear(), mSelectedEvent.mStartDate.getPersianMonth());

                            mSelectedDay.mGoogleEvents.remove(mSelectedEvent);
                            showDate(mSelectedDay, true);

                            //Update Notification
                            Intent updateNotification = new Intent(getActivity(), NotificationUpdateService.class);
                            getActivity().startService(updateNotification);
                        } else {
                            Toast.makeText(getActivity(), msgId, Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    }
                });

                mEventActionBtn.setImageResource(R.drawable.ic_pencil_white_24dp);
                mEventActionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editEvent(mSelectedEvent, true);
                    }
                });
                break;

            case SHEET_MODE_EDIT_EVENT:
                final GoogleEvent tempEvent = mSelectedEvent != null
                        ? mSelectedEvent.clone()
                        : new GoogleEvent(mSelectedDay);

                mBottomSheet.setEditEventSheet(tempEvent);

                mEventActionBtn.setImageResource(R.drawable.ic_check_white_24dp);
                mEventActionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AndroidUtilities.hideSoftKeyboard(view);

                        //Save Event
                        tempEvent.mTITLE = mBottomSheet.mEventTitle.getText().toString();
                        tempEvent.mDESCRIPTION = mBottomSheet.mEventDesc.getText().toString();

                        int msgId = GoogleCalendarHelper.saveEvent(tempEvent);

                        //Refresh UI and show Date if Event Successfully added
                        if(msgId == R.string.event_successfully_added || msgId == R.string.event_successfully_updated){
                            refreshFragment(tempEvent.mStartDate.getPersianYear(), tempEvent.mStartDate.getPersianMonth());

                            mSelectedDay.mGoogleEvents = GoogleCalendarHelper.getEvents(mSelectedDay.mPersianDate);
                            showDate(mSelectedDay, true);

                            //Update Notification
                            Intent updateNotification = new Intent(getActivity(), NotificationUpdateService.class);
                            getActivity().startService(updateNotification);
                        } else {
                            Toast.makeText(getActivity(), msgId, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }

        //Expand View If Needed
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //If BottomSheet is in the Date mode restore any previous state if available, else just expand it
                if(mBottomSheet.mBottomSheetMode == CalendarBottomSheet.Mode.SHEET_MODE_DATE && mPreviousBottomSheetState > 0){
                    //If BottomSheet is Stuck in Settling Set it to Collapse
                    if(mPreviousBottomSheetState == BottomSheetBehavior.STATE_SETTLING)
                        mPreviousBottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;

                    mBottomSheet.mBottomSheetBehavior.setState(mPreviousBottomSheetState);
                    mPreviousBottomSheetState = 0;
                } else if(mShouldExpandBottomSheet) {
                    mBottomSheet.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        }, 200);
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
        if(mBottomSheet.mBottomSheetMode != CalendarBottomSheet.Mode.SHEET_MODE_DATE){
            //Return Bottom Sheet to Show Date Mode & Expand Bottom Sheet if it Was in Show Event Mode
            showDate(mSelectedDay, mBottomSheet.mBottomSheetMode == CalendarBottomSheet.Mode.SHEET_MODE_VIEW_EVENT);
            return true;
        } else if(!mBottomSheet.isCollapsed()){
            //Close Bottom Sheet if has Content and is Expanded
            mBottomSheet.collapse();
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
