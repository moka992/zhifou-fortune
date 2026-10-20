package com.zhifou.fortune

import kotlin.math.abs
import org.junit.Assert.*
import org.junit.Test

class DiceRigidBodySimulationTest {
    @Test fun allPolyhedraUseTheSameFacesForRenderingAndCollisions() {
        for (sides in listOf(4, 6, 8, 10, 12, 20)) {
            val mesh = buildPhysicsMesh(sides)
            val faces = buildPhysicsMeshFaces(mesh)
            assertEquals(sides, faces.size)
            assertTrue(faces.all { it.normal.length() > 0.999f })
            if (sides == 10) assertTrue(faces.all { it.vertices.size == 4 })
            if (sides == 12) assertTrue(faces.all { it.vertices.size == 5 })
        }
    }

    @Test fun allTypesAndCountsLandFlatAndSeparateWithMatchingResults() {
        var maxAttempts = 0
        val started = System.nanoTime()
        for (sides in listOf(4, 6, 8, 10, 12, 20)) {
            for (count in 1..6) {
                repeat(20) { sample ->
                    val results = List(count) { (it * 3 + sample) % sides + 1 }
                    val path = DiceRigidBodySimulation.generate(sides, results, sides * 10000L + count * 100 + sample)
                    maxAttempts = maxOf(maxAttempts, path.attempts)
                    assertTrue("D$sides x$count must rest flat and without overlap", DiceRigidBodySimulation.isValidRest(sides, path.frames.last().poses))
                    assertTrue(path.durationSeconds in 0.75f..7.1f)
                    val faces = buildPhysicsMeshFaces(buildPhysicsMesh(sides))
                    path.frames.last().poses.forEachIndexed { die, pose ->
                        assertEquals((1..sides).toList(), path.faceValues[die].sorted())
                        val face = DiceRigidBodySimulation.resultFace(faces, pose.rotation, sides)
                        assertEquals(results[die], path.faceValues[die][face])
                    }
                }
            }
        }
        println("720 physical rolls passed; max attempts=$maxAttempts, host computation ${(System.nanoTime() - started) / 1_000_000}ms")
    }

    @Test fun d6OppositeFacesAlwaysSumToSeven() {
        val faces = buildPhysicsMeshFaces(buildPhysicsMesh(6))
        faces.indices.forEach { top ->
            for (result in 1..6) {
                val values = DiceRigidBodySimulation.faceValues(faces, top, result, 6)
                faces.indices.forEach { face ->
                    val opposite = faces.indices.minBy { faces[it].normal.dot(faces[face].normal) }
                    assertEquals(7, values[face] + values[opposite])
                }
            }
        }
    }

    @Test fun everyStrengthPreservesFlatSeparateResultsForAllTypesAndCounts() {
        for (strength in DiceThrowStrength.entries) {
            for (sides in listOf(4, 6, 8, 10, 12, 20)) {
                for (count in 1..6) {
                    repeat(10) { sample ->
                        val results = List(count) { (it * 7 + sample) % sides + 1 }
                        val path = DiceRigidBodySimulation.generate(sides, results, sides * 30000L + count * 100 + sample, strength)
                        assertEquals(results, path.results)
                        assertTrue("$strength D$sides x$count", DiceRigidBodySimulation.isValidRest(sides, path.frames.last().poses))
                        assertTrue(path.durationSeconds in .75f..7.1f)
                        val faces = buildPhysicsMeshFaces(buildPhysicsMesh(sides))
                        path.frames.last().poses.forEachIndexed { die, pose ->
                            assertEquals((1..sides).toList(), path.faceValues[die].sorted())
                            assertEquals(results[die], path.faceValues[die][DiceRigidBodySimulation.resultFace(faces, pose.rotation, sides)])
                        }
                    }
                }
            }
        }
    }

