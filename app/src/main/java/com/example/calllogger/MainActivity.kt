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
        val testButton = findViewById<Button>(R.id.testButton)

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

        testButton.setOnClickListener {
            Logger.d("Manually triggering worker for testing")
            triggerWorkerNow()
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
            .setRequiresBatteryNotLow(false) // Allow work even when battery is low
            .build()

        // Use 15 minutes as minimum interval for periodic work
        // WorkManager has a minimum interval of 15 minutes
        val workRequest = PeriodicWorkRequestBuilder<CallLogWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CallLogSync",
            ExistingPeriodicWorkPolicy.REPLACE, // Replace existing work to ensure it runs
            workRequest
        )

        Logger.d("Work scheduled successfully for every 15 minutes")
        
        // Check and log current work status
        checkWorkStatus()
        
        Toast.makeText(this, "Call logging service started", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun checkWorkStatus() {
        WorkManager.getInstance(this).getWorkInfosForUniqueWorkLiveData("CallLogSync")
            .observe(this) { workInfos ->
                Logger.d("Current work status: ${workInfos.size} work infos")
                workInfos.forEach { workInfo ->
                    Logger.d("Work ID: ${workInfo.id}, State: ${workInfo.state}, Tags: ${workInfo.tags}")
                }
            }
    }

    private fun triggerWorkerNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<CallLogWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
        Logger.d("One-time worker triggered for testing")
        Toast.makeText(this, "Test worker triggered", Toast.LENGTH_SHORT).show()
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