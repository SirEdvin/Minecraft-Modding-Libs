package site.siredvin.broccolium.modules.platform

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.client.Minecraft
import net.minecraft.core.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
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
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.energy.EnergyUnit
import java.util.UUID
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate

object FabricPlatformToolkit : InnerPlatformToolkit {
    const val FORGE_COMPACT_DEVIDER = 81
    override val fluidCompactDivider: Int
        get() = FORGE_COMPACT_DEVIDER
    override val commonEnergy: EnergyUnit
        get() = Energies.REDSTONE_FLUX
    override var minecraftServer: MinecraftServer? = null

    override fun <T : Any> wrap(registry: ResourceKey<Registry<T>>): RegistryWrapper<T> {
        @Suppress("UNCHECKED_CAST")
        val targetRegistry = (BuiltInRegistries.REGISTRY.getValue(registry.identifier()) ?: throw IllegalArgumentException("Cannot find registry $registry")) as Registry<T>
        return FabricRegistryWrapper(registry.identifier(), targetRegistry)
    }

    override fun <T : Any> lookup(registry: ResourceKey<Registry<T>>): HolderLookup.RegistryLookup<T> = registries!!.lookupOrThrow(registry)

    override fun isBlockProtected(pos: BlockPos, state: BlockState, player: ServerPlayer): Boolean {
        val level = player.level()
        if (level.server.isUnderSpawnProtection(level, pos, player)) return true
        return !PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, state, null)
    }

    override fun interactWithEntity(player: ServerPlayer, hand: InteractionHand, entity: Entity, hit: EntityHitResult): InteractionResult {
        val fabricInteraction = UseEntityCallback.EVENT.invoker().interact(player, entity.level(), hand, entity, hit)
        if (fabricInteraction.consumesAction()) return fabricInteraction
        // Native interactOn now owns the hit-position interaction and item fallback.
        return player.interactOn(entity, hand, hit.location.subtract(entity.position()))
    }

    override fun useOn(player: ServerPlayer, stack: ItemStack, hit: BlockHitResult, canUseBlock: Predicate<BlockState>): InteractionResult {
        val result = UseBlockCallback.EVENT.invoker().interact(player, player.level(), InteractionHand.MAIN_HAND, hit)
        if (result != InteractionResult.PASS) return result
        val block = player.level().getBlockState(hit.blockPos)
        if (!block.isAir && canUseBlock.test(block)) {
            val useResult = block.useItemOn(stack, player.level(), player, InteractionHand.MAIN_HAND, hit)
            if (useResult.consumesAction()) return useResult
        }
        return stack.useOn(UseOnContext(player, InteractionHand.MAIN_HAND, hit))
    }

    override fun setChunkForceLoad(level: ServerLevel, modID: String, owner: UUID, chunkPos: ChunkPos, add: Boolean, ticking: Boolean): Boolean = level.setChunkForced(chunkPos.x, chunkPos.z, add)

    override fun <T : Entity> createEntityType(name: Identifier, factory: Function<Level, T>): EntityType<T> = EntityType.Builder.of({ _, level -> factory.apply(level) }, MobCategory.MISC).build(ResourceKey.create(Registries.ENTITY_TYPE, name))

    override fun <T : BlockEntity> createBlockEntityType(factory: BiFunction<BlockPos, BlockState, T>, block: Block): BlockEntityType<T> = FabricBlockEntityTypeBuilder.create({ pos, state -> factory.apply(pos, state) }, block).build()

    override fun createTabBuilder(): CreativeModeTab.Builder = FabricCreativeModeTab.builder()

    override fun triggerRenderUpdate(blockEntity: BlockEntity) {
        val level = blockEntity.level ?: return
        if (level.isClientSide) {
            val pos = blockEntity.blockPos
            Minecraft.getInstance().levelRenderer.setBlocksDirty(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z)
        }
    }

    override fun openMenu(player: Player, owner: MenuProvider, savingFunction: SavingFunction) {
        (player as ServerPlayer).openMenu(object : ExtendedMenuProvider<ByteArray> {
            override fun createMenu(id: Int, inventory: Inventory, player: Player) = owner.createMenu(id, inventory, player)
            override fun getDisplayName() = owner.displayName
            override fun getScreenOpeningData(player: ServerPlayer): ByteArray = FabricMenuOpeningData.encode(player.registryAccess(), savingFunction)
        })
    }
}
