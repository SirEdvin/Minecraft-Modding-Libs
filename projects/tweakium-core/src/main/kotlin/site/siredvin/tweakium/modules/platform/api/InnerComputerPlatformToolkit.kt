package site.siredvin.tweakium.modules.platform.api

import com.mojang.authlib.GameProfile
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeData
import dan200.computercraft.shared.util.NBTUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.base.util.DataComponentUtil
import site.siredvin.tweakium.modules.platform.ComputerPlatformRegistries
import java.util.*

interface InnerComputerPlatformToolkit {
    fun createFakePlayer(level: ServerLevel, profile: GameProfile): ServerPlayer

    fun getTurtleAccess(entity: BlockEntity): ITurtleAccess?

    fun getPeripheral(level: ServerLevel, pos: BlockPos, side: Direction): IPeripheral?

    fun nbtHash(tag: Tag?): String?
    fun nbtHash(component: DataComponentPatch?): String? = nbtHash(DataComponentUtil.patchToNBT(component))

    fun getTurtleUpgrade(registries: HolderLookup.Provider, stack: ItemStack): UpgradeData<ITurtleUpgrade>?

    fun getPocketUpgrade(registries: HolderLookup.Provider, stack: ItemStack): UpgradeData<IPocketUpgrade>?

    fun getTurtleUpgrade(key: String): Optional<Holder.Reference<ITurtleUpgrade>> = ComputerPlatformRegistries.TURTLE_UPGRADES.get(
        ResourceKey.create(ITurtleUpgrade.REGISTRY, ResourceLocation.parse(key)),
    )

    fun getPocketUpgrade(key: String): Optional<Holder.Reference<IPocketUpgrade>> = ComputerPlatformRegistries.POCKET_UPGRADES.get(
        ResourceKey.create(IPocketUpgrade.REGISTRY, ResourceLocation.parse(key)),
    )

    fun nbtToLua(tag: Tag): Any?

    fun componentToLua(component: DataComponentPatch): Any? {
        val rawNBT = DataComponentPatch.CODEC.encodeStart(
            NbtOps.INSTANCE,
            component,
        ).result()
        if (rawNBT.isEmpty) return null
        return NBTUtil.toLua(rawNBT.get())
    }

    fun createTurtlesWithUpgrade(upgrade: UpgradeData<ITurtleUpgrade>): List<ItemStack>
    fun createPocketsWithUpgrade(upgrade: UpgradeData<IPocketUpgrade>): List<ItemStack>
    fun registerGenericPeripheralLookup()
}
