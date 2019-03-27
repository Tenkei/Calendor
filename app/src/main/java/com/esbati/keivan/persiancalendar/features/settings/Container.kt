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

    private fun <V : View> initView(view: V, init: V.() -> Unit): V {
        view.init()
        addView(view)
        return view
    }

    fun header(init: HeaderCell.() -> Unit) = initView(HeaderCell(context), init)
    fun textCheck(init: TextCheckCell.() -> Unit) = initView(TextCheckCell(context), init)
    fun textSetting(init: TextSettingsCell.() -> Unit) = initView(TextSettingsCell(context), init)
    fun textInfo(init: TextInfoCell.() -> Unit) = initView(TextInfoCell(context), init)
    fun shadowDivider() = initView(ShadowSectionCell(context)) {}


    fun header(@StringRes titleResId: Int) = header { text = context.getString(titleResId) }

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