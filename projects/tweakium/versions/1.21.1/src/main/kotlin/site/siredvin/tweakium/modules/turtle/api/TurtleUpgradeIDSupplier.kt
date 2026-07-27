package site.siredvin.tweakium.modules.turtle.api

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import site.siredvin.broccolium.modules.platform.PlatformRegistries

fun interface TurtleUpgradeIDSupplier {
    companion object {
        val IDENTIC = TurtleUpgradeIDSupplier { PlatformRegistries.ITEMS.getKey(it) }
        val WITHOUT_CORE = TurtleUpgradeIDSupplier {
            val base = IDENTIC.get(it)
            // To cutoff _core part
            return@TurtleUpgradeIDSupplier ResourceLocation.fromNamespaceAndPath(base.namespace, base.path.replace("_core", ""))
        }
        val WITHOUT_TURTLE = TurtleUpgradeIDSupplier {
            val base = IDENTIC.get(it)
            // To cutoff _core part
            return@TurtleUpgradeIDSupplier ResourceLocation.fromNamespaceAndPath(base.namespace, base.path.replace("turtle_", ""))
        }
    }

    fun get(item: Item): ResourceLocation
}
