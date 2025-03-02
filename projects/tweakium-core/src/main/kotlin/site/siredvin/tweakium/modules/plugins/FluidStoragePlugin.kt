package site.siredvin.tweakium.modules.plugins

import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage

open class FluidStoragePlugin(level: Level, override val storage: AgnosticFluidStorage, fluidStorageTransferLimit: Int) : AbstractFluidStoragePlugin(level, fluidStorageTransferLimit)
