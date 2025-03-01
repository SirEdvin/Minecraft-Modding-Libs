package site.siredvin.broccolium.modules.platform

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.client.Minecraft
import net.minecraft.core.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
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
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
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
import site.siredvin.broccolium.modules.platform.api.InnerPlatformToolkit
import site.siredvin.broccolium.modules.platform.api.RegistryWrapper
import site.siredvin.broccolium.modules.platform.api.SavingFunction
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate

object FabricPlatformToolkit : InnerPlatformToolkit {
    const val FORGE_COMPACT_DEVIDER = 81
    private var minecraftServerCache: MinecraftServer? = null

    override val fluidCompactDivider: Int
        get() = FORGE_COMPACT_DEVIDER

    override var minecraftServer: MinecraftServer?
        get() = minecraftServerCache
        set(value) {
            minecraftServerCache = value
        }

    override fun <T> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T> {
        @Suppress("UNCHECKED_CAST")
        val targetRegistry: Registry<T> = (BuiltInRegistries.REGISTRY.get(registry.location()) ?: throw IllegalArgumentException("Cannot find registry $registry")) as Registry<T>
        return FabricRegistryWrapper(registry.location(), targetRegistry)
    }

    override fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean {
        if (player.server.isUnderSpawnProtection(player.serverLevel(), pos, player)) {
            return true
        }
        return !PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(player.level(), player, pos, state, null)
    }

    override fun interactWithEntity(player: ServerPlayer, hand: InteractionHand, entity: Entity, hit: EntityHitResult): InteractionResult {
        val fabricInteraction = UseEntityCallback.EVENT.invoker().interact(player, entity.level(), InteractionHand.MAIN_HAND, entity, hit)
        if (fabricInteraction.consumesAction()) {
            return fabricInteraction
        }
        val entityInteraction = entity.interactAt(player, hit.location.subtract(entity.position()), InteractionHand.MAIN_HAND)
        if (entityInteraction.consumesAction()) {
            return entityInteraction
        }
        return player.interactOn(entity, hand)
    }

    override fun useOn(
        player: ServerPlayer,
        stack: ItemStack,
        hit: BlockHitResult,
        canUseBlock: Predicate<BlockState>,
    ): InteractionResult {
        val result = UseBlockCallback.EVENT.invoker().interact(player, player.level(), InteractionHand.MAIN_HAND, hit)
        if (result != InteractionResult.PASS) return result
        val block = player.level().getBlockState(hit.blockPos)
        if (!block.isAir && canUseBlock.test(block)) {
            val useResult = block.use(player.level(), player, InteractionHand.MAIN_HAND, hit)
            if (useResult.consumesAction()) return useResult
        }
        return stack.useOn(UseOnContext(player, InteractionHand.MAIN_HAND, hit))
    }

    override fun setChunkForceLoad(level: ServerLevel, modID: String, owner: UUID, chunkPos: ChunkPos, add: Boolean, ticking: Boolean): Boolean = level.setChunkForced(chunkPos.x, chunkPos.z, add)

    override fun <T : Entity> createEntityType(
        name: ResourceLocation,
        factory: Function<Level, T>,
    ): EntityType<T> = FabricEntityTypeBuilder.create(MobCategory.MISC) { _, level -> factory.apply(level) }.build()

    override fun <T : BlockEntity> createBlockEntityType(
        factory: BiFunction<BlockPos, BlockState, T>,
        block: Block,
    ): BlockEntityType<T> = FabricBlockEntityTypeBuilder.create({ t: BlockPos, u: BlockState ->
        factory.apply(t, u)
    }).addBlock(block).build()

    override fun createTabBuilder(): CreativeModeTab.Builder = FabricItemGroup.builder()

    override fun triggerRenderUpdate(blockEntity: BlockEntity) {
        val level = blockEntity.level!!
        if (level.isClientSide) {
            val pos = blockEntity.blockPos
            // Basically, just world.setBlocksDirty with bypass model block state check
            Minecraft.getInstance().levelRenderer.setBlocksDirty(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z)
        }
    }

    override fun openMenu(player: Player, owner: MenuProvider, savingFunction: SavingFunction) {
        player.openMenu(WrappedMenuProvider(owner, savingFunction))
    }

    @JvmRecord
    private data class WrappedMenuProvider(val owner: MenuProvider, val savingFunction: SavingFunction) : ExtendedScreenHandlerFactory {
        override fun createMenu(id: Int, inventory: Inventory, player: Player): AbstractContainerMenu? = owner.createMenu(id, inventory, player)

        override fun getDisplayName(): Component = owner.displayName

        override fun writeScreenOpeningData(player: ServerPlayer, buf: FriendlyByteBuf) {
            savingFunction.toBytes(buf)
        }
    }
}
