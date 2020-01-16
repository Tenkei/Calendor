package com.esbati.keivan.persiancalendar.features.notification

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.util.Log
import com.esbati.keivan.persiancalendar.components.locate
import com.esbati.keivan.persiancalendar.features.home.MainActivity
import com.esbati.keivan.persiancalendar.repository.Repository
import com.esbati.keivan.persiancalendar.utils.Constants
import com.esbati.keivan.persiancalendar.utils.LanguageHelper

/**
 * @author ali (alirezaiyann@gmail.com)
 * @since 7/8/19 9:58 AM.
 *
 * This class owned by persian-calendar
 * @see <a href="https://github.com/persian-calendar/DroidPersianCalendar/blob/master/PersianCalendar/src/main/java/com/byagowi/persiancalendar/service/PersianCalendarTileService.kt"</a>
 */

@TargetApi(Build.VERSION_CODES.N)
class TileService : android.service.quicksettings.TileService() {

    private val repository: Repository by locate()

    override fun onClick() {
        try {
            startActivityAndCollapse(
                Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: Exception) {
            Log.e("TileService", "Tile onClick fail", e)
        }
    }

    override fun onStartListening() {
        qsTile?.let {
            val today = repository.getToday()

            val segmentedDate = LanguageHelper.formatStringInPersian(today.formattedDate).split(" ")

            val nameOfDay = segmentedDate[0]
            val nameOfMonth = segmentedDate[4]

            it.icon = Icon.createWithResource(this, Constants.daysIcon_fa[today.mDay])
            it.label = nameOfDay
            it.contentDescription = nameOfMonth

//          explicitly set Tile state to Active, fixes tile not being lit on some Samsung devices
            it.state = Tile.STATE_ACTIVE
            it.updateTile()
        }

    }
}
