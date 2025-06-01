package com.example.calllogger

import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object ApiClient {
    private const val ENDPOINT = "https://your-erp-domain.com/api/method/custom_app.api.receive_call_log"
    private const val TOKEN = "token your_api_key:your_api_secret"

    fun postCalls(phone: String, calls: List<JSONObject>): Boolean {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("phone_number", phone)
            put("calls", JSONArray(calls))
        }

        val request = Request.Builder()
            .url(ENDPOINT)
            .addHeader("Authorization", TOKEN)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), json.toString()))
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: IOException) {
            false
        }
    }
}