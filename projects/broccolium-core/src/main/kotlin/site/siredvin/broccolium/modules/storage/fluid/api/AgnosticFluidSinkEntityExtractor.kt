package site.siredvin.broccolium.modules.storage.fluid.api

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

fun interface AgnosticFluidSinkEntityExtractor {
    fun extract(level: Level, entity: Entity): AgnosticFluidSink?
}
