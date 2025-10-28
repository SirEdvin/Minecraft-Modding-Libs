package site.siredvin.tweakium.modules.peripheral.blockentity

import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
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
    // Peripheral logic
    final override var peripheralSettings: CompoundTag
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

    init {
        peripheralSettings = CompoundTag()
    }

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

    override fun saveAdditional(compound: CompoundTag, provider: Provider) {
        super.saveAdditional(compound, provider)
        if (!peripheralSettings.isEmpty) {
            compound.put(PERIPHERAL_DATA_TAG, peripheralSettings)
        }
        if (ownerPlayerUUID != null) {
            compound.putUUID(OWNER_PROFILE_TAG, ownerPlayerUUID!!)
        }
    }

    override fun loadAdditional(compound: CompoundTag, provider: Provider) {
        if (compound.contains(PERIPHERAL_DATA_TAG)) peripheralSettings = compound.getCompound(PERIPHERAL_DATA_TAG)
        if (compound.contains(OWNER_PROFILE_TAG)) {
            ownerPlayerUUID = compound.getUUID(OWNER_PROFILE_TAG)
        }
        super.loadAdditional(compound, provider)
    }

    override fun markSettingsChanged() {
        setChanged()
    }

    companion object {
        private const val PERIPHERAL_DATA_TAG = "peripheralData"
        private const val OWNER_PROFILE_TAG = "ownerProfile"
    }
}
