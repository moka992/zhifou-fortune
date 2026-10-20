package com.zhifou.fortune

import kotlin.math.*

internal data class Vec3(var x: Float, var y: Float, var z: Float) {
    operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
    operator fun times(value: Float) = Vec3(x * value, y * value, z * value)
    fun dot(other: Vec3) = x * other.x + y * other.y + z * other.z
    fun cross(other: Vec3) = Vec3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x,
    )
    fun length() = sqrt(x * x + y * y + z * z)
    fun normalized(): Vec3 {
        val length = length()
        return if (length < 0.0001f) Vec3(0f, 1f, 0f) else this * (1f / length)
    }
}

internal data class Quat(var w: Float, var x: Float, var y: Float, var z: Float) {
    operator fun times(other: Quat) = Quat(
        w * other.w - x * other.x - y * other.y - z * other.z,
        w * other.x + x * other.w + y * other.z - z * other.y,
        w * other.y - x * other.z + y * other.w + z * other.x,
        w * other.z + x * other.y - y * other.x + z * other.w,
    )

    fun normalized(): Quat {
        val length = sqrt(w * w + x * x + y * y + z * z).coerceAtLeast(0.0001f)
        return Quat(w / length, x / length, y / length, z / length)
    }

    fun rotate(vector: Vec3): Vec3 {
        val tx = 2f * (y * vector.z - z * vector.y)
        val ty = 2f * (z * vector.x - x * vector.z)
        val tz = 2f * (x * vector.y - y * vector.x)
        return Vec3(
            vector.x + w * tx + y * tz - z * ty,
            vector.y + w * ty + z * tx - x * tz,
            vector.z + w * tz + x * ty - y * tx,
        )
    }

    fun integrate(angularVelocity: Vec3, dt: Float): Quat {
        val derivative = Quat(0f, angularVelocity.x, angularVelocity.y, angularVelocity.z) * this
        return Quat(
            w + derivative.w * 0.5f * dt,
            x + derivative.x * 0.5f * dt,
            y + derivative.y * 0.5f * dt,
            z + derivative.z * 0.5f * dt,
        ).normalized()
    }

    private fun conjugate() = Quat(w, -x, -y, -z)

    companion object {
        val identity = Quat(1f, 0f, 0f, 0f)

        fun fromTo(from: Vec3, to: Vec3): Quat {
            val source = from.normalized()
            val target = to.normalized()
            val dot = source.dot(target).coerceIn(-1f, 1f)
            if (dot > 0.9995f) return identity
            if (dot < -0.9995f) {
                val reference = if (kotlin.math.abs(source.x) < 0.9f) Vec3(1f, 0f, 0f) else Vec3(0f, 1f, 0f)
                val axis = source.cross(reference).normalized()
                return Quat(0f, axis.x, axis.y, axis.z)
            }
            val cross = source.cross(target)
            return Quat(
                w = 1f + dot,
                x = cross.x,
                y = cross.y,
                z = cross.z,
            ).normalized()
        }

        fun slerp(from: Quat, to: Quat, progress: Float): Quat {
            val t = progress.coerceIn(0f, 1f)
            var target = to
            var dot = from.w * target.w + from.x * target.x + from.y * target.y + from.z * target.z
            if (dot < 0f) {
                target = Quat(-target.w, -target.x, -target.y, -target.z)
                dot = -dot
            }
            if (dot > 0.9995f) {
                return Quat(
                    from.w + (target.w - from.w) * t,
                    from.x + (target.x - from.x) * t,
                    from.y + (target.y - from.y) * t,
                    from.z + (target.z - from.z) * t,
                ).normalized()
            }
            val theta = kotlin.math.acos(dot.coerceIn(-1f, 1f))
            val sinTheta = sin(theta).coerceAtLeast(0.0001f)
            val fromWeight = sin((1f - t) * theta) / sinTheta
            val targetWeight = sin(t * theta) / sinTheta
            return Quat(
                from.w * fromWeight + target.w * targetWeight,
                from.x * fromWeight + target.x * targetWeight,
                from.y * fromWeight + target.y * targetWeight,
                from.z * fromWeight + target.z * targetWeight,
            ).normalized()
        }
    }
}


internal data class PhysicsTriangle3d(val a: Vec3, val b: Vec3, val c: Vec3)

internal data class PhysicsMeshFace(
    val vertices: List<Vec3>,
    val normal: Vec3,
)

internal data class PhysicsMeshEdge(
    val a: Vec3,
    val b: Vec3,
    val normals: List<Vec3>,
)

internal data class DiceFaceBasis(val center: Vec3, val right: Vec3, val up: Vec3, val clearance: Float)

