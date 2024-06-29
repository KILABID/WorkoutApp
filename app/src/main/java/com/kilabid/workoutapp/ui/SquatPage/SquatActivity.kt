package com.kilabid.workoutapp.ui.SquatPage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.kilabid.workoutapp.CameraViewModel
import com.kilabid.workoutapp.R
import com.kilabid.workoutapp.databinding.ActivitySquatBinding
import com.kilabid.workoutapp.helper.PoseLandmarkersHelper
import com.kilabid.workoutapp.helper.SquatPoseDetector
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SquatActivity : AppCompatActivity(), PoseLandmarkersHelper.LandmarkerListener{
    private lateinit var binding: ActivitySquatBinding
    private lateinit var poseDetector: SquatPoseDetector
    private val cameraViewModel: CameraViewModel by viewModels()
    private lateinit var counterTextView: TextView
    private lateinit var poseLandmarkersHelper: PoseLandmarkersHelper
    private var cameraFacing = CameraSelector.LENS_FACING_BACK
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var backgroundExecutor: ExecutorService
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var preview: Preview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySquatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        counterTextView = binding.tvSquatDetect
        poseDetector = SquatPoseDetector()

        backgroundExecutor = Executors.newSingleThreadExecutor()

        backgroundExecutor.execute {
            poseLandmarkersHelper = PoseLandmarkersHelper(
                context = this,
                runningMode = RunningMode.LIVE_STREAM,
                minPoseDetectionConfidence = 0.5F,
                minPoseTrackingConfidence = 0.5F,
                minPosePresenceConfidence = 0.5F,
                currentDelegate = 0,
                poseLandmarkerHelperListener = this,
                exerciseType = PoseLandmarkersHelper.ExerciseType.SQUAT
            )
        }

        binding.viewFinder.post {
            setUpCamera()
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(this)
        )
    }
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraFacing).build()

        preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also {
                it.setAnalyzer(backgroundExecutor) { image ->
                    detectPose(image)
                }
            }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e("PushUpActivity", "Use case binding failed", exc)
        }
    }

    private fun detectPose(imageProxy: ImageProxy) {
        if (this::poseLandmarkersHelper.isInitialized) {
            poseLandmarkersHelper.detectLiveStream(
                imageProxy = imageProxy,
                isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        poseLandmarkersHelper.close()
        binding.overlay.clear()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted(REQUIRED_PERMISSIONS)) {
                cameraViewModel.startCamera(this, this, binding.viewFinder)
                initializePoseLandmarkerHelper()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }
    }

    private fun Activity.allPermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
    }

    private fun initializePoseLandmarkerHelper() {
        poseLandmarkersHelper = PoseLandmarkersHelper(
            context = this,
            runningMode = RunningMode.LIVE_STREAM,
            poseLandmarkerHelperListener = this,
            minPosePresenceConfidence = 0.5f,
            minPoseTrackingConfidence = 0.5f,
            currentDelegate = 0,
            exerciseType = PoseLandmarkersHelper.ExerciseType.SQUAT,
            minPoseDetectionConfidence = 0.5f
        )
    }

    override fun onError(error: String, errorCode: Int) {
        Log.e("SquatActivity", error)
    }

    override fun onResults(resultBundle: PoseLandmarkersHelper.ResultBundle) {
        runOnUiThread {
            binding.overlay.setResults(
                resultBundle.results.first(),
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth,
                RunningMode.LIVE_STREAM
            )

            val results = resultBundle.results.firstOrNull()
            val landmarks = results?.landmarks()?.firstOrNull()
            if (!landmarks.isNullOrEmpty()) {
                val position = poseDetector.detectSquatPosition(landmarks)
                if (position == PoseLandmarkersHelper.SquatPosition.SQUAT_DOWN) {
                    val count = poseDetector.getCounter()
                    counterTextView.text = getString(R.string.squat_count, count.toString())
                }
            }
            binding.overlay.invalidate()
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}