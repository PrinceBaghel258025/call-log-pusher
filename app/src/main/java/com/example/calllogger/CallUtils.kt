package com.example.calllogger

import android.content.Context
import android.provider.CallLog
import android.provider.CallLog.Calls.*
import com.example.calllogger.utils.Logger

object CallUtils {
    fun getCallLogs(context: Context): List<CallData> {
        val calls = mutableListOf<CallData>()
        
        // Get calls from the last 24 hours instead of 2 hours to ensure we don't miss any
        val cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // 24 hours ago
        
        Logger.d("Fetching calls since: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(cutoffTime))}")

        val projection = arrayOf(
            NUMBER,
            TYPE,
            DATE,
            DURATION
        )

        val selection = "$DATE > ?"
        val selectionArgs = arrayOf(cutoffTime.toString())
        val sortOrder = "$DATE DESC"

        context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val numberColumn = cursor.getColumnIndex(NUMBER)
            val typeColumn = cursor.getColumnIndex(TYPE)
            val dateColumn = cursor.getColumnIndex(DATE)
            val durationColumn = cursor.getColumnIndex(DURATION)

            Logger.d("Found ${cursor.count} total calls in the last 24 hours")

            while (cursor.moveToNext()) {
                val number = cursor.getString(numberColumn)
                val type = when (cursor.getInt(typeColumn)) {
                    INCOMING_TYPE -> "incoming"
                    OUTGOING_TYPE -> "outgoing"
                    MISSED_TYPE -> "missed"
                    else -> "unknown"
                }
                val timestamp = cursor.getLong(dateColumn)
                val duration = cursor.getLong(durationColumn)

                val callTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
                Logger.d("Call: $number ($type) at $callTime, duration: $duration seconds")

                calls.add(CallData(number, type, duration, timestamp))
            }
        } ?: run {
            Logger.w("Failed to query call log - cursor is null")
        }

        Logger.d("Returning ${calls.size} calls")
        return calls
    }
}

data class CallData(
    val number: String,
    val type: String,
    val duration: Long,
    val timestamp: Long
) 