package site.siredvin.tweakium.modules.peripheral.boon

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import site.siredvin.broccolium.modules.base.util.world.ScanUtils
import site.siredvin.tweakium.modules.operation.SphereOperationContext
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOperation
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwnerBoon
import site.siredvin.tweakium.modules.peripheral.representation.LuaRepresentation
import site.siredvin.tweakium.modules.peripheral.util.assertBetween
import site.siredvin.tweakium.modules.plugins.PeripheralPluginUtils
import java.util.function.BiConsumer
import java.util.function.Predicate
import kotlin.math.min

class ScanningBoon<T : IPeripheralOwner>(val owner: T, val maxRadius: Int) : IPeripheralOwnerBoon {
    abstract class ScanningMethod<T : IPeripheralOwner>(val name: String, val operation: IPeripheralOperation<SphereOperationContext>) {
        abstract fun scan(ability: ScanningBoon<T>, radius: Int, filter: Any?): MethodResult
    }

    class BlockScanningMethod<T : IPeripheralOwner>(operation: IPeripheralOperation<SphereOperationContext>, private val enriches: Array<out BiConsumer<BlockState, MutableMap<String, Any>>>) : ScanningMethod<T>("block", operation) {
        private fun blockStateConverter(state: BlockState, pos: BlockPos, facing: Direction, center: BlockPos, level: Level): MutableMap<String, Any> {
            val base = LuaRepresentation.withPos(state, pos, facing, center) { data ->
                LuaRepresentation.forBlockV2(
                    level,
                    pos,
                )
            }
            enriches.forEach { it.accept(state, base) }
            return base
        }
        override fun scan(ability: ScanningBoon<T>, radius: Int, filter: Any?): MethodResult {
            val result = mutableListOf<MutableMap<String, Any>>()
            val predicate = if (filter != null) {
                PeripheralPluginUtils.blockQueryToPredicate(filter)
            } else {
                Predicate<BlockState> { !it.isAir }
            }
            val level = ability.owner.level ?: return MethodResult.of(emptyMap<String, Any>())
            ScanUtils.traverseBlocks(
                level,
                ability.owner.pos,
                min(radius, ability.maxRadius),
                { state, pos -> result.add(blockStateConverter(state, pos, ability.owner.facing, ability.owner.pos, level)) },
                relativePosition = false,
                predicate = predicate,
            )
            return MethodResult.of(result)
        }
    }

    abstract class EntityScanningMethod<T : IPeripheralOwner, V : Entity>(
        name: String,
        operation: IPeripheralOperation<SphereOperationContext>,
        private val entityClass: Class<V>,
        private val predicate: Predicate<V> = Predicate { true },
    ) : ScanningMethod<T>(name, operation) {
        private fun getBox(ability: ScanningBoon<T>, pos: BlockPos, radius: Int): AABB {
            val x: Int = pos.x
            val y: Int = pos.y
            val z: Int = pos.z
            val interactionRadius = min(radius, ability.maxRadius)
            return AABB(
                (x - interactionRadius).toDouble(),
                (y - interactionRadius).toDouble(),
                (z - interactionRadius).toDouble(),
                (x + interactionRadius).toDouble(),
                (y + interactionRadius).toDouble(),
                (z + interactionRadius).toDouble(),
            ).inflate(0.99)
        }

        abstract fun convert(entity: V, ability: ScanningBoon<T>): Map<String, Any>

        override fun scan(ability: ScanningBoon<T>, radius: Int, filter: Any?): MethodResult {
            val level = ability.owner.level ?: return MethodResult.of(emptyMap<String, Any>())
            return MethodResult.of(
                level.getEntitiesOfClass(entityClass, getBox(ability, ability.owner.pos, radius)).filter(
                    predicate::test,
                ).map {
                    convert(it, ability)
                },
            )
        }
    }

