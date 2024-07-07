package com.kilabid.workoutapp.helper

import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.kilabid.workoutapp.util.calculateAngle

class SitUpPoseDetector {
    private var counter = 0
    private var previousPosition: PoseLandmarkersHelper.SitUpPosition = PoseLandmarkersHelper.SitUpPosition.WRONG_POSITION
    private var lastDetectionTime: Long = 0
    private val debounceDuration: Long = 500 // 500 ms atau 0.5 detik
    fun detectSitUpPosition(landmarks: MutableList<NormalizedLandmark>, reps : Int?): PoseLandmarkersHelper.SitUpPosition {
        val leftShoulder = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_SHOULDER]
        val rightShoulder = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_SHOULDER]
        val leftHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_HIP]
        val rightHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_HIP]
        val leftKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_KNEE]
        val rightKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_KNEE]
        val currentTime = SystemClock.uptimeMillis()

        if (reps != null && counter == 0) {
            counter = reps
        }

        // Calculate angles for sit-up detection
        val leftBodyAngle = calculateAngle(leftShoulder, leftHip, leftKnee)
        val rightBodyAngle = calculateAngle(rightShoulder, rightHip, rightKnee)

        val currentPosition =  when {
            leftBodyAngle > 150 && rightBodyAngle > 150 -> PoseLandmarkersHelper.SitUpPosition.SIT_UP_DOWN
            leftBodyAngle < 90 && rightBodyAngle < 90 -> PoseLandmarkersHelper.SitUpPosition.SIT_UP_UP
            else -> PoseLandmarkersHelper.SitUpPosition.WRONG_POSITION
        }

        if (currentPosition != PoseLandmarkersHelper.SitUpPosition.WRONG_POSITION && currentTime - lastDetectionTime > debounceDuration) {
            // Decrement counter when transitioning from UP to DOWN
            if (previousPosition == PoseLandmarkersHelper.SitUpPosition.SIT_UP_DOWN &&
                currentPosition == PoseLandmarkersHelper.SitUpPosition.SIT_UP_UP) {
                if (counter > 0) {
                    counter--
                    Log.d("PushUpPoseDetector", "Counter decremented: $counter")
                }
            }

            // Update the previous position
            previousPosition = currentPosition
            // Update the last detection time
            lastDetectionTime = currentTime
        }

        return currentPosition
    }
    fun getCounter(): Int {
        return counter
    }
}
