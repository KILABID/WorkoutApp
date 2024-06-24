package com.kilabid.workoutapp.ui.PushUpPage

import PoseLandmarkersHelper
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.kilabid.workoutapp.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PoseEstimationActivity : AppCompatActivity(), PoseLandmarkersHelper.LandmarkerListener {

    private lateinit var previewView: PreviewView
    private lateinit var mediaPipeHelper: PoseLandmarkersHelper
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pose_estimation)

        previewView = findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()
        mediaPipeHelper = PoseLandmarkersHelper(
            context = this,
            poseLandmarkerHelperListener = this
        )

        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    mediaPipeHelper.detectLiveStream(imageProxy, isFrontCamera = true)
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, preview, imageAnalyzer)
            } catch (exc: Exception) {
                Log.e("CameraX", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPipeHelper.close()
        cameraExecutor.shutdown()
    }

    override fun onError(error: String, errorCode: Int) {
        Log.e("PoseEstimationActivity", "Error: $error")
    }

    override fun onResults(resultBundle: PoseLandmarkersHelper.ResultBundle) {
        // Handle pose estimation results here
        Log.d("PoseEstimationActivity", "Pose estimation results: ${resultBundle.results}")
    }
}
