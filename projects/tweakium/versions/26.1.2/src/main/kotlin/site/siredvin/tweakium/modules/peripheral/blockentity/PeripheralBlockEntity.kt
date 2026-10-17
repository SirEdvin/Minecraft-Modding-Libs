package site.siredvin.tweakium.modules.peripheral.blockentity

import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import site.siredvin.broccolium.modules.base.api.IOwnedBlockEntity
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralBlockEntity
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralProvider
import java.util.UUID

abstract class PeripheralBlockEntity<T : IOwnedPeripheral<*>>(
    blockEntityType: BlockEntityType<*>,
    blockPos: BlockPos,
    blockState: BlockState,
) : BlockEntity(blockEntityType, blockPos, blockState),
    IPeripheralBlockEntity,
    IPeripheralProvider<T>,
    IOwnedBlockEntity {
    final override var peripheralSettings: CompoundTag = CompoundTag()
        protected set

    protected var peripheral: T? = null
    override var ownerPlayerUUID: UUID? = null
    override var player: Player?
        get() = ownerPlayerUUID?.let { level?.getPlayerByUUID(it) }
        set(value) {
            ownerPlayerUUID = value?.uuid
        }

    val connectedComputers: List<IComputerAccess>
        get() = if (peripheral == null) emptyList() else peripheral!!.connectedComputers

    override fun getPeripheral(side: Direction): T? {
        ensurePeripheralCreated(side)
        return peripheral!!
    }

    fun ensurePeripheralCreated(side: Direction) {
        if (peripheral == null) {
            peripheral = createPeripheral(side)
        }
    }

    protected abstract fun createPeripheral(side: Direction): T

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        if (!peripheralSettings.isEmpty) {
            output.store(PERIPHERAL_DATA_TAG, CompoundTag.CODEC, peripheralSettings)
        }
        ownerPlayerUUID?.let { output.store(OWNER_PROFILE_TAG, UUIDUtil.CODEC, it) }
    }

    override fun loadAdditional(input: ValueInput) {
        input.read(PERIPHERAL_DATA_TAG, CompoundTag.CODEC).ifPresent { peripheralSettings = it }
        input.read(OWNER_PROFILE_TAG, UUIDUtil.CODEC).ifPresent { ownerPlayerUUID = it }
        super.loadAdditional(input)
    }

    override fun markSettingsChanged() {
        setChanged()
    }

    companion object {
        const val PERIPHERAL_DATA_TAG = "peripheralData"
        const val OWNER_PROFILE_TAG = "ownerProfile"
    }
}
