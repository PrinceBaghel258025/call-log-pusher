package com.example.calllogger

import android.content.Context
import android.provider.CallLog
import org.json.JSONObject

object CallUtils {
    fun getCallLogs(context: Context): List<JSONObject> {
        val callList = mutableListOf<JSONObject>()
        val cutoff = System.currentTimeMillis() - 2 * 60 * 60 * 1000
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val number = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
                val duration = it.getInt(it.getColumnIndexOrThrow(CallLog.Calls.DURATION))
                val type = it.getInt(it.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                val date = it.getLong(it.getColumnIndexOrThrow(CallLog.Calls.DATE))

                if (date >= cutoff) {
                    val call = JSONObject().apply {
                        put("number", number)
                        put("duration", duration)
                        put("type", type)
                        put("timestamp", date / 1000)
                    }
                    callList.add(call)
                }
            }
        }
        return callList
    }
}