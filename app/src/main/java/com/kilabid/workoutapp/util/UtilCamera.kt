package com.kilabid.workoutapp.util

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.kilabid.workoutapp.helper.PoseLandmarkersHelper
import java.util.concurrent.ExecutorService

object CameraUtil {
    fun allPermissionsGranted(activity: Activity, permissions: Array<String>): Boolean {
        return permissions.all { activity.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
    }

    fun requestPermissionsIfNeeded(activity: Activity, requestCode: Int, permissions: Array<String>) {
        if (!allPermissionsGranted(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }
    }

    fun setUpCamera(
        activity: AppCompatActivity,
        cameraSelector: CameraSelector,
        preview: Preview,
        imageAnalyzer: ImageAnalysis,
        executor: ExecutorService,
        onBind: (Camera) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            try {
                val camera = cameraProvider.bindToLifecycle(
                    activity, cameraSelector, preview, imageAnalyzer
                )
                onBind(camera)
            } catch (exc: Exception) {
                // Handle exception
                Log.e("SetupCamera", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(activity))
    }
}

object PoseUtil {
    fun initializePoseLandmarkerHelper(
        activity: AppCompatActivity,
        listener: PoseLandmarkersHelper.LandmarkerListener,
        exerciseType: PoseLandmarkersHelper.ExerciseType
    ): PoseLandmarkersHelper {
        return PoseLandmarkersHelper(
            context = activity,
            runningMode = RunningMode.LIVE_STREAM,
            poseLandmarkerHelperListener = listener,
            minPosePresenceConfidence = 0.5f,
            minPoseTrackingConfidence = 0.5f,
            currentDelegate = 0,
            exerciseType = exerciseType,
            minPoseDetectionConfidence = 0.5f
        )
    }

    fun detectPose(
        poseLandmarkersHelper: PoseLandmarkersHelper,
        imageProxy: ImageProxy,
        isFrontCamera: Boolean
    ) {
        poseLandmarkersHelper.detectLiveStream(
            imageProxy = imageProxy,
            isFrontCamera = isFrontCamera
        )
    }
}
