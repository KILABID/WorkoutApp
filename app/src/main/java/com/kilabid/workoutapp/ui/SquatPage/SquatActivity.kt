package com.kilabid.workoutapp.ui.SquatPage

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.kilabid.workoutapp.R
import com.kilabid.workoutapp.databinding.ActivitySquatBinding
import com.kilabid.workoutapp.helper.PoseLandmarkersHelper
import com.kilabid.workoutapp.helper.SquatPoseDetector
import com.kilabid.workoutapp.util.CameraUtil
import com.kilabid.workoutapp.util.PoseUtil
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SquatActivity : AppCompatActivity(), PoseLandmarkersHelper.LandmarkerListener {
    private lateinit var binding: ActivitySquatBinding
    private lateinit var poseDetector: SquatPoseDetector
    private lateinit var counterTextView: TextView
    private lateinit var poseLandmarkersHelper: PoseLandmarkersHelper
    private var cameraFacing = CameraSelector.LENS_FACING_BACK
    private lateinit var backgroundExecutor: ExecutorService
    private var squatCounter: Int = 0
    private var incorrectPositionToast: Toast? = null
    private var incorrectPositionStartTime: Long = 0
    private val incorrectPositionDuration = 5000 // 5 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySquatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        counterTextView = binding.tvSquatDetect
        poseDetector = SquatPoseDetector()
        backgroundExecutor = Executors.newSingleThreadExecutor()

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.btnStart.setOnClickListener {
            counterTextView.text = getString(R.string.squat_count, squatCounter.toString())
        }
        checkAndRequestPermissions()
        showPopUp()
    }

    private fun setUpCamera() {
        val cameraSelector = CameraSelector.Builder().requireLensFacing(cameraFacing).build()

        val preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.viewFinder.display.rotation).build()

        val imageAnalyzer = ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).build().also {
                it.setAnalyzer(backgroundExecutor) { image ->
                    PoseUtil.detectPose(
                        poseLandmarkersHelper,
                        image,
                        cameraFacing == CameraSelector.LENS_FACING_FRONT
                    )
                }
            }

        CameraUtil.setUpCamera(this, cameraSelector, preview, imageAnalyzer, backgroundExecutor) {
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        }
    }

    private fun initializePoseLandmarkerHelper() {
        backgroundExecutor.execute {
            poseLandmarkersHelper = PoseUtil.initializePoseLandmarkerHelper(
                this, this, PoseLandmarkersHelper.ExerciseType.SQUAT
            )
        }
    }

    private fun checkAndRequestPermissions() {
        if (!allPermissionsGranted(REQUIRED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        } else {
            initializePoseLandmarkerHelper()
            binding.viewFinder.post { setUpCamera() }
        }
    }

    private fun allPermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted(REQUIRED_PERMISSIONS)) {
                initializePoseLandmarkerHelper()
                binding.viewFinder.post { setUpCamera() }
            } else {
                Log.e("SquatActivity", "Permissions not granted by the user.")
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        poseLandmarkersHelper.close()
        binding.overlay.clear()
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
            val position = poseDetector.detectSquatPosition(landmarks, squatCounter)
            if (position == PoseLandmarkersHelper.SquatPosition.SQUAT_DOWN) {
                val count = poseDetector.getCounter()
                counterTextView.text = getString(R.string.squat_count, count.toString())
                incorrectPositionStartTime = 0 // Reset incorrect position start time
                if (count == 0) {
                    successPopUp()
                }
            } else if (position == PoseLandmarkersHelper.SquatPosition.WRONG_POSITION) {
                if (incorrectPositionStartTime == 0L) {
                    incorrectPositionStartTime = System.currentTimeMillis()
                } else if (System.currentTimeMillis() - incorrectPositionStartTime >= incorrectPositionDuration) {
                    showIncorrectPositionNotification()
                    incorrectPositionStartTime = 0 // Reset incorrect position start time after showing notification
                }
            } else {
                incorrectPositionStartTime = 0 // Reset incorrect position start time if in correct position
            }
        }
        binding.overlay.invalidate()
    }
}

    private fun showIncorrectPositionNotification() {
        incorrectPositionToast?.cancel() // Batalkan toast sebelumnya jika masih ditampilkan
        incorrectPositionToast = Toast.makeText(this, "Posisi salah, perbaiki form Anda!", Toast.LENGTH_LONG)
        incorrectPositionToast?.show()

        // Gunakan Handler untuk menjadwalkan pembatalan toast setelah 5 detik
        Handler(Looper.getMainLooper()).postDelayed({
            incorrectPositionToast?.cancel()
        }, 5000)
    }

    private fun showPopUp() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.pop_up_select)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnCancel = dialog.findViewById<Button>(R.id.cancel)
        val btnLanjut = dialog.findViewById<Button>(R.id.next)
        val dropdownLayout =
            dialog.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.dropdown_skill_layout)
        val dropdown = dropdownLayout.findViewById<AutoCompleteTextView>(R.id.dropdown_skill)

        val skillLevels = resources.getStringArray(R.array.menu_kemampuan)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, skillLevels)
        dropdown.setAdapter(adapter)

        dropdown.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> squatCounter = 5 // Beginner
                1 -> squatCounter = 10 // Intermediate
                2 -> squatCounter = 15 // Advanced
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        btnLanjut.setOnClickListener {
            dialog.dismiss()
            counterTextView.text = getString(R.string.squat_count, squatCounter.toString())
        }

        dialog.show()
    }

    private fun successPopUp() {
        val successDialog = Dialog(this)
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        successDialog.setCancelable(false)
        successDialog.setContentView(R.layout.pop_up_success)
        successDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val animationView: LottieAnimationView = successDialog.findViewById(R.id.lottie_success)
        animationView.setAnimation(R.raw.success)
        animationView.playAnimation()

        val btnKembali = successDialog.findViewById<Button>(R.id.btn_kembali)
        btnKembali.setOnClickListener {
            successDialog.dismiss()
            finish()
        }

        successDialog.show()
    }


    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
