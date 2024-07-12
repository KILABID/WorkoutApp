package com.kilabid.workoutapp.helper

import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.kilabid.workoutapp.util.calculateAngle

class SitUpPoseDetector {
    private var counter = 0
    private var previousPosition: PoseLandmarkersHelper.SitUpPosition = PoseLandmarkersHelper.SitUpPosition.WRONG_POSITION
    private var lastDetectionTime: Long = 0
    private val debounceDuration: Long = 500 // 500 ms or 0.5 seconds

    fun detectSitUpPosition(landmarks: MutableList<NormalizedLandmark>, reps: Int?): PoseLandmarkersHelper.SitUpPosition {
        val leftShoulder = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_SHOULDER]
        val rightShoulder = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_SHOULDER]
        val leftHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_HIP]
        val rightHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_HIP]
        val leftKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_KNEE]
        val rightKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_KNEE]
        val rightAnkle = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_ANKLE]
        val leftAnkle = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_ANKLE]
        val currentTime = SystemClock.uptimeMillis()

        if (reps != null && counter == 0) {
            counter = reps
        }

        // Determine facing direction
        val isFacingLeft = leftShoulder.x() > rightShoulder.x()

        // Calculate angle between left and right knee
        val kneeAngle = if (isFacingLeft) {
            calculateAngle(rightHip, rightKnee, rightAnkle)
        } else {
            calculateAngle(leftHip, leftKnee, leftAnkle)
        }

        // Calculate angles based on facing direction
        val bodyAngle = if (isFacingLeft) {
            calculateAngle(rightShoulder, rightHip, rightKnee)
        } else {
            calculateAngle(leftShoulder, leftHip, leftKnee)
        }

        val currentPosition = when {
            bodyAngle in 120.0..180.0 && kneeAngle in 45.0..110.0 -> PoseLandmarkersHelper.SitUpPosition.SIT_UP_DOWN
            bodyAngle in 30.0.. 90.0 && kneeAngle in 45.0..110.0 -> PoseLandmarkersHelper.SitUpPosition.SIT_UP_UP
            else -> PoseLandmarkersHelper.SitUpPosition.WRONG_POSITION
        }

        if (currentPosition != PoseLandmarkersHelper.SitUpPosition.WRONG_POSITION && currentTime - lastDetectionTime > debounceDuration) {
            // Decrement counter when transitioning from DOWN to UP
            if (previousPosition == PoseLandmarkersHelper.SitUpPosition.SIT_UP_DOWN &&
                currentPosition == PoseLandmarkersHelper.SitUpPosition.SIT_UP_UP) {
                if (counter > 0) {
                    counter--
                    Log.d("SitUpPoseDetector", "Counter decremented: $counter")
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