    class ItemEntityScanningMethod<T : IPeripheralOwner>(
        operation: IPeripheralOperation<SphereOperationContext>,
        private val enriches: Array<out BiConsumer<ItemStack, MutableMap<String, Any>>>,
    ) : EntityScanningMethod<T, ItemEntity>("item", operation, ItemEntity::class.java) {
        override fun convert(entity: ItemEntity, ability: ScanningBoon<T>): Map<String, Any> {
            val base = LuaRepresentation.withPos(entity, ability.owner.facing, ability.owner.pos) { LuaRepresentation.forItemStack(it.item) }
            enriches.forEach { it.accept(entity.item, base) }
            return base
        }
    }
    class XpEntityScanningMethod<T : IPeripheralOwner>(
        operation: IPeripheralOperation<SphereOperationContext>,
        private val enriches: Array<out BiConsumer<ExperienceOrb, MutableMap<String, Any>>>,
    ) : EntityScanningMethod<T, ExperienceOrb>("xp", operation, ExperienceOrb::class.java) {
        override fun convert(entity: ExperienceOrb, ability: ScanningBoon<T>): Map<String, Any> {
            val base = LuaRepresentation.withPos(entity, ability.owner.facing, ability.owner.pos) { LuaRepresentation.forExpirenceOrb(it) }
            enriches.forEach { it.accept(entity, base) }
            return base
        }
    }
    class LivingEntityScanningMethod<T : IPeripheralOwner>(
        operation: IPeripheralOperation<SphereOperationContext>,
        private val enriches: Array<out BiConsumer<LivingEntity, MutableMap<String, Any>>>,
        predicate: Predicate<LivingEntity>,
    ) : EntityScanningMethod<T, LivingEntity>("entity", operation, LivingEntity::class.java, predicate.and { it !is Player }) {
        override fun convert(entity: LivingEntity, ability: ScanningBoon<T>): Map<String, Any> {
            val base = LuaRepresentation.withPos(entity, ability.owner.facing, ability.owner.pos, LuaRepresentation::forLivingEntity)
            enriches.forEach { it.accept(entity, base) }
            return base
        }
    }

    class PlayerScanningMethod<T : IPeripheralOwner>(
        operation: IPeripheralOperation<SphereOperationContext>,
        private val enriches: Array<out BiConsumer<Player, MutableMap<String, Any>>>,
    ) : EntityScanningMethod<T, Player>("player", operation, Player::class.java) {
        override fun convert(entity: Player, ability: ScanningBoon<T>): Map<String, Any> {
            val base = LuaRepresentation.withPos(entity, ability.owner.facing, ability.owner.pos) { LuaRepresentation.forPlayer(it) }
            enriches.forEach { it.accept(entity, base) }
            return base
        }
    }

    private val scanningMethods: MutableMap<String, ScanningMethod<T>> = mutableMapOf()

    override val operations: List<IPeripheralOperation<*>>
        get() = scanningMethods.map { it.value.operation }.toSet().toList()

    override fun collectConfiguration(data: MutableMap<String, Any>) {
        data["maxRadius"] = maxRadius
        data["scanMethods"] = scanningMethods.keys.toList()
        data["scanAPIVersion"] = listOf(1, 2)
    }

    fun attachBlockScan(operation: IPeripheralOperation<SphereOperationContext>, vararg enriches: BiConsumer<BlockState, MutableMap<String, Any>>): ScanningBoon<T> = attachScanningMethod(BlockScanningMethod(operation, enriches))

    fun attachItemScan(operation: IPeripheralOperation<SphereOperationContext>, vararg enriches: BiConsumer<ItemStack, MutableMap<String, Any>>): ScanningBoon<T> = attachScanningMethod(ItemEntityScanningMethod(operation, enriches))

    fun attachLivingEntityScan(operation: IPeripheralOperation<SphereOperationContext>, predicate: Predicate<LivingEntity>, vararg enriches: BiConsumer<LivingEntity, MutableMap<String, Any>>): ScanningBoon<T> = attachScanningMethod(LivingEntityScanningMethod(operation, enriches, predicate))

    fun attachXpScan(operation: IPeripheralOperation<SphereOperationContext>, vararg enriches: BiConsumer<ExperienceOrb, MutableMap<String, Any>>): ScanningBoon<T> = attachScanningMethod(XpEntityScanningMethod(operation, enriches))

    fun attachPlayerScan(operation: IPeripheralOperation<SphereOperationContext>, vararg enriches: BiConsumer<Player, MutableMap<String, Any>>): ScanningBoon<T> = attachScanningMethod(PlayerScanningMethod(operation, enriches))

    fun attachScanningMethod(method: ScanningMethod<T>): ScanningBoon<T> {
        this.scanningMethods[method.name] = method
        return this
    }

    @LuaFunction(mainThread = true)
    fun scan(arguments: IArguments): MethodResult {
        val mode = arguments.getString(0)
        val radius = arguments.optInt(1, maxRadius)
        assertBetween(radius, 1, maxRadius, "radius")
        val scanningMethod = scanningMethods[mode] ?: throw LuaException("There is no scanning method $mode")
        return owner.withOperation(scanningMethod.operation, SphereOperationContext.of(radius), {
            scanningMethod.scan(this, radius, arguments.get(2))
        })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScanningBoon<*>) return false

        if (maxRadius != other.maxRadius) return false
        if (scanningMethods != other.scanningMethods) return false

        return true
    }

    override fun hashCode(): Int {
        var result = maxRadius
        result = 31 * result + scanningMethods.hashCode()
        return result
    }
}
