package site.siredvin.broccolium.modules.storage.energy.api

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

fun interface AgnosticEnergySinkEntityExtractor {
    fun extract(level: Level, entity: Entity): AgnosticEnergySink?
}
