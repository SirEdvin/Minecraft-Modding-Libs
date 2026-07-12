package site.siredvin.tweakium.modules.data

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.registries.RegistryPatchGenerator
import site.siredvin.broccolium.modules.data.api.GeneratorSink

fun GeneratorSink.upgrades(
    modID: String,
    pockets: RegistrySetBuilder.RegistryBootstrap<IPocketUpgrade> = RegistrySetBuilder.RegistryBootstrap {},
    turtles: RegistrySetBuilder.RegistryBootstrap<ITurtleUpgrade> = RegistrySetBuilder.RegistryBootstrap {},
) {
    addRegistryPatch(modID) { registries ->
        RegistryPatchGenerator.createLookup(
            registries,
            RegistrySetBuilder()
                .add(IPocketUpgrade.REGISTRY, pockets)
                .add(ITurtleUpgrade.REGISTRY, turtles),
        )
    }
}
