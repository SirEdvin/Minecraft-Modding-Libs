package site.siredvin.broccolium.modules.lookup

import net.minecraft.world.level.Level

fun interface InventoryItemBasedLookup<T, V> {
    fun extract(level: Level, origin: V, slot: Int): T?
}
