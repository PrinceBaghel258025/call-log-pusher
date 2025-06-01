package com.example.calllogger

import android.content.Context
import android.provider.CallLog
import android.provider.CallLog.Calls.*

object CallUtils {
    fun getCallLogs(context: Context): List<CallData> {
        val calls = mutableListOf<CallData>()
        val cutoffTime = System.currentTimeMillis() - (2 * 60 * 60 * 1000) // 2 hours ago

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

                calls.add(CallData(number, type, duration, timestamp))
            }
        }

        return calls
    }
}

data class CallData(
    val number: String,
    val type: String,
    val duration: Long,
    val timestamp: Long
) 