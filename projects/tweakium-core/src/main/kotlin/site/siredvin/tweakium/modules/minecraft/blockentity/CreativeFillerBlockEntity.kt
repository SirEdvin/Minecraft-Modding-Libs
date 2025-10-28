package site.siredvin.tweakium.modules.minecraft.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.broccolium.modules.storage.energy.VoidEnergySink
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergySink
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergySinkProvider
import site.siredvin.broccolium.modules.storage.fluid.VoidFluidSink
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidSink
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidSinkProvider
import site.siredvin.broccolium.modules.storage.item.VoidItemSink
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemSink
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemSinkProvider
import site.siredvin.tweakium.modules.minecraft.computercraft.CreativeFillerPeripheral
import site.siredvin.tweakium.modules.minecraft.setup.TweakiumBlockEntityTypes
import site.siredvin.tweakium.modules.peripheral.blockentity.PeripheralBlockEntity
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner

class CreativeFillerBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    PeripheralBlockEntity<CreativeFillerPeripheral>(
        TweakiumBlockEntityTypes.CREATIVE_FILLER.get(),
        blockPos,
        blockState,
    ),
    AgnosticItemSinkProvider,
    AgnosticFluidSinkProvider,
    AgnosticEnergySinkProvider {

    override fun createPeripheral(side: Direction): CreativeFillerPeripheral = CreativeFillerPeripheral(BlockEntityPeripheralOwner(this))
    override val itemSink: AgnosticItemSink by lazy {
        VoidItemSink()
    }
    override val fluidSink: AgnosticFluidSink by lazy {
        VoidFluidSink()
    }
    override val energySink: AgnosticEnergySink by lazy {
        VoidEnergySink(PlatformToolkit.get().commonEnergy)
    }
}
