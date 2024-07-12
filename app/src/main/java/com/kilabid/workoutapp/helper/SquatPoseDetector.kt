package com.kilabid.workoutapp.helper

import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.kilabid.workoutapp.util.calculateAngle

class SquatPoseDetector {
    private var counter = 0
    private var previousPosition: PoseLandmarkersHelper.SquatPosition = PoseLandmarkersHelper.SquatPosition.WRONG_POSITION
    private var lastDetectionTime: Long = 0
    private val debounceDuration: Long = 500 // 500 ms or 0.5 seconds

    fun detectSquatPosition(landmarks: MutableList<NormalizedLandmark>, reps: Int?): PoseLandmarkersHelper.SquatPosition {
        val currentTime = SystemClock.uptimeMillis()

        if (reps != null && counter == 0) {
            counter = reps
        }

        val leftHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_HIP]
        val rightHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_HIP]
        val leftKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_KNEE]
        val rightKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_KNEE]
        val leftAnkle = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_ANKLE]
        val rightAnkle = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_ANKLE]

        // Determine facing direction
        val isFacingLeft = leftHip.x() > rightHip.x()

        // Calculate leg angles based on facing direction
        val legAngle = if (isFacingLeft) {
            calculateAngle(rightHip, rightKnee, rightAnkle)
        } else {
            calculateAngle(leftHip, leftKnee, leftAnkle)
        }

        val currentPosition = when {
            legAngle in 160.0..180.0 -> PoseLandmarkersHelper.SquatPosition.SQUAT_UP
            legAngle < 90.0 -> PoseLandmarkersHelper.SquatPosition.SQUAT_DOWN
            else -> PoseLandmarkersHelper.SquatPosition.WRONG_POSITION
        }

        if (currentPosition != PoseLandmarkersHelper.SquatPosition.WRONG_POSITION && currentTime - lastDetectionTime > debounceDuration) {
            // Decrement counter when transitioning from UP to DOWN
            if (previousPosition == PoseLandmarkersHelper.SquatPosition.SQUAT_UP &&
                currentPosition == PoseLandmarkersHelper.SquatPosition.SQUAT_DOWN) {
                if (counter > 0) {
                    counter--
                    Log.d("SquatPoseDetector", "Counter decremented: $counter")
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
