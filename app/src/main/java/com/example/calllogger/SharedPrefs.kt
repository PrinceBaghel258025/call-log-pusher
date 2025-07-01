package com.example.calllogger

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "CallLoggerPrefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_LAST_SYNC_TIME = "last_sync_time"
    }

    fun savePhoneNumber(phoneNumber: String) {
        prefs.edit().putString(KEY_PHONE_NUMBER, phoneNumber).apply()
    }

    fun getPhoneNumber(): String {
        return prefs.getString(KEY_PHONE_NUMBER, "") ?: ""
    }

    fun saveLastSyncTime(timestamp: Long) {
        prefs.edit().putLong(KEY_LAST_SYNC_TIME, timestamp).apply()
    }

    fun getLastSyncTime(): Long {
        return prefs.getLong(KEY_LAST_SYNC_TIME, 0L)
    }

    fun resetLastSyncTime() {
        prefs.edit().remove(KEY_LAST_SYNC_TIME).apply()
    }
} 