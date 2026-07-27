package site.siredvin.broccolium.modules.lookup

import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

fun interface EntityBasedLookup<T> {
    fun extract(level: Level, entity: Entity, direction: Direction?): T?
}
