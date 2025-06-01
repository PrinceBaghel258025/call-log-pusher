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
import com.example.calllogger.utils.Logger

class MainActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Check if phone number is already saved
        val prefs = SharedPrefs(this)
        val savedPhoneNumber = prefs.getPhoneNumber()
        if (savedPhoneNumber.isNotEmpty()) {
            Logger.d("Found saved phone number: $savedPhoneNumber")
            // Phone number exists, just check permissions and schedule work
            checkPermissionAndScheduleWork()
        }

        submitButton.setOnClickListener {
            val phoneNumber = phoneInput.text.toString()
            if (phoneNumber.isNotEmpty()) {
                Logger.d("Saving new phone number: $phoneNumber")
                prefs.savePhoneNumber(phoneNumber)
                checkPermissionAndScheduleWork()
            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionAndScheduleWork() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Logger.d("Requesting READ_CALL_LOG permission")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CALL_LOG),
                PERMISSION_REQUEST_CODE
            )
        } else {
            Logger.d("Permission already granted, scheduling work")
            scheduleWork()
        }
    }

    private fun scheduleWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // For testing: Run every 15 minutes instead of 2 hours
        val workRequest = PeriodicWorkRequestBuilder<CallLogWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CallLogSync",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        Logger.d("Work scheduled successfully")
        Toast.makeText(this, "Call logging service started", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            Logger.d("Permission granted by user")
            scheduleWork()
        } else {
            Logger.w("Permission denied by user")
            Toast.makeText(
                this,
                "Call log permission is required for this app to work",
                Toast.LENGTH_LONG
            ).show()
        }
    }
} 