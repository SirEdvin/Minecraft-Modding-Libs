package site.siredvin.broccolium.modules.platform.api

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import site.siredvin.broccolium.modules.storage.energy.EnergyUnit
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate

interface InnerPlatformToolkit {
    val fluidCompactDivider: Int
    val commonEnergy: EnergyUnit
    val minecraftServer: MinecraftServer?

    val registries: HolderLookup.Provider?
        get() = minecraftServer?.registryAccess()

    fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T>

    fun <T> lookup(registry: ResourceKey<Registry<T>>): HolderLookup.RegistryLookup<T>

    fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean

    fun interactWithEntity(player: ServerPlayer, hand: InteractionHand, entity: Entity, hit: EntityHitResult): InteractionResult

    fun useOn(player: ServerPlayer, stack: ItemStack, hit: BlockHitResult, canUseBlock: Predicate<BlockState>): InteractionResult

    fun setChunkForceLoad(level: ServerLevel, modID: String, owner: UUID, chunkPos: ChunkPos, add: Boolean, ticking: Boolean = true): Boolean

    fun <T : BlockEntity> createBlockEntityType(
        factory: BiFunction<BlockPos, BlockState, T>,
        block: Block,
    ): BlockEntityType<T>

    fun <T : Entity> createEntityType(
        name: ResourceLocation,
        factory: Function<Level, T>,
    ): EntityType<T>

    fun createTabBuilder(): CreativeModeTab.Builder

    fun triggerRenderUpdate(blockEntity: BlockEntity)

    fun openMenu(player: Player, owner: MenuProvider, savingFunction: SavingFunction)
}
