package com.example.calllogger

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/method/custom_app.api.receive_call_log")
    suspend fun sendCallLogs(
        @Header("Authorization") auth: String,
        @Body payload: CallLogPayload
    ): Response<Unit>
}

object ApiClient {
    private const val BASE_URL = "YOUR_ERPNEXT_URL" // Replace with your ERPNext URL
    private const val API_KEY = "YOUR_API_KEY" // Replace with your API key
    private const val API_SECRET = "YOUR_API_SECRET" // Replace with your API secret

    fun create(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    suspend fun sendCallLogs(phoneNumber: String, calls: List<CallData>): Response<Unit> {
        val service = create()
        val auth = "token $API_KEY:$API_SECRET"
        val payload = CallLogPayload(phoneNumber, calls)
        return service.sendCallLogs(auth, payload)
    }
}

data class CallLogPayload(
    val phone_number: String,
    val calls: List<CallData>
) 