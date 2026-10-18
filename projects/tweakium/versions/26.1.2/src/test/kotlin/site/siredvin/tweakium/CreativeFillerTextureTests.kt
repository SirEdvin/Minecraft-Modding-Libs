package site.siredvin.tweakium

import net.minecraft.data.CachedOutput
import net.minecraft.data.PackOutput
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import site.siredvin.tweakium.modules.data.CreativeFillerTextures
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreativeFillerTextureTests {
    @TempDir
    lateinit var directory: Path

    @Test
    fun `purple recolor preserves alpha and grain contrast`() {
        val original = BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB)
        original.setRGB(0, 0, 0x40404040)
        original.setRGB(1, 0, 0x7f808080)
        val result = CreativeFillerTextures.recolor(original)
        assertEquals(0x405a0c78, result.getRGB(0, 0))
        assertEquals(0x7fb418f0, result.getRGB(1, 0))
        assertEquals(0x40404040, original.getRGB(0, 0))
    }

    @Test
    fun `all pinned barrel faces generate deterministic purple textures`() {
        val outputs = mutableMapOf<Path, ByteArray>()
        val cache = CachedOutput { path, bytes, _ -> outputs[path] = bytes }
        val provider = CreativeFillerTextures(PackOutput(directory))
        provider.run(cache).join()
        val first = outputs.mapValues { it.value.copyOf() }
        provider.run(cache).join()
        assertEquals(3, outputs.size)
        for (face in listOf("side", "top", "bottom")) {
            val path = directory.resolve("assets/tweakium/textures/block/creative_filler_$face.png")
            val bytes = outputs.getValue(path)
            assertContentEquals(first.getValue(path), bytes)
            val image = ImageIO.read(bytes.inputStream())
            val source = CreativeFillerTextures.readVanillaFace(face)
            assertEquals(source.width, image.width)
            assertEquals(source.height, image.height)
            val colors = mutableSetOf<Int>()
            val luminances = mutableSetOf<Int>()
            val expected = CreativeFillerTextures.recolor(source)
            for (y in 0 until image.height) {
                for (x in 0 until image.width) {
                    val pixel = image.getRGB(x, y)
                    assertEquals(expected.getRGB(x, y), pixel, "Changed grain layout at $face:$x,$y")
                    assertEquals(source.getRGB(x, y) ushr 24, pixel ushr 24)
                    val original = source.getRGB(x, y)
                    luminances.add((54 * ((original ushr 16) and 255) + 183 * ((original ushr 8) and 255) + 19 * (original and 255)) / 256)
                    val red = (pixel ushr 16) and 255
                    val green = (pixel ushr 8) and 255
                    val blue = pixel and 255
                    assertTrue(blue >= red && red > green, "Expected saturated purple at $face:$x,$y")
                    colors.add(pixel)
                }
            }
            assertTrue(colors.size > 1, "Barrel grain/band detail was flattened: $face")
            assertEquals(luminances.size, colors.size, "Distinct vanilla shading was lost: $face")
        }
    }
}
