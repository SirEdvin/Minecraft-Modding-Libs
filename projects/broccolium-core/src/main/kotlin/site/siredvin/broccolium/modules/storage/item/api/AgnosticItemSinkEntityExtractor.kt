package site.siredvin.broccolium.modules.storage.item.api

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

fun interface AgnosticItemSinkEntityExtractor {
    fun extract(level: Level, entity: Entity): AgnosticItemSink?
}
