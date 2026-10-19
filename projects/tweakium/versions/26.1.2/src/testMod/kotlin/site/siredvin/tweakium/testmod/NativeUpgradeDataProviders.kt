package site.siredvin.tweakium.testmod

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.Items
import site.siredvin.broccolium.modules.data.api.GeneratorSink
import site.siredvin.tweakium.modules.data.turtleUpgrades
import site.siredvin.tweakium.modules.data.upgrades

object NativeUpgradeDataProviders {
    fun add(sink: GeneratorSink) {
        sink.upgrades("tweakium", { context ->
            context.register(ResourceKey.create(IPocketUpgrade.REGISTRY, NativeUpgradeFixtures.ID), NativeUpgradeFixtures.Pocket(ItemStackTemplate(Items.DIAMOND)))
        }, { context ->
            context.register(ResourceKey.create(ITurtleUpgrade.REGISTRY, NativeUpgradeFixtures.ID), NativeUpgradeFixtures.Turtle(ItemStackTemplate(Items.DIAMOND)))
        })
        sink.models({}, { items -> turtleUpgrades(items, Items.DIAMOND, baseID = NativeUpgradeFixtures.ID.withPrefix("turtle/")) })
    }
}
