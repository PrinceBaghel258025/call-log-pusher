# Call Logger App

An Android application that logs call details and sends them to a webhook endpoint every 15 minutes.

## Features

- Logs incoming, outgoing, and missed calls
- Stores user's phone number locally
- Runs in background using WorkManager
- Sends call logs to a configurable webhook endpoint
- Handles permissions automatically

## Prerequisites

- Android Studio Arctic Fox (2021.3.1) or newer
- JDK 17
- Android SDK with minimum API level 24 (Android 7.0)
- Android device or emulator for testing

## Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd CallLoggerApp
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository and select it

3. **Gradle Sync**
   - Wait for the initial Gradle sync to complete
   - If sync fails:
     - Click "File" > "Sync Project with Gradle Files"
     - Or click the "Sync Now" link in any error messages

4. **Configure Webhook URL**
   - Open `app/src/main/java/com/example/calllogger/ApiClient.kt`
   - Update the webhook ID in the `@POST` annotation with your webhook URL
   ```kotlin
   @POST("your-webhook-id-here")
   ```

5. **Build the Project**
   - Click "Build" > "Make Project" (or press Ctrl+F9)
   - Fix any build errors if they occur
   - Ensure all dependencies are downloaded

## Running the App

1. **Connect Android Device**
   - Enable Developer Options on your device:
     1. Go to Settings > About Phone
     2. Tap "Build Number" 7 times
     3. You'll see a message that you're now a developer
   - Enable USB Debugging:
     1. Go to Settings > Developer Options
     2. Enable "USB Debugging"
   - Connect your device via USB
   - Allow USB debugging when prompted on your device

2. **Run the App**
   - Click the "Run" button (green play icon) in Android Studio
   - Select your connected device
   - Click "OK" to install and run the app

3. **First Launch**
   - Enter your phone number when prompted
   - Grant the call log permission when requested
   - The app will show "Call logging service started"

## Viewing Logs

1. **Using Android Studio**
   - Open the "Logcat" window (View > Tool Windows > Logcat)
   - In the search box, enter: `tag:CallLogger`
   - You'll see all app-related logs

2. **Using ADB (Command Line)**
   ```bash
   adb logcat -s CallLogger
   ```

## Troubleshooting

1. **Gradle Sync Issues**
   - Update Android Studio to the latest version
   - Invalidate caches: File > Invalidate Caches / Restart
   - Check your internet connection for dependency downloads

2. **Permission Issues**
   - Ensure call log permission is granted in device settings
   - App Info > Permissions > Phone > Allow

3. **Background Service**
   - Check if the app is allowed to run in background
   - Device Settings > Apps > Call Logger > Battery > Unrestricted

4. **Webhook Not Receiving Data**
   - Verify internet connection
   - Check Logcat for API response codes and errors
   - Verify webhook URL is correctly configured

## Architecture

- Built with Kotlin
- Uses WorkManager for background tasks
- Retrofit for API calls
- SharedPreferences for local storage
- AndroidX components for modern Android features

## Contributing

Feel free to submit issues and enhancement requests! 