package com.example.calllogger

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var phoneInput: EditText
    private lateinit var saveButton: Button
    private val REQUEST_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        phoneInput = findViewById(R.id.phone_input)
        saveButton = findViewById(R.id.save_button)

        saveButton.setOnClickListener {
            val phone = phoneInput.text.toString().trim()
            if (phone.isNotEmpty()) {
                SharedPrefs.savePhoneNumber(this, phone)
                requestPermissions()
            }
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(Manifest.permission.READ_CALL_LOG)
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            scheduleWorker()
            Toast.makeText(this, "Permissions granted, worker scheduled", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), REQUEST_PERMISSIONS)
        }
    }

    private fun scheduleWorker() {
        val workRequest = PeriodicWorkRequestBuilder<CallLogWorker>(2, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CallLogSync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}