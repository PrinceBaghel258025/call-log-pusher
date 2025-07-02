package com.example.calllogger

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.calllogger.utils.Logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

interface ApiService {
    @POST("bc3761b2-eed8-47c8-9b9a-6359c4f42220")
    suspend fun sendCallLogs(
        @Body payload: CallLogPayload
    ): Response<Unit>
}

object ApiClient {
    private const val BASE_URL = "https://erp.vastuvihar.org/api/method/vastu_vihar.vastu_vihar.custom_script.lead.call_logs/"

    fun create(): ApiService {
        Logger.d("Creating API client with BASE_URL: $BASE_URL")
        
        val logging = HttpLoggingInterceptor { message -> 
            Logger.d("OkHttp: $message") 
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    suspend fun sendCallLogs(phoneNumber: String, calls: List<CallData>): Response<Unit> {
        val service = create()
        val payload = CallLogPayload(phoneNumber, calls)
        Logger.d("Making API request to: $BASE_URL")
        return service.sendCallLogs(payload)
    }
}

data class CallLogPayload(
    val phone_number: String,
    val calls: List<CallData>
)