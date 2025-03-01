package site.siredvin.broccolium.modules.storage.item.api

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

fun interface AgnosticItemStorageEntityExtractor {
    fun extract(level: Level, entity: Entity): AgnosticItemStorage?
}
