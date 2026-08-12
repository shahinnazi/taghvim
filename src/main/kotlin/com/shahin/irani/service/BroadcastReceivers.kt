package com.shahin.irani.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast
import com.shahin.irani.variants.debugLog
import com.shahin.irani.ADD_EVENT
import com.shahin.irani.BROADCAST_ALARM
import com.shahin.irani.BROADCAST_RESTART_APP
import com.shahin.irani.BROADCAST_UPDATE_APP
import com.shahin.irani.KEY_EXTRA_PRAYER
import com.shahin.irani.KEY_EXTRA_PRAYER_TIME
import com.shahin.irani.MONTH_NEXT_COMMAND
import com.shahin.irani.MONTH_PREV_COMMAND
import com.shahin.irani.MONTH_RESET_COMMAND
import com.shahin.irani.R
import com.shahin.irani.entities.PrayTime
import com.shahin.irani.ui.calendar.AddEventData
import com.shahin.irani.utils.logException
import com.shahin.irani.utils.startAthan
import com.shahin.irani.utils.startWorker
import com.shahin.irani.utils.update
import com.shahin.irani.utils.updateMonthWidget

class BroadcastReceivers : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        when (val action = intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            TelephonyManager.ACTION_PHONE_STATE_CHANGED,
            BROADCAST_RESTART_APP -> startWorker(context)

            Intent.ACTION_DATE_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> update(context, true)
            Intent.ACTION_TIME_CHANGED, Intent.ACTION_SCREEN_ON, BROADCAST_UPDATE_APP ->
                update(context, false)

            ADD_EVENT -> runCatching {
                val addEventIntent = AddEventData.upcoming().asIntent()
                context.startActivity(addEventIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }.onFailure(logException).onFailure {
                Toast.makeText(context, R.string.device_does_not_support, Toast.LENGTH_SHORT).show()
            }

            BROADCAST_ALARM -> {
                val key = PrayTime.fromName(intent.getStringExtra(KEY_EXTRA_PRAYER)) ?: return
                val intendedTime = intent.getLongExtra(KEY_EXTRA_PRAYER_TIME, 0).takeIf { it != 0L }
                debugLog("Alarms: AlarmManager for $key")
                startAthan(context, key, intendedTime)
            }

            null -> Unit
            else -> {
                if (action.startsWith(MONTH_PREV_COMMAND)) {
                    action.replace(MONTH_PREV_COMMAND, "").toIntOrNull()?.let { id ->
                        updateMonthWidget(context, id, -1)
                    }
                } else if (action.startsWith(MONTH_NEXT_COMMAND)) {
                    action.replace(MONTH_NEXT_COMMAND, "").toIntOrNull()?.let { id ->
                        updateMonthWidget(context, id, 1)
                    }
                } else if (action.startsWith(MONTH_RESET_COMMAND)) {
                    action.replace(MONTH_RESET_COMMAND, "").toIntOrNull()?.let { id ->
                        updateMonthWidget(context, id, 0)
                    }
                }
            }
        }
    }
}
