package com.example.calllogger

import android.content.Context
import android.net.ConnectivityManager
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
            val currentTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            Logger.i("=== CallLogWorker started at $currentTime ===")
            Logger.i("Starting call log sync...")
            
            val phoneNumber = SharedPrefs(context).getPhoneNumber()
            if (phoneNumber.isEmpty()) {
                Logger.w("Phone number not set, skipping sync")
                return@withContext Result.failure()
            }
            Logger.d("Phone number retrieved: $phoneNumber")

            // Check if we have network connectivity
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo == null || !networkInfo.isConnected) {
                Logger.w("No network connectivity, will retry later")
                return@withContext Result.retry()
            }
            Logger.d("Network connectivity confirmed")

            val calls = CallUtils.getCallLogs(context)
            Logger.d("Retrieved ${calls.size} calls to sync")
            
            if (calls.isEmpty()) {
                Logger.i("No calls found in the last 24 hours")
                return@withContext Result.success()
            }

            val apiClient = ApiClient.create()
            val payload = CallLogPayload(phoneNumber, calls)
            Logger.d("Sending payload to server: $payload")
            
            try {
                val response = apiClient.sendCallLogs(payload = payload)
                Logger.d("API Response code: ${response.code()}")
                Logger.d("API Response body: ${response.body()}")
                Logger.d("API Error body: ${response.errorBody()?.string()}")
                
                return@withContext if (response.isSuccessful) {
                    Logger.i("Successfully synced ${calls.size} calls")
                    Result.success()
                } else {
                    Logger.e("Failed to sync calls. Response code: ${response.code()}")
                    Result.retry()
                }
            } catch (e: Exception) {
                Logger.e("Network error during API call", e)
                return@withContext Result.retry()
            }
        } catch (e: Exception) {
            Logger.e("Error during call log sync", e)
            return@withContext Result.retry()
        }
    }
} 