/** A permanent object-space text frame: projected edge lengths never select its orientation. */
internal fun buildDiceFaceBasis(face: PhysicsMeshFace): DiceFaceBasis {
    val center = face.vertices.reduce { sum, vertex -> sum + vertex } * (1f / face.vertices.size)
    val right = (face.vertices[1] - face.vertices[0]).normalized()
    val up = face.normal.cross(right).normalized()
    val clearance = face.vertices.indices.minOf { index ->
        val a = face.vertices[index]
        val b = face.vertices[(index + 1) % face.vertices.size]
        (center - a).cross(b - a).length() / (b - a).length()
    }
    return DiceFaceBasis(center, right, up, clearance)
}


internal fun buildPhysicsMesh(sides: Int): List<PhysicsTriangle3d> {
    val radius = if (sides == 6) 0.31f * 0.90f else 0.31f * 1.10f
    return when (sides) {
        4 -> {
            val vertices = listOf(
                Vec3(1f, 1f, 1f),
                Vec3(-1f, -1f, 1f),
                Vec3(-1f, 1f, -1f),
                Vec3(1f, -1f, -1f),
            ).map { vertex -> vertex.normalized() * radius }
            listOf(
                PhysicsTriangle3d(vertices[0], vertices[1], vertices[2]),
                PhysicsTriangle3d(vertices[0], vertices[3], vertices[1]),
                PhysicsTriangle3d(vertices[0], vertices[2], vertices[3]),
                PhysicsTriangle3d(vertices[1], vertices[3], vertices[2]),
            )
        }
        6 -> cubePhysicsMesh(radius)
        8 -> octahedronPhysicsMesh(radius)
        10 -> pentagonalTrapezohedronPhysicsMesh(radius)
        12 -> dodecahedronPhysicsMesh(radius)
        20 -> icosahedronPhysicsMesh(radius)
        else -> lathePhysicsMesh(radius, 6)
    }
}

internal fun buildPhysicsMeshFaces(mesh: List<PhysicsTriangle3d>): List<PhysicsMeshFace> {
    data class FaceAccumulator(
        val normal: Vec3,
        val planeDistance: Float,
        val vertices: MutableList<Vec3>,
    )

    fun samePoint(first: Vec3, second: Vec3): Boolean {
        return kotlin.math.abs(first.x - second.x) < 0.0001f &&
            kotlin.math.abs(first.y - second.y) < 0.0001f &&
            kotlin.math.abs(first.z - second.z) < 0.0001f
    }

    val faces = mutableListOf<FaceAccumulator>()
    mesh.forEach { triangle ->
        val center = (triangle.a + triangle.b + triangle.c) * (1f / 3f)
        val rawNormal = (triangle.b - triangle.a).cross(triangle.c - triangle.a).normalized()
        val normal = if (rawNormal.dot(center) < 0f) rawNormal * -1f else rawNormal
        val planeDistance = normal.dot(center)
        val face = faces.firstOrNull { candidate ->
            candidate.normal.dot(normal) > 0.9995f &&
                kotlin.math.abs(candidate.planeDistance - planeDistance) < 0.001f
        } ?: FaceAccumulator(normal, planeDistance, mutableListOf()).also { faces += it }
        listOf(triangle.a, triangle.b, triangle.c).forEach { vertex ->
            if (face.vertices.none { existing -> samePoint(existing, vertex) }) {
                face.vertices += vertex
            }
        }
    }
    return faces.map { face ->
        val center = face.vertices.reduce { total, vertex -> total + vertex } * (1f / face.vertices.size)
        val reference = (face.vertices.first() - center).normalized()
        val tangent = face.normal.cross(reference).normalized()
        PhysicsMeshFace(
            vertices = face.vertices.sortedBy { vertex ->
                val delta = vertex - center
                atan2(delta.dot(tangent), delta.dot(reference))
            },
            normal = face.normal,
        )
    }
}

internal fun buildPhysicsMeshEdges(mesh: List<PhysicsTriangle3d>): List<PhysicsMeshEdge> {
    data class EdgeAccumulator(
        val a: Vec3,
        val b: Vec3,
        val normals: MutableList<Vec3>,
    )

    fun samePoint(first: Vec3, second: Vec3): Boolean {
        return kotlin.math.abs(first.x - second.x) < 0.0001f &&
            kotlin.math.abs(first.y - second.y) < 0.0001f &&
            kotlin.math.abs(first.z - second.z) < 0.0001f
    }

    val edges = mutableListOf<EdgeAccumulator>()
    mesh.forEach { triangle ->
        val center = (triangle.a + triangle.b + triangle.c) * (1f / 3f)
        val rawNormal = (triangle.b - triangle.a).cross(triangle.c - triangle.a).normalized()
        val normal = if (rawNormal.dot(center) < 0f) rawNormal * -1f else rawNormal
        listOf(
            triangle.a to triangle.b,
            triangle.b to triangle.c,
            triangle.c to triangle.a,
        ).forEach { (a, b) ->
            val existing = edges.firstOrNull { edge ->
                (samePoint(edge.a, a) && samePoint(edge.b, b)) ||
                    (samePoint(edge.a, b) && samePoint(edge.b, a))
            }
            if (existing == null) {
                edges += EdgeAccumulator(a, b, mutableListOf(normal))
            } else {
                existing.normals += normal
            }
        }
    }
    return edges
        .filter { edge ->
            edge.normals.size == 1 || edge.normals.any { first ->
                edge.normals.any { second -> first.dot(second) < 0.985f }
            }
        }
        .map { edge -> PhysicsMeshEdge(edge.a, edge.b, edge.normals.toList()) }
}

