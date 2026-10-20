package com.zhifou.fortune

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CoinModelAssetTest {
    private fun model(): JSONObject {
        val path = "src/main/assets/models/zhifou_coin.glb"
        val file = listOf(File(path), File("app/$path")).first { it.isFile }
        val bytes = file.readBytes()
        assertTrue("Coin asset exceeds shared-mesh budget", bytes.size <= 2 * 1024 * 1024)
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        assertEquals(0x46546C67, buffer.int)
        assertEquals(2, buffer.int)
        assertEquals(bytes.size, buffer.int)
        val length = buffer.int
        assertEquals(0x4E4F534A, buffer.int)
        val json = ByteArray(length)
        buffer.get(json)
        return JSONObject(String(json, Charsets.UTF_8).trim())
    }

    @Test
    fun `ten coins share one bounded mesh with mobile triangle budget`() {
        val gltf = model()
        assertEquals(1, gltf.getJSONArray("meshes").length())
        val nodes = gltf.getJSONArray("nodes")
        assertEquals(10, nodes.length())
        repeat(10) { index ->
            assertEquals("Coin_${(index + 1).toString().padStart(2, '0')}", nodes.getJSONObject(index).getString("name"))
            assertEquals(0, nodes.getJSONObject(index).getInt("mesh"))
        }
        val accessors = gltf.getJSONArray("accessors")
        val primitives = gltf.getJSONArray("meshes").getJSONObject(0).getJSONArray("primitives")
        var triangles = 0
        repeat(primitives.length()) { index ->
            val primitive = primitives.getJSONObject(index)
            assertEquals(4, primitive.optInt("mode", 4))
            triangles += accessors.getJSONObject(primitive.getInt("indices")).getInt("count") / 3
            val position = accessors.getJSONObject(primitive.getJSONObject("attributes").getInt("POSITION"))
            repeat(3) { axis ->
                val limit = if (axis == 2) .16 else 1.02
                assertTrue(position.getJSONArray("min").getDouble(axis) >= -limit)
                assertTrue(position.getJSONArray("max").getDouble(axis) <= limit)
            }
        }
        assertTrue("Coin geometry must stay within 40k triangles", triangles in 1000..40_000)
    }

    @Test
    fun `coin keeps opaque metallic PBR materials and no external resources`() {
        val gltf = model()
        val materials = gltf.getJSONArray("materials")
        assertEquals(5, materials.length())
        repeat(materials.length()) { index ->
            val material = materials.getJSONObject(index)
            assertEquals("OPAQUE", material.optString("alphaMode", "OPAQUE"))
            val pbr = material.getJSONObject("pbrMetallicRoughness")
            assertEquals(1.0, pbr.optDouble("metallicFactor", 1.0), .001)
            assertTrue(pbr.getDouble("roughnessFactor") in .1.. .5)
        }
        assertEquals(0, gltf.optJSONArray("images")?.length() ?: 0)
        assertEquals(0, gltf.optJSONArray("textures")?.length() ?: 0)
        assertTrue(!gltf.getJSONArray("buffers").getJSONObject(0).has("uri"))
    }
}
