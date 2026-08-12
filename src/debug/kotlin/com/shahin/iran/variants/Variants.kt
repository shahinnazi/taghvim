package com.shahin.irani.variants

import android.util.Log
import com.shahin.irani.LOG_TAG

fun debugLog(vararg message: Any?) {
    Log.d(LOG_TAG, message.joinToString(", "))
}

inline val <T> T.debugAssertNotNull: T inline get() = checkNotNull(this)
