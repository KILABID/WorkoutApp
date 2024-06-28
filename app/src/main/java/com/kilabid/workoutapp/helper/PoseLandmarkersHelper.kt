package com.kilabid.workoutapp.helper

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageProxy
import com.google.common.annotations.VisibleForTesting
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.pow

class PoseLandmarkersHelper(
    private var minPoseDetectionConfidence: Float = DEFAULT_POSE_DETECTION_CONFIDENCE,
    private var minPoseTrackingConfidence: Float = DEFAULT_POSE_TRACKING_CONFIDENCE,
    private var minPosePresenceConfidence: Float = DEFAULT_POSE_PRESENCE_CONFIDENCE,
    private var currentDelegate: Int = DELEGATE_CPU,
    private var runningMode: RunningMode = RunningMode.LIVE_STREAM,
    private val context: Context,
    private val poseLandmarkerHelperListener: LandmarkerListener? = null,
) {
    private val modelName = "pose_landmarker_lite.task"
    private var poseLandmarker: PoseLandmarker? = null

    private val pushUpPoseDetector = PushUpPoseDetector()
    private val squatPoseDetector = SquatPoseDetector()
    private val sitUpPoseDetector = SitUpPoseDetector()


    init {
        setupPoseLandmarker()
    }

    private fun setupPoseLandmarker() {
        val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath(modelName)

        when (currentDelegate) {
            DELEGATE_CPU -> baseOptionsBuilder.setDelegate(Delegate.CPU)
            DELEGATE_GPU -> baseOptionsBuilder.setDelegate(Delegate.GPU)
        }

        if (runningMode == RunningMode.LIVE_STREAM && poseLandmarkerHelperListener == null) {
            throw IllegalStateException("poseLandmarkerHelperListener must be set when runningMode is LIVE_STREAM.")
        }

        try {
            val baseOptions = baseOptionsBuilder.build()
            val optionsBuilder = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinPoseDetectionConfidence(minPoseDetectionConfidence)
                .setMinTrackingConfidence(minPoseTrackingConfidence)
                .setMinPosePresenceConfidence(minPosePresenceConfidence)
                .setNumPoses(DEFAULT_NUM_POSES)
                .setRunningMode(runningMode)

            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder
                    .setResultListener(this::returnLivestreamResult)
                    .setErrorListener(this::returnLivestreamError)
            }
            val options = optionsBuilder.build()
            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            poseLandmarkerHelperListener?.onError(
                "Pose Landmarker failed to initialize. See error logs for details"
            )
            Log.e(TAG, "MediaPipe failed to load the task with error: " + e.message)
        } catch (e: RuntimeException) {
            poseLandmarkerHelperListener?.onError(
                "Pose Landmarker failed to initialize. See error logs for details", GPU_ERROR
            )
            Log.e(TAG, "Pose landmarker failed to load model with error: " + e.message)
        }
    }

    fun detectLiveStream(
        imageProxy: ImageProxy,
        isFrontCamera: Boolean,
    ) {
        if (runningMode != RunningMode.LIVE_STREAM) {
            throw IllegalArgumentException(
                "Attempting to call detectLiveStream while not using RunningMode.LIVE_STREAM"
            )
        }
        val frameTime = SystemClock.uptimeMillis()
        val bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width,
            imageProxy.height,
            Bitmap.Config.ARGB_8888
        )

        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            if (isFrontCamera) {
                postScale(-1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat())
            }
        }
        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height, matrix, true
        )

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
        detectAsync(mpImage, frameTime)
    }

    @VisibleForTesting
    fun detectAsync(mpImage: MPImage, frameTime: Long) {
        poseLandmarker?.detectAsync(mpImage, frameTime)
    }

    private fun returnLivestreamResult(
        result: PoseLandmarkerResult,
        input: MPImage,
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()
        result.landmarks().forEachIndexed { _, poseLandmarks ->
            // Detect exercise pose using joint angles
            val exercisePosition = detectExercisePosition(poseLandmarks)
            when (exercisePosition) {
                ExercisePosition.PUSH_UP_UP -> {
                    Log.d(TAG, "Push-up UP position detected")
                    showToast("Push-up UP position detected")
                }

                ExercisePosition.PUSH_UP_DOWN -> {
                    Log.d(TAG, "Push-up DOWN position detected")
                    showToast("Push-up DOWN position detected")
                }

                ExercisePosition.SQUAT_UP -> {
                    Log.d(TAG, "Squat UP position detected")
                    showToast("Squat UP position detected")
                }

                ExercisePosition.SQUAT_DOWN -> {
                    Log.d(TAG, "Squat DOWN position detected")
                    showToast("Squat DOWN position detected")
                }

                ExercisePosition.SIT_UP_UP -> {
                    Log.d(TAG, "Sit-up UP position detected")
                    showToast("Sit-up UP position detected")
                }

                ExercisePosition.SIT_UP_DOWN -> {
                    Log.d(TAG, "Sit-up DOWN position detected")
                    showToast("Sit-up DOWN position detected")
                }

                ExercisePosition.NONE -> Log.d(TAG, "No exercise position detected")
            }
        }
        poseLandmarkerHelperListener?.onResults(
            ResultBundle(
                listOf(result),
                inferenceTime,
                input.height,
                input.width
            )
        )
    }

    private fun detectExercisePosition(landmarks: MutableList<NormalizedLandmark>): ExercisePosition {
        return when {
            // Detect push-up position using joint angles
            pushUpPoseDetector.detectPushUpPosition(landmarks) != ExercisePosition.NONE -> {
                pushUpPoseDetector.detectPushUpPosition(landmarks)
            }
            // Detect squat position using joint angles
            squatPoseDetector.detectSquatPosition(landmarks) != ExercisePosition.NONE -> {
                squatPoseDetector.detectSquatPosition(landmarks)
            }
            // Detect sit-up position using joint angles
            sitUpPoseDetector.detectSitUpPosition(landmarks) != ExercisePosition.NONE -> {
                sitUpPoseDetector.detectSitUpPosition(landmarks)
            }

            else -> ExercisePosition.NONE
        }
    }

    private fun showToast(message: String) {
        (context as? Activity)?.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    enum class ExercisePosition {
        PUSH_UP_UP,
        PUSH_UP_DOWN,
        SQUAT_UP,
        SQUAT_DOWN,
        SIT_UP_UP,
        SIT_UP_DOWN,
        NONE
    }
    private fun returnLivestreamError(error: RuntimeException) {
        poseLandmarkerHelperListener?.onError(error.message ?: "An unknown error has occurred")
    }

    fun close() {
        poseLandmarker?.close()
        poseLandmarker = null
    }
    companion object {
        const val TAG = "PoseLandmarkersHelper"
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_POSE_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_POSE_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_POSE_PRESENCE_CONFIDENCE = 0.5F
        const val DEFAULT_NUM_POSES = 1
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1

        // Landmark indices for MediaPipe Pose Landmarker
        const val POSE_LANDMARK_LEFT_SHOULDER = 11
        const val POSE_LANDMARK_RIGHT_SHOULDER = 12
        const val POSE_LANDMARK_LEFT_ELBOW = 13
        const val POSE_LANDMARK_RIGHT_ELBOW = 14
        const val POSE_LANDMARK_LEFT_WRIST = 15
        const val POSE_LANDMARK_RIGHT_WRIST = 16
        const val POSE_LANDMARK_LEFT_HIP = 23
        const val POSE_LANDMARK_RIGHT_HIP = 24
        const val POSE_LANDMARK_LEFT_KNEE = 25
        const val POSE_LANDMARK_RIGHT_KNEE = 26
        const val POSE_LANDMARK_LEFT_ANKLE = 27
        const val POSE_LANDMARK_RIGHT_ANKLE = 28
    }
    private fun FloatArray.pow(exponent: Int): FloatArray {
        val result = FloatArray(size)
        for (i in indices) {
            result[i] = this[i].pow(exponent)
        }
        return result
    }

    data class ResultBundle(
        val results: List<PoseLandmarkerResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
    )

    interface LandmarkerListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResults(resultBundle: ResultBundle)
    }
}
