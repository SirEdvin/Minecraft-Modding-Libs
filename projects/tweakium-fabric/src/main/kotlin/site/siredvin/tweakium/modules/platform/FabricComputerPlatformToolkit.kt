package site.siredvin.tweakium.modules.platform

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.peripheral.PeripheralLookup
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeData
import dan200.computercraft.impl.PocketUpgrades
import dan200.computercraft.impl.TurtleUpgrades
import dan200.computercraft.shared.ModRegistry
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity
import dan200.computercraft.shared.util.DataComponentUtil
import dan200.computercraft.shared.util.NBTUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralProvider
import site.siredvin.tweakium.modules.platform.api.InnerComputerPlatformToolkit
import site.siredvin.tweakium.modules.player.FabricFakePlayer

object FabricComputerPlatformToolkit : InnerComputerPlatformToolkit {

    override fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer = FabricFakePlayer.create(level, profile)

    override fun getTurtleAccess(entity: BlockEntity): ITurtleAccess? {
        if (entity is TurtleBlockEntity) {
            return entity.access
        }
        return null
    }

    override fun getPeripheral(level: ServerLevel, pos: BlockPos, side: Direction): IPeripheral? = PeripheralLookup.get().find(level, pos, side)

    override fun nbtHash(tag: CompoundTag?): String? = NBTUtil.getNBTHash(tag)

    override fun nbtHash(component: DataComponentPatch?): String? {
        if (component == null) return null
        return NBTUtil.getNBTHash(
            DataComponentPatch.CODEC.encodeStart(
                NbtOps.INSTANCE,
                component,
            ).result().orElse(null),
        )
    }

    override fun getTurtleUpgrade(registries: HolderLookup.Provider, stack: ItemStack): UpgradeData<ITurtleUpgrade>? = TurtleUpgrades.instance().get(registries, stack)

    override fun getPocketUpgrade(registries: HolderLookup.Provider, stack: ItemStack): UpgradeData<IPocketUpgrade>? = PocketUpgrades.instance().get(registries, stack)

    override fun getTurtleUpgrade(key: String): ITurtleUpgrade? = ComputerPlatformRegistries.TURTLE_UPGRADES.get(
        ResourceLocation.parse(key),
    )

    override fun getPocketUpgrade(key: String): IPocketUpgrade? = ComputerPlatformRegistries.POCKET_UPGRADES.get(
        ResourceLocation.parse(key),
    )

    override fun nbtToLua(tag: Tag): Any? = NBTUtil.toLua(tag)

    override fun createTurtlesWithUpgrade(upgrade: UpgradeData<ITurtleUpgrade>): List<ItemStack> = listOf(
        DataComponentUtil.createStack(ModRegistry.Items.TURTLE_NORMAL.get(), ModRegistry.DataComponents.RIGHT_TURTLE_UPGRADE.get(), upgrade),
        DataComponentUtil.createStack(ModRegistry.Items.TURTLE_ADVANCED.get(), ModRegistry.DataComponents.RIGHT_TURTLE_UPGRADE.get(), upgrade),
    )

    override fun createPocketsWithUpgrade(upgrade: UpgradeData<IPocketUpgrade>): List<ItemStack> = listOf(
        DataComponentUtil.createStack(ModRegistry.Items.POCKET_COMPUTER_NORMAL.get(), ModRegistry.DataComponents.POCKET_UPGRADE.get(), upgrade),
        DataComponentUtil.createStack(ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get(), ModRegistry.DataComponents.POCKET_UPGRADE.get(), upgrade),
    )

    override fun registerGenericPeripheralLookup() {
        PeripheralLookup.get().registerFallback { _, _, _, blockEntity, context ->
            if (blockEntity is IPeripheralProvider<*>) {
                return@registerFallback blockEntity.getPeripheral(context)
            }
            return@registerFallback null
        }
    }
}
