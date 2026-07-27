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
import dan200.computercraft.shared.util.NBTUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralProvider
import site.siredvin.tweakium.modules.platform.api.InnerComputerPlatformToolkit
import site.siredvin.tweakium.modules.player.FabricFakePlayer

object FabricComputerPlatformToolkit : InnerComputerPlatformToolkit {

    private var genericRegistered: Boolean = false

    override fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer = FabricFakePlayer.create(level, profile)

    override fun getTurtleAccess(entity: BlockEntity): ITurtleAccess? {
        if (entity is TurtleBlockEntity) {
            return entity.access
        }
        return null
    }

    override fun getPeripheral(level: ServerLevel, pos: BlockPos, side: Direction): IPeripheral? = PeripheralLookup.get().find(level, pos, side)

    override fun nbtHash(tag: CompoundTag?): String? = NBTUtil.getNBTHash(tag)

    override fun getTurtleUpgrade(stack: ItemStack): UpgradeData<ITurtleUpgrade>? = TurtleUpgrades.instance().get(stack)

    override fun getPocketUpgrade(stack: ItemStack): UpgradeData<IPocketUpgrade>? = PocketUpgrades.instance().get(stack)

    override fun getTurtleUpgrade(key: String): ITurtleUpgrade? = TurtleUpgrades.instance().get(key)

    override fun getPocketUpgrade(key: String): IPocketUpgrade? = PocketUpgrades.instance().get(key)

    override fun nbtToLua(tag: Tag): Any? = NBTUtil.toLua(tag)

    override fun createTurtlesWithUpgrade(upgrade: UpgradeData<ITurtleUpgrade>): List<ItemStack> = listOf(
        ModRegistry.Items.TURTLE_NORMAL.get().create(-1, null, -1, null, upgrade, 0, null),
        ModRegistry.Items.TURTLE_ADVANCED.get().create(-1, null, -1, null, upgrade, 0, null),
    )

    override fun createPocketsWithUpgrade(upgrade: UpgradeData<IPocketUpgrade>): List<ItemStack> = listOf(
        ModRegistry.Items.POCKET_COMPUTER_NORMAL.get().create(-1, null, -1, upgrade),
        ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get().create(-1, null, -1, upgrade),
    )

    override fun registerGenericPeripheralLookup() {
        if (!genericRegistered) {
            PeripheralLookup.get().registerFallback { _, _, _, blockEntity, context ->
                if (blockEntity is IPeripheralProvider<*>) {
                    return@registerFallback blockEntity.getPeripheral(context)
                }
                return@registerFallback null
            }
            genericRegistered = true
        }
    }
}
