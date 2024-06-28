package com.kilabid.workoutapp.helper

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import java.lang.Math.toDegrees
import kotlin.math.atan2

class SitUpPoseDetector {
    fun detectSitUpPosition(landmarks: MutableList<NormalizedLandmark>): PoseLandmarkersHelper.ExercisePosition {
        val leftShoulder = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_SHOULDER]
        val rightShoulder = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_SHOULDER]
        val leftHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_HIP]
        val rightHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_HIP]
        val leftKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_KNEE]
        val rightKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_KNEE]

        // Calculate angles for sit-up detection
        val leftBodyAngle = calculateAngle(leftShoulder, leftHip, leftKnee)
        val rightBodyAngle = calculateAngle(rightShoulder, rightHip, rightKnee)

        return when {
            leftBodyAngle > 170 && rightBodyAngle > 170 -> PoseLandmarkersHelper.ExercisePosition.SIT_UP_UP
            leftBodyAngle < 90 && rightBodyAngle < 90 -> PoseLandmarkersHelper.ExercisePosition.SIT_UP_DOWN
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
