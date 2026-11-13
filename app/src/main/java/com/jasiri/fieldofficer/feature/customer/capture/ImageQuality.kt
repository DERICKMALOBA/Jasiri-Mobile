package com.jasiri.fieldofficer.feature.customer.capture

import android.graphics.Bitmap

/**
 * Cheap on-device blur/glare check so a bad ID photo never reaches the retake/continue
 * decision (per docs/Field-Officer-App-Screen-Designs.pdf, Onboarding ID Capture). This is
 * a Laplacian-variance focus measure plus a mean-luminance glare check, not a full CV
 * pipeline — thresholds are heuristic starting points and may need tuning on real devices.
 */
data class ImageQualityResult(val isAcceptable: Boolean, val reason: String? = null)

object ImageQuality {
    private const val DOWNSCALE = 100
    private const val BLUR_VARIANCE_THRESHOLD = 90.0
    private const val GLARE_BRIGHTNESS_THRESHOLD = 245
    private const val GLARE_RATIO_THRESHOLD = 0.35

    fun evaluate(bitmap: Bitmap): ImageQualityResult {
        val scaled = Bitmap.createScaledBitmap(bitmap, DOWNSCALE, DOWNSCALE, true)
        val gray = IntArray(DOWNSCALE * DOWNSCALE)
        var brightPixels = 0
        for (y in 0 until DOWNSCALE) {
            for (x in 0 until DOWNSCALE) {
                val pixel = scaled.getPixel(x, y)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                val lum = (r * 0.299 + g * 0.587 + b * 0.114).toInt()
                gray[y * DOWNSCALE + x] = lum
                if (lum >= GLARE_BRIGHTNESS_THRESHOLD) brightPixels++
            }
        }
        if (brightPixels.toDouble() / gray.size > GLARE_RATIO_THRESHOLD) {
            return ImageQualityResult(false, "Glare detected — avoid direct light on the ID and try again.")
        }

        var sumSquared = 0.0
        var count = 0
        for (y in 1 until DOWNSCALE - 1) {
            for (x in 1 until DOWNSCALE - 1) {
                val center = gray[y * DOWNSCALE + x]
                val laplacian = 4 * center -
                    gray[(y - 1) * DOWNSCALE + x] -
                    gray[(y + 1) * DOWNSCALE + x] -
                    gray[y * DOWNSCALE + (x - 1)] -
                    gray[y * DOWNSCALE + (x + 1)]
                sumSquared += laplacian.toDouble() * laplacian
                count++
            }
        }
        val focusMeasure = sumSquared / count
        if (focusMeasure < BLUR_VARIANCE_THRESHOLD) {
            return ImageQualityResult(false, "Photo looks blurry — hold steady and try again.")
        }

        return ImageQualityResult(true)
    }
}
