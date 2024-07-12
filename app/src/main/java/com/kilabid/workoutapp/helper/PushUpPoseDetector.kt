package com.kilabid.workoutapp.helper

import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.kilabid.workoutapp.util.calculateAngle

class PushUpPoseDetector {
    private var counter = 0
    private var previousPosition: PoseLandmarkersHelper.PushUpPosition = PoseLandmarkersHelper.PushUpPosition.WRONG_POSITION
    private var lastDetectionTime: Long = 0
    private val debounceDuration: Long = 500

    fun detectPushUpPosition(landmarks: MutableList<NormalizedLandmark>, reps: Int?): PoseLandmarkersHelper.PushUpPosition {
        val currentTime = SystemClock.uptimeMillis()

        if (reps != null && counter == 0) {
            counter = reps
        }

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

        // Determine facing direction
        val isFacingLeft = leftShoulder.x() > rightShoulder.x()

        // Calculate angles based on facing direction
        val hipAngle = if (isFacingLeft) {
            calculateAngle(rightShoulder, rightHip, rightKnee)
        } else {
            calculateAngle(leftShoulder, leftHip, leftKnee)
        }

        val elbowAngle = if (isFacingLeft) {
            calculateAngle(rightShoulder, rightElbow, rightWrist)
        } else {
            calculateAngle(leftShoulder, leftElbow, leftWrist)
        }

        val isUp = hipAngle in 170.0 .. 180.0 && elbowAngle in 150.0 .. 180.0
        val isDown = hipAngle in 170.0 .. 180.0 && elbowAngle in 30.0 .. 70.0

        val currentPosition = when {
            isUp -> PoseLandmarkersHelper.PushUpPosition.PUSH_UP_UP
            isDown -> PoseLandmarkersHelper.PushUpPosition.PUSH_UP_DOWN
            else -> PoseLandmarkersHelper.PushUpPosition.WRONG_POSITION
        }

        if (currentPosition != PoseLandmarkersHelper.PushUpPosition.WRONG_POSITION && currentTime - lastDetectionTime > debounceDuration) {
            // Decrement counter when transitioning from UP to DOWN
            if (previousPosition == PoseLandmarkersHelper.PushUpPosition.PUSH_UP_UP &&
                currentPosition == PoseLandmarkersHelper.PushUpPosition.PUSH_UP_DOWN) {
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
