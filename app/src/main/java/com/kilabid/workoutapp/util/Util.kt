package com.kilabid.workoutapp.util

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.atan2

fun calculateAngle(
    firstPoint: NormalizedLandmark,
    midPoint: NormalizedLandmark,
    lastPoint: NormalizedLandmark,
): Double {
    val result = Math.toDegrees(
        atan2(
            (lastPoint.y() - midPoint.y()).toDouble(),
            (lastPoint.x() - midPoint.x()).toDouble()
        )
                - atan2(
            (firstPoint.y() - midPoint.y()).toDouble(),
            (firstPoint.x() - midPoint.x()).toDouble()
        )
    )
    var angle = Math.abs(result)
    if (angle > 180) {
        angle = 360.0 - angle
    }
    return angle
}