    @Test fun increasingStrengthAddsRealTumblingAndTheDefaultIsMedium() {
        val travel = DiceThrowStrength.entries.map { strength ->
            val path = DiceRigidBodySimulation.generate(6, listOf(3), 1511L, strength)
            val a = path.frames[0].poses.single().rotation
            val b = path.frames[1].poses.single().rotation
            val dot = abs(a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z).coerceIn(0f, 1f)
            2f * kotlin.math.acos(dot)
        }
        assertTrue(travel[1] > travel[0] * 1.15f)
        assertTrue(travel[2] > travel[1] * 1.1f)
        val explicit = DiceRigidBodySimulation.generate(6, listOf(3), 1511L, DiceThrowStrength.Medium)
        val default = DiceRigidBodySimulation.generate(6, listOf(3), 1511L)
        assertEquals(explicit, default)
    }

    @Test fun motionHasContinuousPosesAndRealContactEvents() {
        val path = DiceRigidBodySimulation.generate(6, listOf(1, 2, 3, 4, 5, 6), 912743L)
        assertTrue(path.frames.any { it.impact > 0.4f })
        path.frames.zipWithNext().forEach { (a, b) ->
            a.poses.indices.forEach { die ->
                assertTrue("No position teleport", (a.poses[die].position - b.poses[die].position).length() < 0.11f)
                val first = a.poses[die].rotation
                val second = b.poses[die].rotation
                val dot = abs(first.w * second.w + first.x * second.x + first.y * second.y + first.z * second.z)
                assertTrue("No last-frame orientation snap", dot > 0.97f)
            }
        }
    }

    @Test fun repeatedThrowsDoNotConvergeOnPresetGridPositions() {
        val locations = (0..5).map { seed ->
            DiceRigidBodySimulation.generate(6, listOf(1), seed.toLong()).frames.last().poses.single().position
        }
        assertTrue(locations.maxOf { it.x } - locations.minOf { it.x } > 0.4f)
        assertTrue(locations.maxOf { it.z } - locations.minOf { it.z } > 0.4f)
    }

    @Test fun textCoordinatesAreFixedToEachRealFace() {
        for (sides in listOf(4, 6, 8, 10, 12, 20)) {
            buildPhysicsMeshFaces(buildPhysicsMesh(sides)).forEach { face ->
                val basis = buildDiceFaceBasis(face)
                assertEquals(1f, basis.right.length(), 0.0001f)
                assertEquals(1f, basis.up.length(), 0.0001f)
                assertEquals(0f, basis.right.dot(face.normal), 0.0001f)
                assertEquals(0f, basis.up.dot(face.normal), 0.0001f)
                assertTrue(basis.clearance > 0.02f)
                var previous: Vec3? = null
                repeat(720) { step ->
                    val angle = step * 0.01f
                    val rotation = Quat(kotlin.math.cos(angle / 2), 0f, kotlin.math.sin(angle / 2), 0f)
                    val anchor = rotation.rotate(basis.center + basis.right * basis.clearance)
                    previous?.let { assertTrue("Text anchor must not switch edges", (anchor - it).length() < 0.01f) }
                    previous = anchor
                }
            }
        }
    }

    @Test fun cancelledPreparationReleasesBodiesAndAllowsAnotherRoll() {
        val before = com.badlogic.gdx.physics.bullet.collision.btCollisionObject.instances.size
        var checkpoints = 0
        assertThrows(java.util.concurrent.CancellationException::class.java) {
            DiceRigidBodySimulation.generate(20, List(6) { 1 }, 901L) {
                if (++checkpoints == 3) throw java.util.concurrent.CancellationException()
            }
        }
        assertEquals(before, com.badlogic.gdx.physics.bullet.collision.btCollisionObject.instances.size)
        val path = DiceRigidBodySimulation.generate(10, listOf(7), 902L)
        assertTrue(DiceRigidBodySimulation.isValidRest(10, path.frames.last().poses))
        assertEquals(before, com.badlogic.gdx.physics.bullet.collision.btCollisionObject.instances.size)
    }
}
