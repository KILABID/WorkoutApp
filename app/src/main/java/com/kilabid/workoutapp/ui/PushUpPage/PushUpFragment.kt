package com.kilabid.workoutapp.ui.PushUpPage


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kilabid.workoutapp.databinding.FragmentPushUpBinding
import com.kilabid.workoutapp.helper.PoseLandmarkersHelper
import com.kilabid.workoutapp.ui.CameraViewModel

class PushUpFragment : Fragment(), PoseLandmarkersHelper.LandmarkerListener {
    private var _binding: FragmentPushUpBinding? = null
    private val binding get() = _binding!!
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private lateinit var poseLandmarkerHelper: PoseLandmarkersHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPushUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize PoseLandmarkersHelper
        poseLandmarkerHelper = PoseLandmarkersHelper(
            context = requireContext(),
            poseLandmarkerHelperListener = this
        )


        if (requireActivity().checkSelfPermission(android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            setUpCamera()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
        }
    }

    private fun setUpCamera() {
        cameraViewModel.setUpCamera(requireContext(), this, CameraSelector.LENS_FACING_FRONT) { imageProxy ->
            processImageProxy(imageProxy)
        }
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        poseLandmarkerHelper.detectLiveStream(imageProxy, isFrontCamera = true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Implement methods from PoseLandmarkersHelper.LandmarkerListener
    override fun onError(error: String, errorCode: Int) {
        // Handle the error
    }

    override fun onResults(resultBundle: PoseLandmarkersHelper.ResultBundle) {
        // Handle the results
    }

    companion object {
        private const val REQUEST_CODE_CAMERA = 101
    }
}