package com.kilabid.workoutapp.helper

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import java.lang.Math.toDegrees
import kotlin.math.atan2

class SquatPoseDetector {
    fun detectSquatPosition(landmarks: MutableList<NormalizedLandmark>): PoseLandmarkersHelper.ExercisePosition {
        val leftHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_HIP]
        val rightHip = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_HIP]
        val leftKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_KNEE]
        val rightKnee = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_KNEE]
        val leftAnkle = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_ANKLE]
        val rightAnkle = landmarks[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_ANKLE]

        val leftLegAngle = calculateAngle(leftHip, leftKnee, leftAnkle)
        val rightLegAngle = calculateAngle(rightHip, rightKnee, rightAnkle)

        val isSquatUp = leftLegAngle > 160 && rightLegAngle > 160
        val isSquatDown = leftLegAngle < 90 && rightLegAngle < 90

        return when {
            isSquatUp -> PoseLandmarkersHelper.ExercisePosition.SQUAT_UP
            isSquatDown -> PoseLandmarkersHelper.ExercisePosition.SQUAT_DOWN
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
