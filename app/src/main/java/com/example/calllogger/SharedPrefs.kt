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
    }

    fun savePhoneNumber(phoneNumber: String) {
        prefs.edit().putString(KEY_PHONE_NUMBER, phoneNumber).apply()
    }

    fun getPhoneNumber(): String {
        return prefs.getString(KEY_PHONE_NUMBER, "") ?: ""
    }
} 