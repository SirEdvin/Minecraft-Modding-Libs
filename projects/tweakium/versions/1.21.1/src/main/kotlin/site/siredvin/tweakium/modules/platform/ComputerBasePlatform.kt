package site.siredvin.tweakium.modules.platform

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeBase
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.platform.BasePlatform
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import site.siredvin.broccolium.modules.platform.SimpleRegistryEntry
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import site.siredvin.tweakium.modules.data.ComputerModInformationHolder
import site.siredvin.tweakium.modules.platform.api.*
import java.util.function.BiFunction

abstract class ComputerBasePlatform : BasePlatform() {
    abstract override val baseInnerPlatform: InnerComputerBasePlatform
    abstract override val modInformationTracker: ComputerModInformationTracker

    @Suppress("UNCHECKED_CAST")
    override val holder: ComputerModInformationHolder
        get() = modInformationTracker

    private fun <T : UpgradeBase> createWithSelfCustomItem(factory: BiFunction<ItemStack, UpgradeType<T>, T>): UpgradeType<T> {
        lateinit var type: UpgradeType<T>
        type = UpgradeType.create(
            PlatformRegistries.ITEMS.byNameCodec()
                .xmap({ factory.apply(ItemStack(it), type) }, { it.craftingItem.item })
                .fieldOf("item"),
        )
        return type
    }

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        name: String,
        upgrade: UpgradeType<V>,
    ): RegistryEntry<UpgradeType<V>> = registerTurtleUpgrade(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), upgrade)

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        upgrade: UpgradeType<V>,
    ): RegistryEntry<UpgradeType<V>> {
        val result = SimpleRegistryEntry(key, baseInnerPlatform.registerTurtleUpgrade(key, upgrade))
        @Suppress("UNCHECKED_CAST")
        modInformationTracker.internalTurtleUpgrades.add(result as RegistryEntry<UpgradeType<out ITurtleUpgrade>>)
        return result
    }

    fun <V : IPocketUpgrade> registerPocketUpgrade(
        name: String,
        upgrade: UpgradeType<V>,
    ): RegistryEntry<UpgradeType<V>> = registerPocketUpgrade(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), upgrade)

    fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        upgrade: UpgradeType<V>,
    ): RegistryEntry<UpgradeType<V>> {
        val result = SimpleRegistryEntry(key, baseInnerPlatform.registerPocketUpgrade(key, upgrade))
        @Suppress("UNCHECKED_CAST")
        modInformationTracker.internalPocketUpgrades.add(result as RegistryEntry<UpgradeType<out IPocketUpgrade>>)
        return result
    }

    fun <V : ITurtleUpgrade> registerTurtleUpgradeWithSelfCustomItem(
        name: String,
        upgradeFactory: TurtleUpgradeFactory<V>,
    ): TurtleUpgradeTypeRegistryEntry<V> = registerTurtleUpgradeWithSelfCustomItem(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), upgradeFactory)

    fun <V : ITurtleUpgrade> registerTurtleUpgradeWithSelfCustomItem(
        id: ResourceLocation,
        upgradeFactory: TurtleUpgradeFactory<V>,
    ): TurtleUpgradeTypeRegistryEntry<V> {
        val upgradeType = createWithSelfCustomItem { itemStack, upgradeType ->
            upgradeFactory.build(
                id,
                upgradeType,
                itemStack,
            )
        }
        val registered = registerTurtleUpgrade(id, upgradeType)
        return TurtleUpgradeTypeRegistryEntry(upgradeFactory, registered)
    }

    fun <V : IPocketUpgrade> registerPocketUpgradeWithSelfCustomItem(
        name: String,
        upgradeFactory: PocketUpgradeFactory<V>,
    ): PocketUpgradeTypeRegistryEntry<V> = registerPocketUpgradeWithSelfCustomItem(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), upgradeFactory)

    fun <V : IPocketUpgrade> registerPocketUpgradeWithSelfCustomItem(
        id: ResourceLocation,
        upgradeFactory: PocketUpgradeFactory<V>,
    ): PocketUpgradeTypeRegistryEntry<V> {
        val upgradeType = createWithSelfCustomItem { itemStack, upgradeType ->
            upgradeFactory.build(
                id,
                upgradeType,
                itemStack,
            )
        }
        val registered = registerPocketUpgrade(id, upgradeType)
        return PocketUpgradeTypeRegistryEntry(upgradeFactory, registered)
    }

    fun <V : ITurtleUpgrade> registerTurtleUpgradeWithCustomItem(
        name: String,
        upgradeFactory: ReducedTurtleUpgradeFactory<V>,
    ): ReducedTurtleUpgradeTypeRegistryEntry<V> = registerTurtleUpgradeWithCustomItem(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), upgradeFactory)

    fun <V : ITurtleUpgrade> registerTurtleUpgradeWithCustomItem(
        id: ResourceLocation,
        upgradeFactory: ReducedTurtleUpgradeFactory<V>,
    ): ReducedTurtleUpgradeTypeRegistryEntry<V> {
        val upgradeType = UpgradeType.simpleWithCustomItem { upgradeFactory.build(id, it) }
        val registered = registerTurtleUpgrade(id, upgradeType)
        return ReducedTurtleUpgradeTypeRegistryEntry(upgradeFactory, registered)
    }

    fun <V : IPocketUpgrade> registerPocketUpgradeWithCustomItem(
        name: String,
        upgradeFactory: ReducedPocketUpgradeFactory<V>,
    ): ReducedPocketUpgradeTypeRegistryEntry<V> = registerPocketUpgradeWithCustomItem(ResourceLocation.fromNamespaceAndPath(baseInnerPlatform.modID, name), upgradeFactory)

    fun <V : IPocketUpgrade> registerPocketUpgradeWithCustomItem(
        id: ResourceLocation,
        upgradeFactory: ReducedPocketUpgradeFactory<V>,
    ): ReducedPocketUpgradeTypeRegistryEntry<V> {
        val upgradeType = UpgradeType.simpleWithCustomItem { upgradeFactory.build(id, it) }
        val registered = registerPocketUpgrade(id, upgradeType)
        return ReducedPocketUpgradeTypeRegistryEntry(upgradeFactory, registered)
    }
}
