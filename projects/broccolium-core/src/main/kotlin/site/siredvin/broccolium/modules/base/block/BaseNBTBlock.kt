package site.siredvin.broccolium.modules.base.block

import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.BlockItemStateProperties
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import site.siredvin.broccolium.modules.base.api.ISyncingBlockEntity
import site.siredvin.broccolium.modules.base.util.BlockUtil

abstract class BaseNBTBlock<T>(
    belongToTickingEntity: Boolean,
    properties: Properties = BlockUtil.defaultProperties(),
) : BaseBlockEntityBlock<T>(belongToTickingEntity, properties) where T : BlockEntity, T : ISyncingBlockEntity {
    abstract fun createItemStack(): ItemStack

    open fun prepareItemStack(blockEntity: ISyncingBlockEntity, state: BlockState): ItemStack {
        val stack: ItemStack = createItemStack()
        val internalData = blockEntity.saveInternalData(CompoundTag())
        if (!internalData.isEmpty) {
            stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(internalData))
        }
        val savableProperties: List<Property<*>> = savableProperties
        if (savableProperties.isNotEmpty() && !defaultBlockState().equals(state)) {
            val value = state.properties.fold(BlockItemStateProperties.EMPTY) { acc, property ->
                acc.with(
                    property,
                    state,
                )
            }

            stack.set(DataComponents.BLOCK_STATE, value)
        }
        return stack
    }

    open val savableProperties: List<Property<*>>
        get() = emptyList()

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is ISyncingBlockEntity) {
            if (!level.isClientSide && !player.isCreative) {
                val stack = prepareItemStack(blockEntity, state)
                val itemDrop = ItemEntity(
                    level,
                    pos.x.toDouble() + 0.5,
                    pos.y.toDouble() + 0.5,
                    pos.z.toDouble() + 0.5,
                    stack,
                )
                itemDrop.setDefaultPickUpDelay()
                level.addFreshEntity(itemDrop)
            }
        }
        return super.playerWillDestroy(level, pos, state, player)
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun setPlacedBy(level: Level, pos: BlockPos, initialState: BlockState, entity: LivingEntity?, stack: ItemStack) {
        var state = initialState
        super.setPlacedBy(level, pos, state, entity, stack)
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is ISyncingBlockEntity) {
            if (!level.isClientSide) {
                stack.components
                if (stack.components.has(DataComponents.BLOCK_STATE)) {
                    val savedState: BlockState = stack.components.get(
                        DataComponents.BLOCK_STATE,
                    )!!.apply(this.defaultBlockState())
                    for (property in savableProperties) {
                        @Suppress("UNCHECKED_CAST")
                        property as Property<Comparable<Any>>
                        state = state.setValue(property, savedState.getValue(property) as Comparable<Any>)
                    }
                }
                if (stack.components.has(DataComponents.BLOCK_ENTITY_DATA)) {
                    state = blockEntity.loadInternalData(stack.components.get(DataComponents.BLOCK_ENTITY_DATA)!!.copyTag(), state)
                    blockEntity.pushInternalDataChangeToClient(state)
                }
            }
        }
    }
}
