package com.kilabid.workoutapp.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.kilabid.workoutapp.R
import com.kilabid.workoutapp.helper.PoseLandmarkersHelper
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: PoseLandmarkerResult? = null
    private var pointPaint = Paint()
    private var linePaint = Paint()
    private var textPaint = TextPaint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    init {
        initPaints()
    }

    fun clear() {
        results = null
        pointPaint.reset()
        linePaint.reset()
        textPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        linePaint.color = ContextCompat.getColor(context!!, R.color.mp_color_primary)
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL

        textPaint.color = Color.WHITE
        textPaint.textSize = TEXT_SIZE
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let { poseLandmarkerResult ->
            for (landmark in poseLandmarkerResult.landmarks()) {
                for (normalizedLandmark in landmark) {
                    canvas.drawPoint(
                        normalizedLandmark.x() * imageWidth * scaleFactor,
                        normalizedLandmark.y() * imageHeight * scaleFactor,
                        pointPaint
                    )
                }

                // Draw lines between connected landmarks
                PoseLandmarker.POSE_LANDMARKS.forEach { connection ->
                    val startLandmark = landmark[connection.start()]
                    val endLandmark = landmark[connection.end()]
                    val startX = startLandmark.x() * imageWidth * scaleFactor
                    val startY = startLandmark.y() * imageHeight * scaleFactor
                    val endX = endLandmark.x() * imageWidth * scaleFactor
                    val endY = endLandmark.y() * imageHeight * scaleFactor

                    canvas.drawLine(startX, startY, endX, endY, linePaint)
                }

                // Display angles at key joints (example for left elbow and left knee)
                val leftShoulder = landmark[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_SHOULDER]
                val leftElbow = landmark[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_ELBOW]
                val leftWrist = landmark[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_WRIST]
                val leftHip = landmark[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_HIP]
                val leftKnee = landmark[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_KNEE]
                val leftAnkle = landmark[PoseLandmarkersHelper.POSE_LANDMARK_LEFT_ANKLE]

                val rightShouler = landmark[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_SHOULDER]
                val rightElbow = landmark[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_ELBOW]
                val rightWrist = landmark[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_WRIST]
                val rightHip = landmark[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_HIP]
                val rightKnee = landmark[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_KNEE]
                val rightAnkle = landmark[PoseLandmarkersHelper.POSE_LANDMARK_RIGHT_ANKLE]


                displayAngle(canvas, leftShoulder, leftElbow, leftWrist)
                displayAngle(canvas, leftHip, leftKnee, leftAnkle)
                displayAngle(canvas, leftKnee, leftHip, leftShoulder)

                displayAngle(canvas, rightShouler, rightElbow, rightWrist)
                displayAngle(canvas, rightHip, rightKnee, rightAnkle)
                displayAngle(canvas, rightKnee, rightHip, rightShouler)
            }
        }
    }

    private fun displayAngle(
        canvas: Canvas,
        startLandmark: NormalizedLandmark,
        midLandmark: NormalizedLandmark,
        endLandmark: NormalizedLandmark,
    ) {
        val startX = startLandmark.x() * imageWidth * scaleFactor
        val startY = startLandmark.y() * imageHeight * scaleFactor
        val midX = midLandmark.x() * imageWidth * scaleFactor
        val midY = midLandmark.y() * imageHeight * scaleFactor
        val endX = endLandmark.x() * imageWidth * scaleFactor
        val endY = endLandmark.y() * imageHeight * scaleFactor

        val angle = calculateAngle(
            floatArrayOf(startX, startY),
            floatArrayOf(midX, midY),
            floatArrayOf(endX, endY)
        )

        canvas.drawText(
            String.format("%.1f°", angle),
            midX,
            midY - TEXT_SIZE / 2,
            textPaint
        )
    }

    fun setResults(
        poseLandmarkerResults: PoseLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE,
    ) {
        results = poseLandmarkerResults

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO,
            -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }

            RunningMode.LIVE_STREAM -> {
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }

    // Function to calculate the angle between two vectors
    // Fungsi untuk menghitung sudut antara tiga titik
    private fun calculateAngle(
        startPoint: FloatArray,
        midPoint: FloatArray,
        endPoint: FloatArray,
    ): Float {
        val angle = Math.toDegrees(
            atan2(
                endPoint[1] - midPoint[1],
                endPoint[0] - midPoint[0]
            ).toDouble() - atan2(
                startPoint[1] - midPoint[1],
                startPoint[0] - midPoint[0]
            ).toDouble()
        ).toFloat()
        return if (angle < 0) angle + 360 else angle
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 12F
        private const val TEXT_SIZE = 24F
    }
}
