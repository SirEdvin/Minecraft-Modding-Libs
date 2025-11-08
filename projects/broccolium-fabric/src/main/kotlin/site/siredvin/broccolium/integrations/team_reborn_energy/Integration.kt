package site.siredvin.broccolium.integrations.team_reborn_energy

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStorageLookup
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import site.siredvin.broccolium.modules.storage.item.SlottedAgnosticItemStorageWrapper
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage
import team.reborn.energy.api.EnergyStorage

class Integration : Runnable {

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun extractEnergyStorage(level: Level, pos: BlockPos, entity: BlockEntity?, direction: Direction?): AgnosticEnergyStorage? {
            var energyStorage = EnergyStorage.SIDED.find(level, pos, null)
            if (energyStorage == null) {
                energyStorage = EnergyStorage.SIDED.find(level, pos, direction) ?: return null
            }
            return EnergyStorageWrapper(energyStorage)
        }

        @Suppress("UNUSED_PARAMETER")
        fun extractEnergyStorage(level: Level, origin: SlottedAgnosticItemStorage, slot: Int): AgnosticEnergyStorage? {
            val energyStorage = EnergyStorage.ITEM.find(
                origin.getItem(slot),
                ContainerItemContext.ofSingleSlot(
                    SlottedAgnosticItemStorageWrapper.of(origin).getSlot(slot),
                ),
            ) ?: return null
            return EnergyStorageWrapper(energyStorage)
        }
    }

    override fun run() {
        AgnosticEnergyStorageLookup.addBlockLookup(::extractEnergyStorage)
        AgnosticEnergyStorageLookup.addInventoryItemLookup(::extractEnergyStorage)
    }
}
