package com.zhifou.fortune

/** Matches the normalized Blender model and its 2.08-unit orthographic artwork. */
internal object CopperCoinProjection {
    const val artworkSpan = 2.08f
    const val halfThickness = .065f
    const val holeHalf = .19f
    private const val cameraDistance = artworkSpan * 8f

    fun writePoint(
        x: Float, y: Float, z: Float, sine: Float, cosine: Float,
        diameter: Float, destination: FloatArray, index: Int,
    ) {
        val rotatedX = cosine * x + sine * z
        val rotatedZ = -sine * x + cosine * z
        val scale = diameter / artworkSpan * cameraDistance / (cameraDistance - rotatedZ)
        destination[index] = diameter / 2f + rotatedX * scale
        destination[index + 1] = diameter / 2f + y * scale
    }

    fun writeFace(
        yang: Boolean, sine: Float, cosine: Float, diameter: Float, destination: FloatArray,
    ) {
        val side = if (yang) 1f else -1f
        val half = artworkSpan / 2f
        val z = side * halfThickness
        writePoint(-side * half, -half, z, sine, cosine, diameter, destination, 0)
        writePoint(side * half, -half, z, sine, cosine, diameter, destination, 2)
        writePoint(side * half, half, z, sine, cosine, diameter, destination, 4)
        writePoint(-side * half, half, z, sine, cosine, diameter, destination, 6)
    }
}