internal fun cubePhysicsMesh(radius: Float): List<PhysicsTriangle3d> {
    val vertices = listOf(
        Vec3(-radius, -radius, -radius), Vec3(radius, -radius, -radius),
        Vec3(radius, radius, -radius), Vec3(-radius, radius, -radius),
        Vec3(-radius, -radius, radius), Vec3(radius, -radius, radius),
        Vec3(radius, radius, radius), Vec3(-radius, radius, radius),
    )
    val quads = listOf(
        intArrayOf(0, 1, 2, 3), intArrayOf(4, 7, 6, 5),
        intArrayOf(0, 4, 5, 1), intArrayOf(1, 5, 6, 2),
        intArrayOf(2, 6, 7, 3), intArrayOf(4, 0, 3, 7),
    )
    return quads.flatMap { quad ->
        listOf(
            PhysicsTriangle3d(vertices[quad[0]], vertices[quad[1]], vertices[quad[2]]),
            PhysicsTriangle3d(vertices[quad[0]], vertices[quad[2]], vertices[quad[3]]),
        )
    }
}

internal fun octahedronPhysicsMesh(radius: Float): List<PhysicsTriangle3d> {
    val top = Vec3(0f, radius, 0f)
    val bottom = Vec3(0f, -radius, 0f)
    val ring = listOf(
        Vec3(radius, 0f, 0f), Vec3(0f, 0f, radius),
        Vec3(-radius, 0f, 0f), Vec3(0f, 0f, -radius),
    )
    return ring.indices.flatMap { index ->
        val next = ring[(index + 1) % ring.size]
        listOf(
            PhysicsTriangle3d(top, ring[index], next),
            PhysicsTriangle3d(bottom, next, ring[index]),
        )
    }
}

internal fun pentagonalTrapezohedronPhysicsMesh(radius: Float): List<PhysicsTriangle3d> {
    val segments = 5
    val top = Vec3(0f, radius, 0f)
    val bottom = Vec3(0f, -radius, 0f)
    val halfStep = Math.PI.toFloat() / segments
    val halfStepCosine = cos(halfStep)
    val ringHeight = radius * (1f - halfStepCosine) / (1f + halfStepCosine)
    val ringRadius = radius * 0.92f
    val ring = List(segments * 2) { index ->
        val angle = Math.PI.toFloat() * index / segments
        Vec3(
            cos(angle) * ringRadius,
            if (index % 2 == 0) -ringHeight else ringHeight,
            sin(angle) * ringRadius,
        )
    }
    val faces = mutableListOf<PhysicsTriangle3d>()
    repeat(segments) { index ->
        val lowerRingIndex = index * 2
        val upperLeft = ring[(lowerRingIndex - 1 + ring.size) % ring.size]
        val upperCenter = ring[lowerRingIndex]
        val upperRight = ring[(lowerRingIndex + 1) % ring.size]
        faces += PhysicsTriangle3d(top, upperLeft, upperCenter)
        faces += PhysicsTriangle3d(top, upperCenter, upperRight)

        val upperRingIndex = lowerRingIndex + 1
        val lowerLeft = ring[(upperRingIndex - 1 + ring.size) % ring.size]
        val lowerCenter = ring[upperRingIndex]
        val lowerRight = ring[(upperRingIndex + 1) % ring.size]
        faces += PhysicsTriangle3d(bottom, lowerLeft, lowerCenter)
        faces += PhysicsTriangle3d(bottom, lowerCenter, lowerRight)
    }
    return faces
}

