package site.siredvin.broccolium

import net.minecraft.world.entity.item.ItemEntity
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent
import site.siredvin.broccolium.modules.platform.LibCommonHooks

@EventBusSubscriber(modid = BroccoliumCore.MOD_ID)
object ForgeCommonHooks {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onEntitySpawn(event: EntityJoinLevelEvent) {
        if (LibCommonHooks.onEntitySpawn(event.entity)) event.isCanceled = true
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    fun onLivingDrops(event: LivingDropsEvent) {
        event.drops.removeIf { itemEntity: ItemEntity ->
            LibCommonHooks.onLivingDrop(
                event.entity,
                itemEntity.item,
            )
        }
    }
}
