package site.siredvin.broccolium.modules.storage.fluid.api

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

fun interface AgnosticFluidStorageEntityExtractor {
    fun extract(level: Level, entity: Entity): AgnosticFluidStorage?
}
