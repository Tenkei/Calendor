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
        val tile = qsTile

        val today = repository.getToday()

        val segmentedDate = LanguageHelper.formatStringInPersian(today.formattedDate).split(" ")

        val nameOfDay = segmentedDate[0]
        val nameOfMonth = segmentedDate[4]

        tile.icon = Icon.createWithResource(this, Constants.daysIcon_fa[today.mDay])
        tile.label = nameOfDay
        tile.contentDescription = nameOfMonth

//      explicitly set Tile state to Active, fixes tile not being lit on some Samsung devices
        tile.state = Tile.STATE_ACTIVE
        tile.updateTile()
    }
}
