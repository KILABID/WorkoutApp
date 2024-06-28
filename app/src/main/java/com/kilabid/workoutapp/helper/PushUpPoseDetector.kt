package com.kilabid.workoutapp.helper

import android.os.SystemClock
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.kilabid.workoutapp.util.calculateAngle

class PushUpPoseDetector {
    private var counter = 0
    private var previousPosition: PoseLandmarkersHelper.PushUpPosition = PoseLandmarkersHelper.PushUpPosition.NONE
    private var lastDetectionTime: Long = 0
    private val debounceDuration: Long = 500 // 500 ms atau 0.5 detik

    fun detectPushUpPosition(landmarks: MutableList<NormalizedLandmark>): PoseLandmarkersHelper.PushUpPosition {
        val currentTime = SystemClock.uptimeMillis()

        val leftHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_HIP]
        val rightHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_HIP]
        val leftShoulder = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_SHOULDER]
        val rightShoulder = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_SHOULDER]
        val leftElbow = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_ELBOW]
        val rightElbow = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_ELBOW]
        val leftWrist = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_WRIST]
        val rightWrist = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_WRIST]
        val leftKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_KNEE]
        val rightKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_KNEE]

        val leftHipAngle = calculateAngle(leftShoulder, leftHip, leftKnee)
        val rightHipAngle = calculateAngle(rightShoulder, rightHip, rightKnee)
        val leftElbowAngle = calculateAngle(leftShoulder, leftElbow, leftWrist)
        val rightElbowAngle = calculateAngle(rightShoulder, rightElbow, rightWrist)

        val isUp = (leftHipAngle > 170 && rightHipAngle > 170) &&
                (leftElbowAngle > 150 && rightElbowAngle > 150)

        val isDown = (leftHipAngle > 170 && rightHipAngle > 170) &&
                (leftElbowAngle < 70 && rightElbowAngle < 70)

        val currentPosition = when {
            isUp -> PoseLandmarkersHelper.PushUpPosition.PUSH_UP_UP
            isDown -> PoseLandmarkersHelper.PushUpPosition.PUSH_UP_DOWN
            else -> PoseLandmarkersHelper.PushUpPosition.NONE
        }

        if (currentPosition != PoseLandmarkersHelper.PushUpPosition.NONE && currentTime - lastDetectionTime > debounceDuration) {
            // Increment counter when transitioning from UP to DOWN
            if (previousPosition == PoseLandmarkersHelper.PushUpPosition.PUSH_UP_UP &&
                currentPosition == PoseLandmarkersHelper.PushUpPosition.PUSH_UP_DOWN) {
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
