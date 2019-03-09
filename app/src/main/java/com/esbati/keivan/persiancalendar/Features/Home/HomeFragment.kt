package com.esbati.keivan.persiancalendar.Features.Home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.*
import com.esbati.keivan.persiancalendar.Components.ApplicationController
import com.esbati.keivan.persiancalendar.Components.Views.CalendarBottomSheet
import com.esbati.keivan.persiancalendar.Components.Views.CalendarPager
import com.esbati.keivan.persiancalendar.Features.CalendarPage.CalendarFragment
import com.esbati.keivan.persiancalendar.Features.Notification.NotificationUpdateService
import com.esbati.keivan.persiancalendar.Features.Settings.SettingFragment
import com.esbati.keivan.persiancalendar.POJOs.CalendarDay
import com.esbati.keivan.persiancalendar.POJOs.UserEvent
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.Repository.Repository
import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities
import com.esbati.keivan.persiancalendar.Utils.Constants
import ir.smartlab.persindatepicker.util.PersianCalendar

class HomeFragment : Fragment() {

    private var mDisplayedMonth: Int = 0
    private var mDisplayedYear: Int = 0
    private lateinit var mSelectedDay: CalendarDay

    //Toolbar
    private var mToolbarMargin: Int = 0
    private lateinit var mCoordinatorLayout: CoordinatorLayout
    private lateinit var mAppbar: AppBarLayout
    private lateinit var mCollapsingToolbar: CollapsingToolbarLayout
    private lateinit var mToolbar: Toolbar
    private lateinit var mToolbarTitle: TextView
    private lateinit var mToolbarSubTitle: TextView
    private lateinit var mToolbarBackground: ImageSwitcher
    private lateinit var mSetting: ImageView
    private lateinit var mRightBtn: ImageView
    private lateinit var mLeftBtn: ImageView

    //Pager
    private lateinit var mPager: CalendarPager
    private lateinit var mPagerAdapter: FragmentPagerAdapter

