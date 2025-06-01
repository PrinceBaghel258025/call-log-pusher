package com.example.calllogger

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object StorageUtils {
    private const val FILE_NAME = "pending_calls.json"

    fun savePendingCalls(ctx: Context, data: List<JSONObject>) {
        val json = JSONArray(data).toString()
        ctx.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    fun loadPendingCalls(ctx: Context): List<JSONObject> {
        return try {
            val json = ctx.openFileInput(FILE_NAME).bufferedReader().readText()
            val array = JSONArray(json)
            List(array.length()) { array.getJSONObject(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearPendingCalls(ctx: Context) {
        ctx.deleteFile(FILE_NAME)
    }
}