package com.zhifou.fortune

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.utils.GdxNativesLoader
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

internal data class DicePose(val position: Vec3, val rotation: Quat)
internal data class DiceMotionFrame(val poses: List<DicePose>, val impact: Float)

internal data class DiceTrajectory(
    val frames: List<DiceMotionFrame>,
    val faceValues: List<List<Int>>,
    val results: List<Int>,
    val attempts: Int,
) {
    val durationSeconds: Float get() = (frames.size - 1) * DiceRigidBodySimulation.STEP_SECONDS
}

/** Computes a real rigid-body path off the UI thread; playback never attracts dice to targets. */
internal object DiceRigidBodySimulation {
    const val STEP_SECONDS = 1f / 120f
    const val TABLE_HALF_X = 1.38f
    const val TABLE_HALF_Z = 0.98f
    private val initialized by lazy {
        GdxNativesLoader.load()
        Bullet.init(false, false)
        true
    }

    fun prepare() { check(initialized) }

    @Synchronized
    fun generate(
        sides: Int,
        results: List<Int>,
        seed: Long = Random.nextLong(),
        strength: DiceThrowStrength = DiceThrowStrength.Medium,
        checkCancellation: () -> Unit = {},
    ): DiceTrajectory {
        require(sides in listOf(4, 6, 8, 10, 12, 20))
        require(results.size in 1..6 && results.all { it in 1..sides })
        prepare()
        val random = Random(seed)
        val mesh = buildPhysicsMesh(sides)
        val faces = buildPhysicsMeshFaces(mesh)
        var lastFailure = "still moving"
        repeat(24) { attempt ->
            checkCancellation()
            val frames = simulate(mesh, sides, results.size, random, strength, checkCancellation)
            if (frames != null && isValidRest(sides, frames.last().poses)) {
                val labels = frames.last().poses.mapIndexed { index, pose ->
                    faceValues(faces, resultFace(faces, pose.rotation, sides), results[index], sides)
                }
                return DiceTrajectory(frames, labels, results.toList(), attempt + 1)
            }
            if (frames != null) lastFailure = frames.last().poses.joinToString { pose ->
                val lowest = mesh.flatMap { listOf(it.a, it.b, it.c) }.minOf { pose.position.y + pose.rotation.rotate(it).y }
                "floor=$lowest normal=${faces.minOf { pose.rotation.rotate(it.normal).y }}"
            }
        }
        error("D$sides x${results.size} seed=$seed did not settle: $lastFailure")
    }

