# Call Recording Implementation Plan

## 1. Additional Permissions & Setup
```xml
<!-- Add to AndroidManifest.xml -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## 2. New Components to Create

### 2.1 Data Models
```kotlin
// RecordingMetadata.kt
data class RecordingMetadata(
    val callId: String,
    val phoneNumber: String,
    val timestamp: Long,
    val duration: Long,
    val callType: String,
    val filePath: String,
    val uploadStatus: UploadStatus
)

enum class UploadStatus {
    PENDING,
    UPLOADING,
    UPLOADED,
    FAILED
}
```

### 2.2 Database
- Create Room database for storing recording metadata
- Tables: recordings, upload_queue
- DAOs for CRUD operations

### 2.3 Call Recording Service
- New foreground service: `CallRecordingService`
- Handles:
  - Call state monitoring
  - Audio recording
  - File management
  - Upload queue management

### 2.4 Recording Manager
- Singleton class to manage recording states
- Interface between service and storage
- Handles file naming and organization

### 2.5 Upload Worker
- Extend existing `CallLogWorker` to handle recordings
- Add retry mechanism for failed uploads
- Implement cleanup of successful uploads

## 3. Implementation Phases

### Phase 1: Setup & Permissions
1. Add new permissions to manifest
2. Update consent screen for audio recording
3. Implement permission requests
4. Create database schema and migrations

### Phase 2: Call Detection & Recording
1. Implement phone state listener
2. Create recording service
3. Handle audio capture
4. Implement file storage
5. Add recording metadata tracking

### Phase 3: Upload & Sync
1. Modify ERPNext API client
2. Implement file upload logic
3. Create upload queue manager
4. Add retry mechanism
5. Implement cleanup logic

### Phase 4: UI & User Experience
1. Add recording status indicators
2. Show upload progress
3. Add settings for recording preferences
4. Implement error notifications

## 4. Technical Considerations

### 4.1 Recording Implementation
```kotlin
class CallRecorder {
    private var mediaRecorder: MediaRecorder? = null
    
    fun startRecording(outputFile: File) {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
    }
    
    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }
}
```

### 4.2 File Management
- Store recordings in app's private directory
- Implement cleanup policy
- Handle storage space management

### 4.3 Battery Optimization
- Use WorkManager for uploads
- Implement efficient recording parameters
- Handle Doze mode restrictions

## 5. ERPNext Integration

### 5.1 API Endpoints
- Add file upload endpoint
- Update call log payload to include recording reference
- Handle authentication for file uploads

### 5.2 Metadata Structure
```json
{
    "phone_number": "string",
    "call_type": "string",
    "timestamp": "long",
    "duration": "long",
    "recording_url": "string",
    "call_log_ref": "string"
}
```

## 6. Testing Strategy

### 6.1 Unit Tests
- Recording service logic
- File management
- Upload queue handling
- Database operations

### 6.2 Integration Tests
- Permission handling
- Recording workflow
- Upload process
- Error handling

### 6.3 Manual Testing
- Different Android versions
- Various call scenarios
- Network conditions
- Storage conditions

## 7. Security Considerations

### 7.1 Data Protection
- Encrypt stored recordings
- Secure file transmission
- Implement access controls
- Handle sensitive data properly

### 7.2 Compliance
- Add consent management
- Implement recording notifications
- Handle privacy requirements
- Document data handling practices

## 8. Implementation Timeline

### Week 1
- Setup & Permissions
- Basic call detection
- Initial recording implementation

### Week 2
- Database implementation
- File management
- Basic upload functionality

### Week 3
- Upload queue management
- Retry mechanism
- Error handling

### Week 4
- UI improvements
- Testing
- Bug fixes
- Documentation

## 9. Future Enhancements

### Phase 2 Features
- Call transcription
- Recording quality settings
- Custom upload schedules
- Storage management options
- Advanced error reporting 