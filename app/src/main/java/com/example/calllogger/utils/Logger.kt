package com.example.calllogger.utils

import android.util.Log
import com.example.calllogger.BuildConfig

object Logger {
    private const val TAG = "CallLogger"
    private val isDebug = BuildConfig.DEBUG

    fun d(message: String) {
        if (isDebug) {
            Log.d(TAG, message)
        }
    }

    fun i(message: String) {
        if (isDebug) {
            Log.i(TAG, message)
        }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (isDebug) {
            Log.e(TAG, message, throwable)
        }
    }

    fun w(message: String) {
        if (isDebug) {
            Log.w(TAG, message)
        }
    }
} 