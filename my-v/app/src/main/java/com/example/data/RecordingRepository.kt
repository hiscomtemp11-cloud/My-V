package com.example.data

import android.content.Context
import android.media.MediaRecorder
import android.util.Base64
import android.util.Log
import com.example.data.db.RecordingDao
import com.example.data.db.RecordingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

class RecordingRepository(
    private val context: Context,
    private val dao: RecordingDao
) {
    private val TAG = "RecordingRepository"
    private var mediaRecorder: MediaRecorder? = null
    private var currentOutputFile: File? = null
    private var startTime: Long = 0

    val allRecordings: Flow<List<RecordingEntity>> = dao.getAllRecordings()

    suspend fun getRecordingById(id: Int): RecordingEntity? = withContext(Dispatchers.IO) {
        dao.getRecordingById(id)
    }

    /**
     * Prepares and starts voice recording.
     * Output format as .m4a (MPEG_4 containing AAC) for optimal Gemini compatibility.
     */
    fun startRecording(scriptName: String): File {
        val dir = File(context.filesDir, "recordings").apply {
            if (!exists()) mkdirs()
        }
        val file = File(dir, "rec_${System.currentTimeMillis()}.m4a")
        currentOutputFile = file

        mediaRecorder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            
            // Audio calibration configs
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            
            prepare()
            start()
        }
        
        startTime = System.currentTimeMillis()
        Log.d(TAG, "Recording started -> ${file.absolutePath}")
        return file
    }

    /**
     * Stops the media recorder and returns the recorded duration in milliseconds.
     */
    fun stopRecording(): Long {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            
            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "Recording stopped. Duration: $duration ms")
            duration
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording correctly: ${e.message}")
            0L
        }
    }

    fun cancelRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            // Ignore
        }
        mediaRecorder = null
        currentOutputFile?.delete()
        currentOutputFile = null
    }

    /**
     * Converts a file to base64, runs real-time Gemini Sentiment analysis with pro-grade Metrics,
     * and saves the parsed report entity to the database history.
     */
    suspend fun analyzeAndSaveRecording(
        title: String,
        file: File,
        durationMs: Long,
        scriptName: String,
        scriptText: String? = null
    ): RecordingEntity = withContext(Dispatchers.IO) {
        val base64 = fileToBase64(file)
        
        // Call modern preview model via client
        val report = GeminiClient.analyzeRecording(title, base64, scriptText ?: scriptName)

        val entity = RecordingEntity(
            title = title,
            filePath = file.absolutePath,
            durationMs = durationMs,
            timestamp = System.currentTimeMillis(),
            scriptName = scriptName,
            sentiment = report.summary,
            voiceScore = report.voiceScore,
            pitchHz = report.pitchHz,
            clarityPercent = report.clarityPercent,
            emotionalTone = report.emotionalTone,
            transcript = report.transcript
        )

        val id = dao.insertRecording(entity)
        entity.copy(id = id.toInt())
    }

    suspend fun deleteRecording(entity: RecordingEntity) = withContext(Dispatchers.IO) {
        try {
            val file = File(entity.filePath)
            if (file.exists()) {
                file.delete()
            }
            dao.deleteRecordingById(entity.id)
            Log.d(TAG, "Successfully deleted recording and database entry.")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting recording: ${e.message}")
        }
    }

    private fun fileToBase64(file: File): String? {
        if (!file.exists()) return null
        return try {
            val size = file.length().toInt()
            val bytes = ByteArray(size)
            FileInputStream(file).use { stream ->
                stream.read(bytes)
            }
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e(TAG, "Error encoding file to Base64: ${e.message}")
            null
        }
    }
}