    private fun simulate(
        mesh: List<PhysicsTriangle3d>,
        sides: Int,
        count: Int,
        random: Random,
        strength: DiceThrowStrength,
        checkCancellation: () -> Unit,
    ): List<DiceMotionFrame>? {
        val configuration = btDefaultCollisionConfiguration()
        val dispatcher = btCollisionDispatcher(configuration)
        val broadphase = btDbvtBroadphase()
        val solver = btSequentialImpulseConstraintSolver()
        val world = btDiscreteDynamicsWorld(dispatcher, broadphase, solver, configuration)
        val shapes = ArrayList<btCollisionShape>(6)
        val bodies = ArrayList<btRigidBody>(count + 5)
        val dice = ArrayList<btRigidBody>(count)
        try {
            world.gravity = Vector3(0f, -9.81f, 0f)
            world.solverInfo.numIterations = 14
            world.solverInfo.splitImpulse = 1
            world.solverInfo.restitutionVelocityThreshold = 0.6f

            fun plane(normal: Vector3, distance: Float, friction: Float = 0.12f) {
                val shape = btStaticPlaneShape(normal, distance)
                shapes += shape
                val body = btRigidBody(0f, null, shape, Vector3.Zero)
                body.friction = friction
                body.restitution = 0.78f
                bodies += body
                world.addRigidBody(body)
            }
            plane(Vector3.Y, 0f, 0.86f)
            plane(Vector3.X, -TABLE_HALF_X)
            plane(Vector3(-1f, 0f, 0f), -TABLE_HALF_X)
            plane(Vector3.Z, -TABLE_HALF_Z)
            plane(Vector3(0f, 0f, -1f), -TABLE_HALF_Z)

            val shape = btConvexHullShape()
            shapes += shape
            val vertices = mesh.flatMap { listOf(it.a, it.b, it.c) }.distinct()
            vertices.forEach { shape.addPoint(Vector3(it.x, it.y, it.z), false) }
            shape.margin = 0.002f
            shape.recalcLocalAabb()
            val inertia = Vector3()
            shape.calculateLocalInertia(1f, inertia)
            val direction = random.nextFloat() * (Math.PI * 2).toFloat()
            val columns = minOf(count, 3)
            val rows = (count + columns - 1) / columns
            repeat(count) { index ->
                val row = index / columns
                val column = index % columns
                val items = minOf(columns, count - row * columns)
                val x = (column - (items - 1) * 0.5f) * 0.88f + random.nextFloat() * 0.02f - 0.01f
                val z = (row - (rows - 1) * 0.5f) * 0.80f + random.nextFloat() * 0.02f - 0.01f
                val rotation = randomRotation(random)
                val body = btRigidBody(1f, null, shape, inertia)
                body.worldTransform = Matrix4().set(
                    Vector3(x, 0.65f + (column % 2) * 0.55f + row * 0.65f + random.nextFloat() * 0.04f, z),
                    Quaternion(rotation.x, rotation.y, rotation.z, rotation.w),
                )
                body.friction = 0.63f
                body.restitution = 0.38f
                body.rollingFriction = 0f
                body.spinningFriction = 0.012f
                body.setDamping(0.08f, 0.12f)
                body.setSleepingThresholds(0.025f, 0.04f)
                body.ccdMotionThreshold = 0.12f
                body.ccdSweptSphereRadius = 0.08f
                val throwAngle = direction + index * 2.399963f
                val speed = strength.speedMin + random.nextFloat() * strength.speedSpread
                body.linearVelocity = Vector3(
                    cos(throwAngle) * speed,
                    -0.4f - random.nextFloat() * 1.1f,
                    sin(throwAngle) * speed,
                )
                // Guarantee substantial horizontal tumbling, not an almost-static or Y-only spin.
                val tumbleAngle = random.nextFloat() * (Math.PI * 2).toFloat()
                val tumble = strength.tumbleMin + random.nextFloat() * strength.tumbleSpread
                body.angularVelocity = Vector3(
                    cos(tumbleAngle) * tumble,
                    (random.nextFloat() * .56f - .28f) * tumble,
                    sin(tumbleAngle) * tumble,
                )
                bodies += body
                dice += body
                world.addRigidBody(body)
            }

            val matrix = Matrix4()
            val position = Vector3()
            val rotation = Quaternion()
            fun snapshot(impact: Float) = DiceMotionFrame(
                dice.map { body ->
                    body.getWorldTransform(matrix)
                    matrix.getTranslation(position)
                    matrix.getRotation(rotation, true)
                    DicePose(Vec3(position.x, position.y, position.z), Quat(rotation.w, rotation.x, rotation.y, rotation.z))
                },
                impact,
            )

            val frames = ArrayList<DiceMotionFrame>(480)
            frames += snapshot(0f)
            var quietSteps = 0
            repeat(840) { step ->
                if (step % 32 == 0) checkCancellation()
                world.stepSimulation(STEP_SECONDS, 0, STEP_SECONDS)
                var impact = 0f
                repeat(dispatcher.numManifolds) { manifoldIndex ->
                    val manifold = dispatcher.getManifoldByIndexInternal(manifoldIndex)
                    repeat(manifold.numContacts) { contactIndex ->
                        val point = manifold.getContactPoint(contactIndex)
                        if (point.lifeTime <= 1 && point.appliedImpulse > 0.4f) {
                            impact = maxOf(impact, point.appliedImpulse)
                        }
                    }
                }
                val frame = snapshot(impact)
                frames += frame
                val quiet = step > 90 && dice.all {
                    it.linearVelocity.len2() < 0.0012f && it.angularVelocity.len2() < 0.0064f
                }
                quietSteps = if (quiet) quietSteps + 1 else 0
                if (quietSteps >= 24) {
                    return frames
                }
            }
            return null
        } finally {
            bodies.asReversed().forEach { world.removeRigidBody(it); it.dispose() }
            world.dispose()
            shapes.asReversed().forEach { it.dispose() }
            solver.dispose()
            broadphase.dispose()
            dispatcher.dispose()
            configuration.dispose()
        }
    }

