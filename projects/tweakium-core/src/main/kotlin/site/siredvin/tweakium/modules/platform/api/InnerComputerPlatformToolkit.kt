package site.siredvin.tweakium.modules.platform.api

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeData
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity

interface InnerComputerPlatformToolkit {
    fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer

    fun getTurtleAccess(entity: BlockEntity): ITurtleAccess?

    fun getPeripheral(level: ServerLevel, pos: BlockPos, side: Direction): IPeripheral?

    fun nbtHash(tag: CompoundTag?): String?

    fun getTurtleUpgrade(stack: ItemStack): UpgradeData<ITurtleUpgrade>?

    fun getPocketUpgrade(stack: ItemStack): UpgradeData<IPocketUpgrade>?

    fun getTurtleUpgrade(key: String): ITurtleUpgrade?

    fun getPocketUpgrade(key: String): IPocketUpgrade?

    fun nbtToLua(tag: Tag): Any?

    fun createTurtlesWithUpgrade(upgrade: UpgradeData<ITurtleUpgrade>): List<ItemStack>
    fun createPocketsWithUpgrade(upgrade: UpgradeData<IPocketUpgrade>): List<ItemStack>
    fun registerGenericPeripheralLookup()
}