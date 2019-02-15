package com.esbati.keivan.persiancalendar.Features.Home;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.Toolbar;
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

import org.jetbrains.annotations.NotNull;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by asus on 11/18/2016.
 */

public class HomeFragment extends Fragment {

    private int mDisplayedMonth;
    private int mDisplayedYear;
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

    private void runStartAnimation(){
        mAppbar.post(new Runnable() {
            @Override
            public void run() {
                mAppbar.setExpanded(true, true);
            }
        });

        mToolbarTitle.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_top));
        mToolbarSubTitle.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_top));
        mBottomSheet.getMPersianDate().startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right));
        mBottomSheet.getMGregorianDate().startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right));
        mEventActionBtn.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left));
        mPager.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_bottom));
     }

    private void setupToolbar(View rootView){
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

    private void setupPager(final View rootView){
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
                mBottomSheet.collapse();
            }
        });
    }

    private void setupBottomSheet(View rootView){
        mEventActionBtn = (FloatingActionButton) rootView.findViewById(R.id.add_event);
        mBottomSheet = (CalendarBottomSheet) rootView.findViewById(R.id.bottom_sheet);
        mBottomSheet.setEventActionBtn(mEventActionBtn);
        mBottomSheet.setOnEventListener(new CalendarBottomSheet.OnEventListener() {
            @Override
            public void onEventDeleted(@NotNull GoogleEvent deletedEvent) {
                int msgId = GoogleCalendarHelper.deleteEvent(deletedEvent);

                //Refresh UI and show Date if Event Successfully added
                if(msgId == R.string.event_successfully_deleted){
                    refreshFragment(deletedEvent.mStartDate.getPersianYear(), deletedEvent.mStartDate.getPersianMonth());

                    mSelectedDay.mGoogleEvents.remove(deletedEvent);
                    showDate(mSelectedDay, true);

                    //Update Notification
                    Intent updateNotification = new Intent(getActivity(), NotificationUpdateService.class);
                    getActivity().startService(updateNotification);
                } else {
                    Toast.makeText(getActivity(), msgId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEventEdited(@NotNull GoogleEvent tempEvent) {
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
    }

    public void showDate(final CalendarDay day, final boolean expandSheet){
        mSelectedDay = day;
        mBottomSheet.showDate(mSelectedDay, expandSheet);
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
        if(mBottomSheet.getMBottomSheetMode() != CalendarBottomSheet.Mode.SHEET_MODE_DATE){
            //Return Bottom Sheet to Show Date Mode & Expand Bottom Sheet if it Was in Show Event Mode
            showDate(mSelectedDay, mBottomSheet.getMBottomSheetMode() == CalendarBottomSheet.Mode.SHEET_MODE_VIEW_EVENT);
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
