package com.kilabid.workoutapp.helper

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import java.lang.Math.toDegrees
import kotlin.math.atan2

class PushUpPoseDetector {
    fun detectPushUpPosition(landmarks: MutableList<NormalizedLandmark>): PoseLandmarkersHelper.ExercisePosition {
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

        val isPushUpUp = (leftHipAngle > 170 && rightHipAngle > 170) &&
                (leftElbowAngle > 150 && rightElbowAngle > 150)

        val isPushUpDown = (leftHipAngle > 170 && rightHipAngle > 170) &&
                (leftElbowAngle < 70 && rightElbowAngle < 70)

        return when {
            isPushUpUp -> PoseLandmarkersHelper.ExercisePosition.PUSH_UP_UP
            isPushUpDown -> PoseLandmarkersHelper.ExercisePosition.PUSH_UP_DOWN
            else -> PoseLandmarkersHelper.ExercisePosition.NONE
        }
    }

    private fun calculateAngle(
        firstPoint: NormalizedLandmark,
        midPoint: NormalizedLandmark,
        lastPoint: NormalizedLandmark
    ): Double {
        val result = toDegrees(
            atan2((lastPoint.y() - midPoint.y()).toDouble(), (lastPoint.x() - midPoint.x()).toDouble())
                    - atan2((firstPoint.y() - midPoint.y()).toDouble(), (firstPoint.x() - midPoint.x()).toDouble())
        )
        var angle = Math.abs(result)
        if (angle > 180) {
            angle = 360.0 - angle
        }
        return angle
    }
}
