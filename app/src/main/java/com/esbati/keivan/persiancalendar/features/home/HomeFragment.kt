package com.esbati.keivan.persiancalendar.features.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.*
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.components.locate
import com.esbati.keivan.persiancalendar.components.views.CalendarBottomSheet
import com.esbati.keivan.persiancalendar.components.views.CalendarPager
import com.esbati.keivan.persiancalendar.features.calendarPage.CalendarFragment
import com.esbati.keivan.persiancalendar.features.notification.NotificationUpdateService
import com.esbati.keivan.persiancalendar.features.settings.SettingsFragment
import com.esbati.keivan.persiancalendar.pojos.CalendarDay
import com.esbati.keivan.persiancalendar.pojos.UserEvent
import com.esbati.keivan.persiancalendar.repository.Repository
import com.esbati.keivan.persiancalendar.utils.Constants
import com.esbati.keivan.persiancalendar.utils.bindView
import com.esbati.keivan.persiancalendar.utils.showToast
import com.esbati.keivan.persiancalendar.utils.toDp

class HomeFragment : androidx.fragment.app.Fragment() {

    private val repository: Repository by locate()
    private var mDisplayedMonth: Int = 0
    private var mDisplayedYear: Int = 0
    private lateinit var mSelectedDay: CalendarDay

    //Toolbar
    private var mToolbarMargin: Int = 0
    private val mCoordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout by bindView(R.id.coordinator_layout)
    private val mAppbar: AppBarLayout by bindView(R.id.appbar)
    private val mCollapsingToolbar: CollapsingToolbarLayout by bindView(R.id.collapsing_toolbar)
    private val mToolbar: Toolbar by bindView(R.id.toolbar)
    private val mToolbarTitle: TextView by bindView(R.id.toolbar_title)
    private val mToolbarSubTitle: TextView by bindView(R.id.toolbar_sub_title)
    private val mToolbarBackground: ImageSwitcher by bindView(R.id.toolbar_background)
    private val mSetting: ImageView by bindView(R.id.toolbar_setting)
    private val mRightBtn: ImageView by bindView(R.id.toolbar_right_btn)
    private val mLeftBtn: ImageView by bindView(R.id.toolbar_left_btn)

    //Pager
    private val mPager: CalendarPager by bindView(R.id.pager)
    private val mPagerAdapter by lazy(LazyThreadSafetyMode.NONE) { HomeAdapter(childFragmentManager) }

    //Bottom Sheet
    private val mBottomSheet: CalendarBottomSheet by bindView(R.id.bottom_sheet)
    private val mEventActionBtn: FloatingActionButton by bindView(R.id.add_event)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupPager()
        setupBottomSheet()

        //Setup Initial Day
        mSelectedDay = repository.getToday().also {
            mDisplayedYear = it.mYear
            mDisplayedMonth = it.mMonth
        }

        //Set Viewpager to Show Current Month
        mPager.isRtL = true
        mPager.setCurrentItem(mDisplayedYear, mDisplayedMonth)
        showDate(mSelectedDay, false)

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

    private fun setupToolbar() {
        mCollapsingToolbar.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mCollapsingToolbar.scrimVisibleHeightTrigger = mCollapsingToolbar.height - 48.toDp()
                if (Build.VERSION.SDK_INT < 16) {
                    mCollapsingToolbar.viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    mCollapsingToolbar.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })

        mRightBtn.setOnClickListener { mPager.loadRightItem() }
        mLeftBtn.setOnClickListener { mPager.loadLeftItem() }
        mSetting.setOnClickListener {
            SettingsFragment().also {
                it.show(activity!!.supportFragmentManager, SettingsFragment::class.java.simpleName)
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

        mAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val abHeight = mAppbar.totalScrollRange.toFloat()
            val heightRatio = (abHeight - Math.abs(verticalOffset)) / abHeight //Range from 1 to 0
            val newToolbarMarginPixel = (heightRatio * 48.toDp()).toInt() //Range from 48 to 0
            val newButtonMarginPixel = ((1 - heightRatio) * 48.toDp()).toInt() //Range from 0 to 48

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
                        setMargins(0, 0, newButtonMarginPixel - 48.toDp(), 0)
                    }
                    mSetting.layoutParams = flp
                }

            //Set Extra Views
            mSetting.alpha = 1 - heightRatio
            mSetting.rotation = 180 * heightRatio
        })
    }

    private fun setupPager() {
        mPager.adapter = mPagerAdapter
        mPager.addOnPageChangeListener(object : CalendarPager.OnPageChangeListener() {
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
                mToolbarTitle.text = String.format("%s %s", Constants.months_fa[mDisplayedMonth - 1], mDisplayedYear)
                mToolbarSubTitle.text = String.format("%s - %s", Constants.months_en[(mDisplayedMonth + 1) % 12],
                        Constants.months_en[(mDisplayedMonth + 2) % 12])
            }

            override fun onPageScrollStateChanged(state: Int) {
                mBottomSheet.collapse()
            }
        })
    }

    private fun setupBottomSheet() {
        mBottomSheet.eventActionBtn = mEventActionBtn
        mBottomSheet.onEventListener = object : CalendarBottomSheet.OnEventListener {
            override fun onEventDeleted(deletedEvent: UserEvent) {
                repository.deleteEvent(deletedEvent).also {
                    //Refresh UI and show Date if Event Successfully added
                    if (it == 1) {
                        refreshFragment(deletedEvent.year, deletedEvent.month)

                        mSelectedDay.mEvents.remove(deletedEvent)
                        showDate(mSelectedDay, true)

                        //Update Notification
                        NotificationUpdateService.enqueueUpdate(context!!)
                    } else {
                        showToast(R.string.event_error_delete)
                    }
                }
            }

            override fun onEventEdited(editedEvent: UserEvent) {
                if (TextUtils.isEmpty(editedEvent.title) && TextUtils.isEmpty(editedEvent.description)) {
                    showToast(R.string.event_error_no_content)
                    return
                }

                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR)
                        == PackageManager.PERMISSION_GRANTED)
                    repository.saveEvent(editedEvent).also {
                        //Refresh UI and show Date if Event Successfully added
                        if (it == 1) {
                            refreshFragment(editedEvent.year, editedEvent.month)

                            mSelectedDay.mEvents.clear()
                            mSelectedDay.mEvents.addAll(repository.getEvents(
                                    mSelectedDay.mYear
                                    , mSelectedDay.mMonth
                                    , mSelectedDay.mDay
                            ))
                            showDate(mSelectedDay, true)

                            //Update Notification
                            NotificationUpdateService.enqueueUpdate(context!!)
                        } else {
                            showToast(R.string.event_error_no_calendar)
                            Log.d("Calendar", "Problem in saving event!")
                        }
                    }
                else {
                    showToast(R.string.event_error_write_permission)
                }
            }
        }
    }

    fun showDate(day: CalendarDay, expand: Boolean) {
        mSelectedDay = day
        mBottomSheet.showDate(day, expand)
    }

    fun refreshFragment(year: Int, month: Int) {
        //if Page is not in Pager Stack return since the Pager will create the Updated Page when Needed
        if (mPager.isPageShown(year, month)) {
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

    inner class HomeAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            val (year, month) = mPager.getYearAndMonth(position)
            return CalendarFragment.newInstance(year, month)
        }

        override fun getCount() = Int.MAX_VALUE
    }
}