    internal fun resultFace(faces: List<PhysicsMeshFace>, rotation: Quat, sides: Int): Int =
        faces.indices.maxBy { index ->
            val normal = rotation.rotate(faces[index].normal)
            // A tetrahedron has no upper horizontal face; retain its established front reading view.
            normal.y * 1000f + if (sides == 4) normal.z else 0f
        }

    internal fun faceValues(faces: List<PhysicsMeshFace>, top: Int, result: Int, sides: Int): List<Int> {
        val values = MutableList(sides) { 0 }
        values[top] = result
        if (sides == 6) {
            fun assignOpposite(face: Int) {
                val opposite = faces.indices.minBy { faces[it].normal.dot(faces[face].normal) }
                values[opposite] = 7 - values[face]
            }
            assignOpposite(top)
            values.indices.forEach { face ->
                if (values[face] == 0) {
                    values[face] = (1..6).first { it !in values }
                    assignOpposite(face)
                }
            }
            return values
        }
        val unused = (1..sides).filter { it !in values }.iterator()
        values.indices.filter { values[it] == 0 }.forEach { values[it] = unused.next() }
        return values
    }

    internal fun isValidRest(sides: Int, poses: List<DicePose>): Boolean {
        val mesh = buildPhysicsMesh(sides)
        val vertices = mesh.flatMap { listOf(it.a, it.b, it.c) }.distinct()
        val faces = buildPhysicsMeshFaces(mesh)
        val worldVertices = poses.map { pose -> vertices.map { pose.position + pose.rotation.rotate(it) } }
        if (poses.indices.any { index ->
                val points = worldVertices[index]
                abs(points.minOf { it.y }) > 0.012f ||
                    faces.none { poses[index].rotation.rotate(it.normal).y < -0.99998f } ||
                    points.any { abs(it.x) > TABLE_HALF_X + 0.005f || abs(it.z) > TABLE_HALF_Z + 0.005f }
            }
        ) return false
        // Every body must touch the floor. Projected bevels may meet without stacking;
        // the result faces themselves must remain separate and readable.
        val footprints = poses.map { pose ->
            val face = faces[resultFace(faces, pose.rotation, sides)]
            convexFootprint(face.vertices.map { pose.position + pose.rotation.rotate(it) })
        }
        return footprints.indices.all { i ->
            (i + 1 until footprints.size).all { j -> separated(footprints[i], footprints[j]) }
        }
    }

    private fun convexFootprint(points: List<Vec3>): List<Vec3> {
        val sorted = points.distinctBy { it.x to it.z }.sortedWith(compareBy<Vec3> { it.x }.thenBy { it.z })
        fun half(input: List<Vec3>): List<Vec3> {
            val hull = ArrayList<Vec3>()
            input.forEach { point ->
                while (hull.size >= 2) {
                    val a = hull[hull.lastIndex - 1]
                    val b = hull.last()
                    if ((b.x - a.x) * (point.z - a.z) - (b.z - a.z) * (point.x - a.x) > 0f) break
                    hull.removeAt(hull.lastIndex)
                }
                hull += point
            }
            return hull.dropLast(1)
        }
        return half(sorted) + half(sorted.asReversed())
    }

    private fun separated(a: List<Vec3>, b: List<Vec3>): Boolean = listOf(a, b).any { polygon ->
        polygon.indices.any { index ->
            val first = polygon[index]
            val second = polygon[(index + 1) % polygon.size]
            val nx = first.z - second.z
            val nz = second.x - first.x
            val length = sqrt(nx * nx + nz * nz).coerceAtLeast(0.000001f)
            val projectionA = a.map { (it.x * nx + it.z * nz) / length }
            val projectionB = b.map { (it.x * nx + it.z * nz) / length }
            projectionA.max() <= projectionB.min() + 0.003f || projectionB.max() <= projectionA.min() + 0.003f
        }
    }

    private fun randomRotation(random: Random): Quat {
        val u = random.nextFloat()
        val a = random.nextFloat() * (Math.PI * 2).toFloat()
        val b = random.nextFloat() * (Math.PI * 2).toFloat()
        return Quat(sqrt(u) * cos(b), sqrt(1f - u) * sin(a), sqrt(1f - u) * cos(a), sqrt(u) * sin(b))
    }
}
