package com.example.calllogger

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.calllogger.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CallLogWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Logger.i("Starting call log sync...")
            
            val phoneNumber = SharedPrefs(context).getPhoneNumber()
            if (phoneNumber.isEmpty()) {
                Logger.w("Phone number not set, skipping sync")
                return@withContext Result.failure()
            }
            Logger.d("Phone number retrieved: $phoneNumber")

            val calls = CallUtils.getCallLogs(context)
            if (calls.isEmpty()) {
                Logger.i("No new calls to sync")
                return@withContext Result.success()
            }
            Logger.d("Retrieved ${calls.size} calls to sync")

            val apiClient = ApiClient.create()
            val payload = CallLogPayload(phoneNumber, calls)
            Logger.d("Sending payload to server: $payload")
            
            val response = apiClient.sendCallLogs(auth = "token", payload = payload)

            return@withContext if (response.isSuccessful) {
                Logger.i("Successfully synced ${calls.size} calls")
                Result.success()
            } else {
                Logger.e("Failed to sync calls. Response code: ${response.code()}")
                Result.retry()
            }
        } catch (e: Exception) {
            Logger.e("Error during call log sync", e)
            Result.retry()
        }
    }
} 