internal fun dodecahedronPhysicsMesh(radius: Float): List<PhysicsTriangle3d> {
    val phi = (1f + sqrt(5f)) / 2f
    val inversePhi = 1f / phi
    val vertices = listOf(
        Vec3(-1f, -1f, -1f), Vec3(-1f, -1f, 1f),
        Vec3(-1f, 1f, -1f), Vec3(-1f, 1f, 1f),
        Vec3(1f, -1f, -1f), Vec3(1f, -1f, 1f),
        Vec3(1f, 1f, -1f), Vec3(1f, 1f, 1f),
        Vec3(0f, -inversePhi, -phi), Vec3(0f, -inversePhi, phi),
        Vec3(0f, inversePhi, -phi), Vec3(0f, inversePhi, phi),
        Vec3(-inversePhi, -phi, 0f), Vec3(-inversePhi, phi, 0f),
        Vec3(inversePhi, -phi, 0f), Vec3(inversePhi, phi, 0f),
        Vec3(-phi, 0f, -inversePhi), Vec3(-phi, 0f, inversePhi),
        Vec3(phi, 0f, -inversePhi), Vec3(phi, 0f, inversePhi),
    ).map { vertex -> vertex.normalized() * radius }
    val pentagons = listOf(
        intArrayOf(0, 1, 12, 16, 17),
        intArrayOf(0, 2, 8, 10, 16),
        intArrayOf(0, 4, 8, 12, 14),
        intArrayOf(1, 3, 9, 11, 17),
        intArrayOf(1, 5, 9, 12, 14),
        intArrayOf(2, 3, 13, 16, 17),
        intArrayOf(2, 6, 10, 13, 15),
        intArrayOf(3, 7, 11, 13, 15),
        intArrayOf(4, 5, 14, 18, 19),
        intArrayOf(4, 6, 8, 10, 18),
        intArrayOf(5, 7, 9, 11, 19),
        intArrayOf(6, 7, 15, 18, 19),
    )
    return pentagons.flatMap { face ->
        val center = face
            .map { index -> vertices[index] }
            .reduce { total, vertex -> total + vertex } * (1f / face.size)
        val outward = center.normalized()
        val reference = (vertices[face.first()] - center).normalized()
        val tangent = outward.cross(reference).normalized()
        val ordered = face.sortedBy { index ->
            val delta = vertices[index] - center
            atan2(delta.dot(tangent), delta.dot(reference))
        }
        (1 until ordered.lastIndex).map { index ->
            PhysicsTriangle3d(
                vertices[ordered[0]],
                vertices[ordered[index]],
                vertices[ordered[index + 1]],
            )
        }
    }
}

internal fun lathePhysicsMesh(radius: Float, segments: Int): List<PhysicsTriangle3d> {
    val top = Vec3(0f, radius, 0f)
    val bottom = Vec3(0f, -radius, 0f)
    val upper = List(segments) { index ->
        val angle = 2f * Math.PI.toFloat() * index / segments
        Vec3(kotlin.math.cos(angle) * radius * 0.9f, radius * 0.35f, kotlin.math.sin(angle) * radius * 0.9f)
    }
    val lower = upper.map { Vec3(it.x, -it.y, it.z) }
    return upper.indices.flatMap { index ->
        val next = (index + 1) % segments
        listOf(
            PhysicsTriangle3d(top, upper[index], upper[next]),
            PhysicsTriangle3d(upper[index], lower[index], upper[next]),
            PhysicsTriangle3d(upper[next], lower[index], lower[next]),
            PhysicsTriangle3d(bottom, lower[next], lower[index]),
        )
    }
}

internal fun icosahedronPhysicsMesh(radius: Float): List<PhysicsTriangle3d> {
    val phi = (1f + sqrt(5f)) / 2f
    val raw = listOf(
        Vec3(-1f, phi, 0f), Vec3(1f, phi, 0f), Vec3(-1f, -phi, 0f), Vec3(1f, -phi, 0f),
        Vec3(0f, -1f, phi), Vec3(0f, 1f, phi), Vec3(0f, -1f, -phi), Vec3(0f, 1f, -phi),
        Vec3(phi, 0f, -1f), Vec3(phi, 0f, 1f), Vec3(-phi, 0f, -1f), Vec3(-phi, 0f, 1f),
    )
    val vertices = raw.map { it.normalized() * radius }
    val faces = listOf(
        intArrayOf(0, 11, 5), intArrayOf(0, 5, 1), intArrayOf(0, 1, 7), intArrayOf(0, 7, 10), intArrayOf(0, 10, 11),
        intArrayOf(1, 5, 9), intArrayOf(5, 11, 4), intArrayOf(11, 10, 2), intArrayOf(10, 7, 6), intArrayOf(7, 1, 8),
        intArrayOf(3, 9, 4), intArrayOf(3, 4, 2), intArrayOf(3, 2, 6), intArrayOf(3, 6, 8), intArrayOf(3, 8, 9),
        intArrayOf(4, 9, 5), intArrayOf(2, 4, 11), intArrayOf(6, 2, 10), intArrayOf(8, 6, 7), intArrayOf(9, 8, 1),
    )
    return faces.map { face -> PhysicsTriangle3d(vertices[face[0]], vertices[face[1]], vertices[face[2]]) }
}

