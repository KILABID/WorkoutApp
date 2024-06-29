package com.kilabid.workoutapp.helper

import android.os.SystemClock
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.kilabid.workoutapp.util.calculateAngle

class SquatPoseDetector {
    private var counter = 0
    private var previousPosition: PoseLandmarkersHelper.SquatPosition = PoseLandmarkersHelper.SquatPosition.NONE
    private var lastDetectionTime: Long = 0
    private val debounceDuration: Long = 500
    fun detectSquatPosition(landmarks: MutableList<NormalizedLandmark>): PoseLandmarkersHelper.SquatPosition {
        val currentTime = SystemClock.uptimeMillis()

        val leftHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_HIP]
        val rightHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_HIP]
        val leftKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_KNEE]
        val rightKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_KNEE]
        val leftAnkle = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_ANKLE]
        val rightAnkle = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_ANKLE]

        val leftLegAngle = calculateAngle(leftHip, leftKnee, leftAnkle)
        val rightLegAngle = calculateAngle(rightHip, rightKnee, rightAnkle)

        val isUp = leftLegAngle > 160 && rightLegAngle > 160
        val isDown = leftLegAngle < 90 && rightLegAngle < 90

        val currentPosition = when {
            isUp -> PoseLandmarkersHelper.SquatPosition.SQUAT_UP
            isDown -> PoseLandmarkersHelper.SquatPosition.SQUAT_DOWN
            else -> PoseLandmarkersHelper.SquatPosition.NONE
        }
        if (currentPosition != PoseLandmarkersHelper.SquatPosition.NONE && currentTime - lastDetectionTime > debounceDuration) {
            // Increment counter when transitioning from UP to DOWN
            if (previousPosition == PoseLandmarkersHelper.SquatPosition.SQUAT_UP &&
                currentPosition == PoseLandmarkersHelper.SquatPosition.SQUAT_DOWN) {
                counter++
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
