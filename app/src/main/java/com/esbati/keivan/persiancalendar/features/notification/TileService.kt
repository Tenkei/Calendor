package com.esbati.keivan.persiancalendar.features.notification

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.util.Log
import com.esbati.keivan.persiancalendar.features.home.MainActivity

/**
 * @author ali (alirezaiyann@gmail.com)
 * @since 7/8/19 9:58 AM.
 */

@TargetApi(Build.VERSION_CODES.N)
class TileService : android.service.quicksettings.TileService() {

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


        tile.state = Tile.STATE_ACTIVE
        tile.updateTile()
    }
}
