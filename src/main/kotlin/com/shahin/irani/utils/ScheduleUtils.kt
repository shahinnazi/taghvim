package com.shahin.irani.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.shahin.irani.variants.debugAssertNotNull
import com.shahin.irani.UPDATE_TAG
import com.shahin.irani.service.UpdateWorker
import java.util.concurrent.TimeUnit

fun startWorker(context: Context) {
    runCatching {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UPDATE_TAG, ExistingPeriodicWorkPolicy.UPDATE,
            // An hourly task to call UpdateWorker.doWork
            PeriodicWorkRequestBuilder<UpdateWorker>(1L, TimeUnit.HOURS).build()
        )
    }.onFailure(logException).getOrNull().debugAssertNotNull
}