    //Bottom Sheet
    private lateinit var mBottomSheet: CalendarBottomSheet
    private lateinit var mEventActionBtn: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)?.apply {
            setupToolbar(this)
            setupPager(this)
            setupBottomSheet(this)

            //Setup Initial Day
            PersianCalendar().let {
                mDisplayedYear = it.persianYear
                mDisplayedMonth = it.persianMonth

                mSelectedDay = CalendarDay(it).apply {
                    mEvents = if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                            == PackageManager.PERMISSION_GRANTED)
                        Repository.getEvents(it)
                    else
                        ArrayList()
                }
            }

            //Set Viewpager to Show Current Month
            mPager.isRtL = true
            mPager.setCurrentItem(mDisplayedYear, mDisplayedMonth)
            showDate(mSelectedDay, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runStartAnimation()
    }

    private fun runStartAnimation() {
        mAppbar.post { mAppbar.setExpanded(true, true) }

        mToolbarTitle.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_top))
        mToolbarSubTitle.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_top))
        mBottomSheet.mPersianDate.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_right))
        mBottomSheet.mGregorianDate.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_right))
        mEventActionBtn.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_left))
        mPager.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in_bottom))
    }

    private fun setupToolbar(rootView: View) {
        mCoordinatorLayout = rootView.findViewById(R.id.coordinator_layout) as CoordinatorLayout
        mAppbar = rootView.findViewById(R.id.appbar) as AppBarLayout
        mCollapsingToolbar = rootView.findViewById(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        mToolbar = rootView.findViewById(R.id.toolbar) as Toolbar
        mToolbarTitle = rootView.findViewById(R.id.toolbar_title) as TextView
        mToolbarSubTitle = rootView.findViewById(R.id.toolbar_sub_title) as TextView
        mToolbarBackground = rootView.findViewById(R.id.toolbar_background) as ImageSwitcher
        mSetting = rootView.findViewById(R.id.toolbar_setting) as ImageView
        mRightBtn = rootView.findViewById(R.id.toolbar_right_btn) as ImageView
        mLeftBtn = rootView.findViewById(R.id.toolbar_left_btn) as ImageView

        mCollapsingToolbar.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                mCollapsingToolbar.scrimVisibleHeightTrigger = mCollapsingToolbar.height - AndroidUtilities.dp(48f)
                mCollapsingToolbar.viewTreeObserver.removeGlobalOnLayoutListener(this)
            }
        })

        mRightBtn.setOnClickListener { mPager.loadRightItem() }
        mLeftBtn.setOnClickListener { mPager.loadLeftItem() }
        mSetting.setOnClickListener {
            SettingFragment().also {
                it.show(activity!!.supportFragmentManager, SettingFragment::class.java.simpleName)
            }
        }

        mToolbarBackground.inAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        mToolbarBackground.outAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        mToolbarBackground.setFactory {
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            }
        }

        mAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val abHeight = mAppbar.totalScrollRange.toFloat()
            val heightRatio = (abHeight - Math.abs(verticalOffset)) / abHeight //Range from 1 to 0
            val newToolbarMarginPixel = (heightRatio * AndroidUtilities.dp(48f)).toInt() //Range from 48 to 0
            val newButtonMarginPixel = ((1 - heightRatio) * AndroidUtilities.dp(48f)).toInt() //Range from 0 to 48

            //Set Toolbar and Icon Margin, Since Padding is int Value
            if (mToolbarMargin != newToolbarMarginPixel)
                mAppbar.post {
                    mToolbarMargin = newToolbarMarginPixel
                    mToolbar.setPadding(0, 0, 0, newToolbarMarginPixel)

                    var flp = (mRightBtn.layoutParams as FrameLayout.LayoutParams).apply {
                        setMargins(0, 0, newButtonMarginPixel, 0)
                    }
                    mRightBtn.layoutParams = flp

                    flp = (mLeftBtn.layoutParams as FrameLayout.LayoutParams).apply {
                        setMargins(newButtonMarginPixel, 0, 0, 0)
                    }
                    mLeftBtn.layoutParams = flp

                    flp = (mSetting.layoutParams as FrameLayout.LayoutParams).apply {
                        setMargins(0, 0, newButtonMarginPixel - AndroidUtilities.dp(48f), 0)
                    }
                    mSetting.layoutParams = flp
                }

            //Set Extra Views
            mSetting.alpha = 1 - heightRatio
            mSetting.rotation = 180 * heightRatio
        })
    }

    private fun setupPager(view: View) {
        mPager = view.findViewById(R.id.pager) as CalendarPager
        mPagerAdapter = HomeAdapter(childFragmentManager)
        mPager.adapter = mPagerAdapter

        mPager.addOnPageChangeListener(object: CalendarPager.OnPageChangeListener() {
            override fun onPageSelected(year: Int, month: Int) {
                mDisplayedYear = year
                mDisplayedMonth = month

                //Set Toolbar Background
                val res = resources
                val icons = res.obtainTypedArray(R.array.months_background)
                val drawable = icons.getDrawable(mDisplayedMonth - 1)
                mToolbarBackground.setImageDrawable(drawable)
                icons.recycle()

                //Set Toolbar Title
                mToolbarTitle.text = Constants.months[mDisplayedMonth - 1] + " " + mDisplayedYear
                mToolbarSubTitle.text = (Constants.months_en[(mDisplayedMonth + 1) % 12]
                        + " - "
                        + Constants.months_en[(mDisplayedMonth + 2) % 12])
            }

            override fun onPageScrollStateChanged(state: Int) {
                mBottomSheet.collapse()
            }
        })
    }

    private fun setupBottomSheet(view: View) {
        mEventActionBtn = view.findViewById(R.id.add_event) as FloatingActionButton
        mBottomSheet = view.findViewById(R.id.bottom_sheet) as CalendarBottomSheet
        mBottomSheet.eventActionBtn = mEventActionBtn
        mBottomSheet.onEventListener = object: CalendarBottomSheet.OnEventListener {
            override fun onEventDeleted(deletedEvent: UserEvent) {
                Repository.deleteEvent(deletedEvent).also {
                    //Refresh UI and show Date if Event Successfully added
                    if (it == 1) {
                        refreshFragment(deletedEvent.mStartDate.persianYear, deletedEvent.mStartDate.persianMonth)

                        mSelectedDay.mEvents.remove(deletedEvent)
                        showDate(mSelectedDay, true)

                        //Update Notification
                        NotificationUpdateService.enqueueUpdate(context!!)
                    } else {
                        Toast.makeText(context, "Problem in deleting event!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onEventEdited(editedEvent: UserEvent) {
                if(TextUtils.isEmpty(editedEvent.title) && TextUtils.isEmpty(editedEvent.description)){
                    Toast.makeText(context, R.string.event_error_no_content, Toast.LENGTH_SHORT).show()
                    return
                }

                if(ContextCompat.checkSelfPermission(ApplicationController.getContext(), Manifest.permission.WRITE_CALENDAR)
                        == PackageManager.PERMISSION_GRANTED)
                    Repository.saveEvent(editedEvent).also {
                        //Refresh UI and show Date if Event Successfully added
                        if (it == 1) {
                            refreshFragment(editedEvent.mStartDate.persianYear, editedEvent.mStartDate.persianMonth)

                            mSelectedDay.mEvents = Repository.getEvents(mSelectedDay.mPersianDate)
                            showDate(mSelectedDay, true)

                            //Update Notification
                            NotificationUpdateService.enqueueUpdate(context!!)
                        } else {
                            Toast.makeText(context, "Problem in saving event!", Toast.LENGTH_SHORT).show()
                            Log.d("Calendar", getString(R.string.event_error_no_calendar))
                        }
                    }
                else
                    Toast.makeText(context, R.string.event_error_write_permission, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showDate(day: CalendarDay, expand: Boolean){
        mSelectedDay = day
        mBottomSheet.showDate(day, expand)
    }

    fun refreshFragment(year: Int, month: Int){
        //if Page is not in Pager Stack return since the Pager will create the Updated Page when Needed
        if(mPager.isPageShown(year, month)){
            val selectedFragment = mPager.getPage(year, month, childFragmentManager)
            if (selectedFragment is CalendarFragment)
                selectedFragment.refreshCalendar()
        }
    }

    fun onBackPressed() = when {
        mBottomSheet.mBottomSheetMode != CalendarBottomSheet.Mode.SHEET_MODE_DATE -> {
            showDate(mSelectedDay, mBottomSheet.mBottomSheetMode == CalendarBottomSheet.Mode.SHEET_MODE_VIEW_EVENT)
            true
        }

        !mBottomSheet.isCollapsed() -> {
            mBottomSheet.collapse()
            true
        }

        else -> false
    }

    inner class HomeAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){

        override fun getItem(position: Int): Fragment {
            val (year, month) = mPager.getYearAndMonth(position)
            return CalendarFragment.newInstance(year, month)
        }

        override fun getCount() = Int.MAX_VALUE
    }
}