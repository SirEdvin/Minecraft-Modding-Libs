package site.siredvin.tweakium.modules.data

import com.google.common.hash.Hashing
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.VanillaPackResourcesBuilder
import net.minecraft.server.packs.repository.PackSource
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.Optional
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

/** Derive the approved purple barrel appearance from this node's pinned vanilla textures. */
class CreativeFillerTextures(private val output: PackOutput) : DataProvider {
    override fun getName(): String = "Tweakium Creative Filler textures"

    @Suppress("DEPRECATION") // Vanilla CachedOutput uses SHA-1 for change detection, not security.
    override fun run(cache: CachedOutput): CompletableFuture<*> = CompletableFuture.runAsync {
        for (face in listOf("side", "top", "bottom")) {
            val source = readVanillaFace(face)
            val bytes = ByteArrayOutputStream().use {
                check(ImageIO.write(recolor(source), "PNG", it))
                it.toByteArray()
            }
            val path = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve("tweakium/textures/block/creative_filler_$face.png")
            cache.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes))
        }
    }

    companion object {
        internal fun readVanillaFace(face: String): BufferedImage {
            val location = PackLocationInfo("vanilla", Component.literal("Vanilla"), PackSource.BUILT_IN, Optional.empty())
            return VanillaPackResourcesBuilder().pushJarResources().applyDevelopmentConfig().exposeNamespace("minecraft")
                .build(location).use { pack ->
                    val id = Identifier.withDefaultNamespace("textures/block/barrel_$face.png")
                    checkNotNull(pack.getResource(PackType.CLIENT_RESOURCES, id)) { "Missing vanilla texture: $id" }
                        .get().use { checkNotNull(ImageIO.read(it)) { "Invalid vanilla texture: $id" } }
                }
        }

        /** Preserve alpha, grain and metal-band contrast; map luminance onto a saturated purple ramp. */
        internal fun recolor(source: BufferedImage): BufferedImage {
            val result = BufferedImage(source.width, source.height, BufferedImage.TYPE_INT_ARGB)
            for (y in 0 until source.height) {
                for (x in 0 until source.width) {
                    val pixel = source.getRGB(x, y)
                    val red = (pixel ushr 16) and 255
                    val green = (pixel ushr 8) and 255
                    val blue = pixel and 255
                    val luminance = (54 * red + 183 * green + 19 * blue) / 256
                    val purpleRed = (luminance * 180 / 128).coerceAtMost(255)
                    val purpleGreen = luminance * 24 / 128
                    val purpleBlue = (luminance * 240 / 128).coerceAtMost(255)
                    result.setRGB(x, y, (pixel and -0x1000000) or (purpleRed shl 16) or (purpleGreen shl 8) or purpleBlue)
                }
            }
            return result
        }
    }
}
