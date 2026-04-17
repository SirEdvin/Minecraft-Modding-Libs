package site.siredvin.broccolium.modules.platform

import net.minecraft.client.Minecraft
import net.minecraft.core.*
import net.minecraft.core.registries.BuiltInRegistries
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
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.neoforged.neoforge.common.CommonHooks
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.common.util.TriState
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks
import site.siredvin.broccolium.modules.platform.api.InnerPlatformToolkit
import site.siredvin.broccolium.modules.platform.api.RegistryWrapper
import site.siredvin.broccolium.modules.platform.api.SavingFunction
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.energy.EnergyUnit
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate

@Suppress("UnstableApiUsage")
object ForgeInnerPlatformToolkit : InnerPlatformToolkit {

    override val fluidCompactDivider: Int
        get() = 1
    override val commonEnergy: EnergyUnit
        get() = Energies.FORGE

    override val minecraftServer: MinecraftServer?
        get() = ServerLifecycleHooks.getCurrentServer()

    override fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T> {
        @Suppress("UNCHECKED_CAST")
        val targetRegistry: Registry<T> = (BuiltInRegistries.REGISTRY.get(registry.location()) ?: throw IllegalArgumentException("Cannot find registry $registry")) as Registry<T>
        return ForgeRegistryWrapper(registry.location(), targetRegistry)
    }

    override fun <T> lookup(registry: ResourceKey<Registry<T>>): HolderLookup.RegistryLookup<T> = registries!!.lookupOrThrow(registry)

    override fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean {
        if (player.server.isUnderSpawnProtection(player.serverLevel(), pos, player)) {
            return true
        }
        val event = BlockEvent.BreakEvent(player.level(), pos, state, player)
        NeoForge.EVENT_BUS.post(event)
        return event.isCanceled
    }

    override fun interactWithEntity(
        player: ServerPlayer,
        hand: InteractionHand,
        entity: Entity,
        hit: EntityHitResult,
    ): InteractionResult {
        // Copied from CC:T to have nearly same logic here :)
        // Our behaviour is slightly different here - we call onInteractEntityAt before the interact methods, while
        // NeoForge does the call afterward (on the server, not on the client).
        var interactAt = CommonHooks.onInteractEntityAt(player, entity, hit.location, hand)
        if (interactAt == null) {
            interactAt = entity.interactAt(player, hit.location.subtract(entity.position()), InteractionHand.MAIN_HAND)
        }

        if (interactAt.consumesAction()) {
            return interactAt
        }

        return player.interactOn(entity, hand)
    }

    override fun useOn(
        player: ServerPlayer,
        stack: ItemStack,
        hit: BlockHitResult,
        canUseBlock: Predicate<BlockState>,
    ): InteractionResult {
        val level = player.level()
        val pos = hit.blockPos
        val event = CommonHooks.onRightClickBlock(player, InteractionHand.MAIN_HAND, pos, hit)
        if (event.isCanceled) return event.cancellationResult

        val context = UseOnContext(player, InteractionHand.MAIN_HAND, hit)
        if (event.useItem != TriState.FALSE) {
            val result = stack.onItemUseFirst(context)
            if (result != InteractionResult.PASS) return result
        }

        val block = level.getBlockState(hit.blockPos)
        if (event.useBlock != TriState.FALSE && !block.isAir && canUseBlock.test(block)) {
            val useResult = block.useItemOn(stack, level, player, InteractionHand.MAIN_HAND, hit)
            if (useResult.consumesAction()) return useResult.result()
        }

        return if (event.useItem == TriState.FALSE) InteractionResult.PASS else stack.useOn(context)
    }

    override fun setChunkForceLoad(level: ServerLevel, modID: String, owner: UUID, chunkPos: ChunkPos, add: Boolean, ticking: Boolean): Boolean = level.setChunkForced(chunkPos.x, chunkPos.z, add)

    override fun <T : BlockEntity> createBlockEntityType(
        factory: BiFunction<BlockPos, BlockState, T>,
        block: Block,
    ): BlockEntityType<T> = BlockEntityType.Builder.of({ t: BlockPos, u: BlockState ->
        factory.apply(t, u)
    }, block).build(null as com.mojang.datafixers.types.Type<*>)

    override fun <T : Entity> createEntityType(
        name: ResourceLocation,
        factory: Function<Level, T>,
    ): EntityType<T> = EntityType.Builder.of({ _, level -> factory.apply(level) }, MobCategory.MISC).build(name.toString())

    override fun createTabBuilder(): CreativeModeTab.Builder = CreativeModeTab.builder()

    override fun triggerRenderUpdate(blockEntity: BlockEntity) {
        val level = blockEntity.level!!
        if (level.isClientSide) {
            val pos = blockEntity.blockPos
            // Basically, just world.setBlocksDirty with bypass model block state check
            Minecraft.getInstance().levelRenderer.setBlocksDirty(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z)
            blockEntity.requestModelDataUpdate()
        }
    }

    override fun openMenu(player: Player, owner: MenuProvider, savingFunction: SavingFunction) {
        (player as ServerPlayer).openMenu(owner) { buf -> savingFunction.toBytes(buf) }
    }
}
