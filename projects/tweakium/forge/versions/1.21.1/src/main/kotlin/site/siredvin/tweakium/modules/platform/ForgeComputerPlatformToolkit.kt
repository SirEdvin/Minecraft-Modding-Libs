package site.siredvin.tweakium.modules.platform

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.peripheral.PeripheralCapability
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeData
import dan200.computercraft.impl.Peripherals
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
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralProvider
import site.siredvin.tweakium.modules.platform.api.InnerComputerPlatformToolkit
import site.siredvin.tweakium.modules.player.ForgeFakePlayer
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_CONTEXT

@Suppress("UnstableApiUsage")
object ForgeComputerPlatformToolkit : InnerComputerPlatformToolkit {
    private var genericRegistered: Boolean = false

    override fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer = ForgeFakePlayer(level, profile)

    override fun getTurtleAccess(entity: BlockEntity): ITurtleAccess? {
        if (entity is TurtleBlockEntity) {
            return entity.access
        }
        return null
    }

    override fun getPeripheral(level: ServerLevel, pos: BlockPos, side: Direction): IPeripheral? = level.getCapability(PeripheralCapability.get(), pos, side)
        ?: Peripherals.getGenericPeripheral(level, pos, side, level.getBlockEntity(pos))

    override fun nbtHash(tag: Tag?): String? = NBTUtil.getNBTHash(tag)

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
        if (!genericRegistered) {
            MOD_CONTEXT.getKEventBus().addListener { event: RegisterCapabilitiesEvent ->
                for (type in BuiltInRegistries.BLOCK_ENTITY_TYPE) {
                    event.registerBlockEntity(PeripheralCapability.get(), type) { be, side ->
                        if (be is IPeripheralProvider<*> && side != null) {
                            be.getPeripheral(side)
                        } else {
                            null
                        }
                    }
                }
            }
            genericRegistered = true
        }
    }
}
