package site.siredvin.tweakium.modules.peripheral.ability

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralFunction
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOperation
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwnerBoon
import site.siredvin.tweakium.modules.peripheral.representation.LuaInterpretation
import site.siredvin.tweakium.modules.peripheral.util.radiusCorrect
import kotlin.math.min

class ExperienceBoon(val owner: IPeripheralOwner, private val interactionRadius: Int, private val xpToFuelRate: Int, private val xpTransferOperation: IPeripheralOperation<Any?>) : IPeripheralOwnerBoon {
    companion object {
        private const val COLLECTED_XP_AMOUNT = "CollectedXPAmount"

        fun getStoredXP(dataStorage: CompoundTag): Double = dataStorage.getDouble(COLLECTED_XP_AMOUNT)
    }

    override val operations: List<IPeripheralOperation<*>>
        get() = listOf(xpTransferOperation)

    override fun collectConfiguration(data: MutableMap<String, Any>) {
        data["xpToFuelRate"] = xpToFuelRate
    }

    fun getStoredXP(): Double = getStoredXP(owner.dataStorage)

    fun adjustStoredXP(amount: Double) {
        owner.dataStorage.putDouble(COLLECTED_XP_AMOUNT, owner.dataStorage.getDouble(COLLECTED_XP_AMOUNT) + amount)
        owner.markDataStorageDirty()
    }

    @Throws(LuaException::class)
    protected fun withXPTransfer(
        function: IPeripheralFunction<Any?, MethodResult>,
    ): MethodResult {
        val ability: OperationBoon = owner.getBoon(PeripheralOwnerBoonKey.OPERATION)!!
        return ability.performOperation(xpTransferOperation, null, null, function, null, null)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun collectXP(): MethodResult = withXPTransfer {
        val level: Level = owner.level!!
        val pos = owner.pos
        val searchBox = AABB(pos).inflate(interactionRadius.toDouble())
        val oldCount = getStoredXP()
        level.getEntitiesOfClass(ExperienceOrb::class.java, searchBox).forEach { entity ->
            adjustStoredXP(entity.value.toDouble())
            entity.remove(Entity.RemovalReason.KILLED)
        }
        MethodResult.of(getStoredXP() - oldCount)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun suckOwnerXP(limit: Int): MethodResult {
        return withXPTransfer {
            val player: Player = owner.owner
                ?: return@withXPTransfer MethodResult.of(null, "Cannot find owning player")
            val suckedCount = min(player.totalExperience, limit)
            player.giveExperiencePoints(-suckedCount)
            adjustStoredXP(suckedCount.toDouble())
            MethodResult.of(suckedCount)
        }
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun burnXP(limit: Double): Double {
        if (limit <= 0) throw LuaException("Incorrect limit")
        val fuelAbility: FuelBoon<*> = owner.getBoon(PeripheralOwnerBoonKey.FUEL)
            ?: throw LuaException("Unsupported operation")
        val burnAmount = min(limit, getStoredXP())
        adjustStoredXP(-burnAmount)
        fuelAbility.addFuel((burnAmount / xpToFuelRate).toInt())
        return burnAmount
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun sendXPToOwner(limit: Int): MethodResult {
        return withXPTransfer {
            val count = min(limit.toDouble(), getStoredXP())
            val player: Player = owner.owner
                ?: return@withXPTransfer MethodResult.of(null, "Cannot find owning player")
            player.giveExperiencePoints(count.toInt())
            adjustStoredXP(-count)
            MethodResult.of(count)
        }
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun sendXP(rawBlockPos: Map<*, *>, limit: Double): MethodResult {
        val pos: BlockPos = owner.pos
        val targetPos: BlockPos = LuaInterpretation.asBlockPos(pos, rawBlockPos)
        return withXPTransfer {
            if (!radiusCorrect(pos, targetPos, interactionRadius)) {
                return@withXPTransfer MethodResult.of(null, "Turtle are too far away")
            }

            val abilityExtractResult =
                BoonToolkit.extractAbility(PeripheralOwnerBoonKey.EXPERIENCE, owner.level!!, targetPos)
            if (abilityExtractResult.second != null) {
                return@withXPTransfer MethodResult.of(null, abilityExtractResult.second)
            }
            val transferAmount = min(getStoredXP(), limit)
            adjustStoredXP(-transferAmount)
            abilityExtractResult.first!!.adjustStoredXP(transferAmount)
            MethodResult.of(transferAmount)
        }
    }

    @LuaFunction(mainThread = true, value = ["getStoredXP"])
    fun getStoredXPLua(): Double = getStoredXP()

    @LuaFunction(mainThread = true)
    fun getOwnerXP(): MethodResult {
        val player: Player = owner.owner ?: return MethodResult.of(null, "Cannot find owning player")
        return MethodResult.of(player.totalExperience)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ExperienceBoon) return false

        if (interactionRadius != other.interactionRadius) return false
        if (xpToFuelRate != other.xpToFuelRate) return false
        if (xpTransferOperation != other.xpTransferOperation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = interactionRadius
        result = 31 * result + xpToFuelRate
        result = 31 * result + xpTransferOperation.hashCode()
        return result
    }
}
