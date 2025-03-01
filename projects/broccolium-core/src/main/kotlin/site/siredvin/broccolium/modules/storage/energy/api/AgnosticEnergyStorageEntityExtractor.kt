package site.siredvin.broccolium.modules.storage.energy.api

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

fun interface AgnosticEnergyStorageEntityExtractor {
    fun extract(level: Level, entity: Entity): AgnosticEnergyStorage?
}
