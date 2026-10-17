package site.siredvin.broccolium.testmod

import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.Containers
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.redstone.Orientation
import site.siredvin.broccolium.modules.base.api.IObservingBlockEntity
import site.siredvin.broccolium.modules.base.block.GenericBlockEntityBlock
import site.siredvin.broccolium.modules.base.blockentity.MutableNBTBlockEntity
import site.siredvin.broccolium.modules.platform.BasePlatform
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.broccolium.modules.platform.api.RegistryEntry

object NativeFixtures {
    const val MOD_ID = "broccolium_testmod"
    lateinit var block: RegistryEntry<GenericBlockEntityBlock<ObservedEntity>>
    lateinit var type: RegistryEntry<BlockEntityType<ObservedEntity>>
    lateinit var tooltipItem: RegistryEntry<site.siredvin.broccolium.modules.base.item.HiddenDescriptiveItemItem>
    var tooltipCalls = 0
    var tooltipEnabled = true
    var typeResolutions = 0

    fun register(platform: BasePlatform) {
        val itemId = Identifier.fromNamespaceAndPath(MOD_ID, "tooltip")
        tooltipItem = platform.registerItem("tooltip", {
            site.siredvin.broccolium.modules.base.item.HiddenDescriptiveItemItem(
                net.minecraft.world.item.Item.Properties().setId(ResourceKey.create(Registries.ITEM, itemId)),
                { tooltipEnabled }, true,
                java.util.function.Function { tooltipCalls++; listOf(net.minecraft.network.chat.Component.literal("first"), net.minecraft.network.chat.Component.literal("second")) },
            )
        })
        val id = Identifier.fromNamespaceAndPath(MOD_ID, "observer")
        block = platform.registerBlock("observer", {
            GenericBlockEntityBlock({ typeResolutions++; type }, true, true, BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id)))
        })
        type = platform.registerBlockEntity("observer", {
            PlatformToolkit.get().createBlockEntityType(::ObservedEntity, block.get())
        })
    }
}

class ObservedEntity(pos: BlockPos, state: BlockState) : MutableNBTBlockEntity(NativeFixtures.type.get(), pos, state), IObservingBlockEntity {
    val cargo = SimpleContainer(ItemStack(Items.DIAMOND, 4))
    val neighbours = mutableListOf<Pair<Block, Orientation?>>()
    var destroys = 0
    var removedBeforeDestroy = false
    var ticks = 0
    var payload = 0

    override fun onNeighbourChange(neighbourBlock: Block, orientation: Orientation?) {
        neighbours.add(neighbourBlock to orientation)
    }

    override fun blockTick() { ticks++ }

    override fun destroy() {
        destroys++
        removedBeforeDestroy = isRemoved && level?.getBlockEntity(blockPos) == null
    }

    override fun preRemoveSideEffects(pos: BlockPos, state: BlockState) {
        super.preRemoveSideEffects(pos, state)
        Containers.dropContents(checkNotNull(level), pos, cargo)
    }

    override fun saveInternalData(data: CompoundTag): CompoundTag = data.apply { putInt("fixture_payload", payload) }
    override fun loadInternalData(data: CompoundTag, state: BlockState?): BlockState {
        payload = data.getIntOr("fixture_payload", 0)
        return state ?: blockState
    }
}
