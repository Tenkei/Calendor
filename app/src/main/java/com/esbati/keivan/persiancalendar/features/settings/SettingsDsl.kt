package com.esbati.keivan.persiancalendar.features.settings

import android.content.Context
import android.support.annotation.StringRes
import android.view.View
import android.widget.LinearLayout
import com.esbati.keivan.persiancalendar.features.settings.cells.*


@DslMarker
annotation class ViewMarker

@ViewMarker
class Container(context: Context) : LinearLayout(context) {

    init {
        orientation = LinearLayout.VERTICAL
    }

    private fun <V : View> initViewIntoGroup(view: V, init: V.() -> Unit): V {
        view.init()
        addView(view)
        return view
    }

    fun header(@StringRes titleResId: Int) = header { title = context.getString(titleResId) }
    fun header(init: HeaderCell.() -> Unit) = initViewIntoGroup(HeaderCell(context), init)
    fun textCheck(init: TextCheckCell.() -> Unit) = initViewIntoGroup(TextCheckCell(context), init)
    fun textSetting(init: TextSettingsCell.() -> Unit) = initViewIntoGroup(TextSettingsCell(context), init)
    fun textInfo(init: TextInfoCell.() -> Unit) = initViewIntoGroup(TextInfoCell(context), init)
    fun shadowDivider() = initViewIntoGroup(ShadowSectionCell(context)) {}

    companion object {
        fun container(context: Context, init: Container.() -> Unit): Container {
            val container = Container(context)
            container.init()
            return container
        }
    }
}

fun View.onClick(action: (View) -> Unit) {
    setOnClickListener(action)
}