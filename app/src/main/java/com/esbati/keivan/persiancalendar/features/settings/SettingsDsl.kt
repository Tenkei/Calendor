package com.esbati.keivan.persiancalendar.features.settings

import android.content.Context
import androidx.annotation.StringRes
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.esbati.keivan.persiancalendar.features.settings.cells.*


@DslMarker
annotation class ViewMarker

@ViewMarker
class Container(context: Context) : LinearLayout(context)

fun container(context: Context, init: Container.() -> Unit): Container {
    val container = Container(context)
    container.orientation = LinearLayout.VERTICAL
    container.init()
    return container
}

private fun <V : View> ViewGroup.initViewIntoGroup(view: V, init: V.() -> Unit): V {
    view.init()
    addView(view)
    return view
}

fun Container.header(@StringRes titleResId: Int) = header { title = context.getString(titleResId) }
fun Container.header(init: HeaderCell.() -> Unit) = initViewIntoGroup(HeaderCell(context), init)
fun Container.textCheck(init: TextCheckCell.() -> Unit) = initViewIntoGroup(TextCheckCell(context), init)
fun Container.textSetting(init: TextSettingsCell.() -> Unit) = initViewIntoGroup(TextSettingsCell(context), init)
fun Container.textInfo(init: TextInfoCell.() -> Unit) = initViewIntoGroup(TextInfoCell(context), init)
fun Container.shadowDivider() = initViewIntoGroup(ShadowSectionCell(context)) {}

fun View.onClick(action: (View) -> Unit) {
    setOnClickListener(action)
}