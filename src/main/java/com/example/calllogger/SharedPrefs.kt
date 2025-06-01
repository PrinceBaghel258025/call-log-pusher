package com.example.calllogger

import android.content.Context
import android.preference.PreferenceManager

object SharedPrefs {
    fun savePhoneNumber(ctx: Context, number: String) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("phone_number", number).apply()
    }

    fun getPhoneNumber(ctx: Context): String? {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString("phone_number", null)
    }
}