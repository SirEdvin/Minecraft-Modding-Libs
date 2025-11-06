package site.siredvin.tweakium.modules.plugins

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStorageLookup
import site.siredvin.broccolium.modules.storage.fluid.EmptyAgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage

open class DeferredFluidStoragePlugin(level: Level, private val pos: BlockPos, private val side: Direction, fluidStorageTransferLimit: Double) : AbstractFluidStoragePlugin(level, fluidStorageTransferLimit) {
    override val storage: AgnosticFluidStorage
        get() = AgnosticFluidStorageLookup.extractFromBlock(level, pos, level.getBlockEntity(pos), side) ?: EmptyAgnosticFluidStorage
}
