package com.example.calllogger

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class CallLogWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {
    override fun doWork(): Result {
        val phone = SharedPrefs.getPhoneNumber(applicationContext)
        if (phone.isNullOrEmpty()) return Result.failure()

        val calls = CallUtils.getCallLogs(applicationContext)
        val previous = StorageUtils.loadPendingCalls(applicationContext)
        val allCalls = previous + calls

        val success = ApiClient.postCalls(phone, allCalls)
        if (success) {
            StorageUtils.clearPendingCalls(applicationContext)
            return Result.success()
        } else {
            StorageUtils.savePendingCalls(applicationContext, allCalls)
            return Result.retry()
        }
    }
}