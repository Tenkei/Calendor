package com.esbati.keivan.persiancalendar.Components.Views

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import com.esbati.keivan.persiancalendar.R

class CalendarPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null
) : SmoothViewPager(context, attrs) {

    var isRtL: Boolean = false

    fun loadRightItem(){
        if (currentItem < Integer.MAX_VALUE - 1)
            currentItem += 1
    }

    fun loadLeftItem(){
        if(currentItem > 0)
            currentItem -= 1
    }

    fun setCurrentItem(year: Int, month: Int){
        val position = getPageNumber(year, month)
        super.setCurrentItem(position)
    }

    fun getPage(year: Int, month: Int, fm: FragmentManager): Fragment? {
        val pageNumber = getPageNumber(year, month)

        return fm.findFragmentByTag("android:switcher:" + R.id.pager + ":" + pageNumber)
    }

    fun isPageShown(year: Int, month: Int): Boolean {
        val pageNumber = getPageNumber(year, month)

        return pageNumber <= currentItem + offscreenPageLimit && pageNumber >= currentItem - offscreenPageLimit
    }

    fun getPageNumber(year: Int, month: Int): Int {
        val monthIndex = year * 12 + month - 1

        return if (isRtL) Integer.MAX_VALUE - monthIndex else monthIndex
    }

    fun getYearAndMonth(position: Int): Pair<Int, Int> {
        val position = if (isRtL) Integer.MAX_VALUE - position else position
        return Pair(position / 12, position % 12 + 1)
    }

    fun addOnPageChangeListener(listener: OnPageChangeListener) {
        super.addOnPageChangeListener(object: SimpleOnPageChangeListener(){
            override fun onPageScrollStateChanged(state: Int) {
                listener.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                listener.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                listener.onPageSelected(position)

                val (year, month) = getYearAndMonth(position)
                listener.onPageSelected(year, month)
            }
        })
    }

    abstract class OnPageChangeListener : ViewPager.SimpleOnPageChangeListener(){
        abstract fun onPageSelected(year: Int, month: Int)
    }
}