package com.zhifou.fortune

import java.io.File
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import org.junit.Assert.*
import org.junit.Test

class CopperCoinArtworkTest {
    // Android's compile stubs omit java.desktop; the host JVM still supplies its PNG decoder.
    private class Pixels(file: File) {
        private val image = Class.forName("javax.imageio.ImageIO").getMethod("read", File::class.java).invoke(null, file)
        private val type = image.javaClass
        private val rgb = type.getMethod("getRGB", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
        val width = type.getMethod("getWidth").invoke(image) as Int
        val height = type.getMethod("getHeight").invoke(image) as Int
        fun getRGB(x: Int, y: Int) = rgb.invoke(image, x, y) as Int
    }

    private fun art(name: String): File {
        val path = "src/main/res/drawable-nodpi/oracle_coin_$name.png"
        return listOf(File(path), File("app/$path")).first { it.isFile }
    }

    @Test fun bothOriginalFacesHaveTransparentSquareHolesAndBoundedDecodeSize() {
        var bytes = 0L
        for (name in listOf("yang", "yin")) {
            val file = art(name)
            bytes += file.length()
            val bitmap = Pixels(file)
            assertEquals(512, bitmap.width)
            assertEquals(512, bitmap.height)
            assertEquals(0, bitmap.getRGB(0, 0) ushr 24)
            for (x in 240..272 step 8) for (y in 240..272 step 8) {
                assertEquals("The central opening must not be an opaque decal", 0, bitmap.getRGB(x, y) ushr 24)
            }
            assertEquals(255, bitmap.getRGB(256, 80) ushr 24)
        }
        assertTrue("Two lossless model renders must stay below 768 KiB", bytes < 768 * 1024)
    }

    @Test fun sunAndMoonFieldsRemainClearlyDifferentWithoutRelyingOnColorAlone() {
        val sun = Pixels(art("yang"))
        val moon = Pixels(art("yin"))
        fun luminance(rgb: Int) = ((rgb shr 16 and 255) * .2126 + (rgb shr 8 and 255) * .7152 + (rgb and 255) * .0722) / 255
        var difference = 0.0
        var count = 0
        for (x in 180..332 step 4) for (y in 96..176 step 4) {
            difference += luminance(sun.getRGB(x, y)) - luminance(moon.getRGB(x, y))
            count++
        }
        assertTrue("The sun face must stay visibly lighter even in grayscale", difference / count > .15)
        val differences = (0 until 512 step 4).sumOf { x -> (0 until 512 step 4).count { y ->
            abs(luminance(sun.getRGB(x, y)) - luminance(moon.getRGB(x, y))) > .15
        } }
        assertTrue("The two motifs must differ over a substantial area", differences > 2000)
    }

    @Test fun oppositeFaceIsUprightAndNeverMirroredAtItsFinalPose() {
        val points = FloatArray(8)
        repeat(3) { coin -> (2..3).forEach { value ->
            val pose = OracleMotion.coinPose(1_100f, coin, value)
            val angle = pose.rotationY.toDouble() * PI / 180
            CopperCoinProjection.writeFace(pose.frontIsYang, sin(angle).toFloat(), cos(angle).toFloat(), 100f, points)
            assertTrue(points[0] < points[2])
            assertTrue(points[6] < points[4])
            assertTrue(points[1] < points[7])
            assertTrue(points[3] < points[5])
            assertEquals(value == 3, pose.frontIsYang)
        } }
    }

    @Test fun projectedCoinStaysFiniteAndInsideTheDrawingAtEveryAngle() {
        val points = FloatArray(2)
        for (diameter in listOf(46f, 92f, 184f)) for (degrees in 0..360) {
            val angle = degrees * PI / 180
            val sine = sin(angle).toFloat()
            val cosine = cos(angle).toFloat()
            for (edge in 0 until 64) {
                val theta = edge * 2.0 * PI / 64
                for (side in listOf(-1, 1)) {
                    CopperCoinProjection.writePoint(cos(theta).toFloat(), sin(theta).toFloat(),
                        side * CopperCoinProjection.halfThickness, sine, cosine, diameter, points, 0)
                    assertTrue(points.all { it.isFinite() && it >= -1f && it <= diameter + 1f })
                }
            }
        }
    }

    @Test fun anEdgeOnCoinRetainsPhysicalThicknessWhenTheFaceDisappears() {
        val points = FloatArray(4)
        CopperCoinProjection.writePoint(0f, 0f, CopperCoinProjection.halfThickness, 1f, 0f, 100f, points, 0)
        CopperCoinProjection.writePoint(0f, 0f, -CopperCoinProjection.halfThickness, 1f, 0f, 100f, points, 2)
        assertTrue(points[0] - points[2] > 6f)
        assertEquals(points[1], points[3], .001f)
    }

    @Test fun elevatedCoinHasASofterWiderShadowWithoutADetachedContactPatch() {
        val resting = OracleMotion.coinPose(1_100f, 0, 3)
        val contact = copperCoinShadow(resting, false)
        val airborne = copperCoinShadow(resting.copy(liftDp = 50f), false)
        assertTrue(airborne.radiusX > contact.radiusX)
        assertTrue(airborne.radiusY > contact.radiusY)
        assertTrue(airborne.opacity < contact.opacity)
        assertTrue(airborne.contactOpacity < .002f)
        assertTrue(contact.contactOpacity > .20f)
        assertTrue(airborne.offsetX > contact.offsetX)
    }

    @Test fun shadowTracksHorizontalMotionAndChangesFootprintWhileTurning() {
        val pose = OracleMotion.coinPose(1_100f, 0, 3)
        val full = copperCoinShadow(pose, false)
        val edge = copperCoinShadow(pose.copy(rotationY = 90f), false)
        val moved = copperCoinShadow(pose.copy(driftDp = -12f), false)
        assertTrue(edge.radiusX < full.radiusX)
        assertEquals(-12f, moved.offsetX - full.offsetX, .001f)
        assertEquals(-12f, moved.contactX - full.contactX, .001f)
    }

    @Test fun reducedMotionKeepsTheShadowGroundedAndIndependentOfFlightPose() {
        val pose = OracleMotion.coinPose(250f, 0, 3)
        val reduced = copperCoinShadow(pose, true)
        val resting = copperCoinShadow(OracleMotion.coinPose(1_100f, 0, 3), true)
        assertEquals(resting, reduced)
    }
}
