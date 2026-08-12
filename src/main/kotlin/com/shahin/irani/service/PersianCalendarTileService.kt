package com.shahin.irani.service

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import com.shahin.irani.PREF_TILE_STATE
import com.shahin.irani.entities.Jdn
import com.shahin.irani.global.mainCalendar
import com.shahin.irani.ui.MainActivity
import com.shahin.irani.utils.getDayIconResource
import com.shahin.irani.utils.launchAppPendingIntent
import com.shahin.irani.utils.logException
import com.shahin.irani.utils.monthName
import com.shahin.irani.utils.preferences

/**
 * Created by Alireza Afkar on 19/6/2018AD.
 */
@RequiresApi(Build.VERSION_CODES.N)
class PersianCalendarTileService : TileService() {

    private var preferredState = Tile.STATE_ACTIVE

    override fun onClick() {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                launchAppPendingIntent()?.let(::startActivityAndCollapse)
            } else @SuppressLint("StartActivityAndCollapseDeprecated") {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(
                    Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }.onFailure(logException)
    }

    override fun onStartListening() {
        preferredState = preferences.getInt(PREF_TILE_STATE, Tile.STATE_ACTIVE)
        val tile = qsTile ?: return
        val jdn = Jdn.today()
        val today = jdn on mainCalendar
        tile.icon = Icon.createWithResource(this, getDayIconResource(today.dayOfMonth))
        tile.label = jdn.weekDayName
        tile.contentDescription = today.monthName
        tile.state = preferredState
        tile.updateTile()
    }
}

class ToggleTileActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && intent.action == TileService.ACTION_QS_TILE_PREFERENCES) {
            val preferredState = preferences.getInt(PREF_TILE_STATE, Tile.STATE_ACTIVE)
            preferences.edit {
                val state =
                    if (preferredState == Tile.STATE_ACTIVE) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
                putInt(PREF_TILE_STATE, state)
            }
        }
        finish()
    }
}


