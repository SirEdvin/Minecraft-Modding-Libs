package site.siredvin.tweakium

import dan200.computercraft.api.lua.MethodResult
import net.minecraft.nbt.CompoundTag
import org.junit.jupiter.api.Test
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralCheck
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOperation
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.boon.FuelBoon
import site.siredvin.tweakium.modules.peripheral.boon.OperationBoon
import site.siredvin.tweakium.modules.peripheral.boon.PeripheralOwnerBoonKey
import site.siredvin.tweakium.modules.peripheral.owner.DisabledPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.util.CompoundTagDataStorage
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class OperationBoonTests {
    private class Owner : IPeripheralOwner by DisabledPeripheralOwner() {
        override val dataStorage = CompoundTagDataStorage(CompoundTag()) {}
    }

    private class Operation(private val cost: Int = 0, private val cooldown: Int = 0) : IPeripheralOperation<Int> {
        override val name = "TEST_ACTION"
        override fun getCost(context: Int) = cost
        override fun getCooldown(context: Int) = cooldown
        override fun computerDescription() = mapOf("cost" to cost)
    }

    // Only the fuel backend is a fixture; multiplier, simulation and validation are production policy.
    private class Fuel(owner: Owner) : FuelBoon<Owner>(owner) {
        override val maxFuelConsumptionRate = 3
        override val isFuelConsumptionDisable = false
        override var fuelCount = 20
        override val fuelMaxCount = 100
        override fun addFuel(count: Int) {
            fuelCount += count
        }
        override fun consumeFuelInternal(count: Int): Boolean {
            if (count > fuelCount) return false
            fuelCount -= count
            return true
        }
    }

    @Test
    fun failedPreconditionStopsBeforePaymentActionAndSuccessCallback() {
        val owner = Owner()
        val fuel = Fuel(owner)
        owner.attachBoon(PeripheralOwnerBoonKey.FUEL, fuel)
        val expected = MethodResult.of(null, "permission denied")
        val failures = mutableListOf<OperationBoon.FailReason>()
        val result = OperationBoon(owner).performOperation(
            Operation(cost = 4),
            42,
            { context ->
                assertEquals(42, context)
                expected
            },
            { error("Rejected action must not execute") },
            { error("Rejected action must not report success") },
            { actual, reason ->
                assertSame(expected, actual)
                failures += reason
            },
        )
        assertSame(expected, result)
        assertEquals(listOf(OperationBoon.FailReason.CHECK_FAILED), failures)
        assertEquals(20, fuel.fuelCount)
    }

    @Test
    fun missingFuelRejectsPaidOperationWithoutCallingAction() {
        val failures = mutableListOf<OperationBoon.FailReason>()
        OperationBoon(Owner()).performOperation(
            Operation(cost = 1),
            0,
            null,
            { error("An unfunded action must not execute") },
            null,
            { _, reason -> failures += reason },
        )
        assertEquals(listOf(OperationBoon.FailReason.NOT_ENOUGH_FUEL), failures)
    }

    @Test
    fun insufficientFuelDoesNotExecuteOrChargeAndSuccessPaysOnce() {
        val owner = Owner()
        val fuel = Fuel(owner)
        owner.attachBoon(PeripheralOwnerBoonKey.FUEL, fuel)
        val operations = OperationBoon(owner)
        val failures = mutableListOf<OperationBoon.FailReason>()
        operations.performOperation(
            Operation(cost = 21),
            0,
            null,
            { error("An unfunded action must not execute") },
            null,
            { _, reason -> failures += reason },
        )
        assertEquals(listOf(OperationBoon.FailReason.NOT_ENOUGH_FUEL), failures)
        assertEquals(20, fuel.fuelCount)
        val calls = mutableListOf<String>()
        val expected = MethodResult.of("completed")
        val result = operations.performOperation(
            Operation(cost = 4),
            7,
            null,
            { context ->
                assertEquals(7, context)
                calls += "action"
                expected
            },
            { calls += "success" },
            { _, _ -> error("Successful action must not report failure") },
        )
        assertSame(expected, result)
        assertEquals(listOf("action", "success"), calls)
        assertEquals(16, fuel.fuelCount)
    }

    @Test
    fun persistedCooldownRejectsBeforeCheckAndExpiredCooldownAllowsAction() {
        val owner = Owner()
        val operation = Operation()
        val operations = OperationBoon(owner)
        val cooldowns = CompoundTag().apply { putLong(operation.settingsName(), System.currentTimeMillis() + 60_000) }
        owner.dataStorage.putCompound(OperationBoon.COOLDOWNS_TAG, cooldowns)
        val failures = mutableListOf<OperationBoon.FailReason>()
        operations.performOperation(
            operation,
            0,
            { error("Cooldown must precede precondition") },
            { error("Cooldown must prevent action") },
            null,
            { _, reason -> failures += reason },
        )
        assertEquals(listOf(OperationBoon.FailReason.COOLDOWN), failures)
        cooldowns.putLong(operation.settingsName(), 1)
        owner.dataStorage.putCompound(OperationBoon.COOLDOWNS_TAG, cooldowns)
        assertEquals(0, operations.getCurrentCooldown(operation))
        val expected = MethodResult.of(true)
        assertSame(expected, operations.performOperation(operation, 0, null, { expected }, null, null))
    }

    @Test
    fun successfulActionPersistsCooldownAndRegistersPublicConfiguration() {
        val owner = Owner()
        val operation = Operation(cooldown = 60_000)
        val operations = OperationBoon(owner)
        operations.registerOperation(operation)
        operations.performOperation(operation, 0, null, { MethodResult.of(true) }, null, null)
        assertTrue(owner.dataStorage.getCompound(OperationBoon.COOLDOWNS_TAG).contains(operation.settingsName()))
        assertTrue(operations.getCurrentCooldown(operation) in 1..60_000)
        assertEquals(listOf(operation.settingsName()), operations.getOperationsLua())
        val configuration = mutableMapOf<String, Any>()
        operations.collectConfiguration(configuration)
        assertEquals(operation.computerDescription(), configuration[operation.settingsName()])
    }

    @Test
    fun fuelSimulationUsesMultiplierWithoutConsumingAndRejectsInvalidRates() {
        val owner = Owner()
        val fuel = Fuel(owner)
        assertEquals(1, fuel.fuelConsumptionRate)
        fuel.setFuelConsumptionRate(3)
        assertEquals(4, fuel.fuelConsumptionMultiply)
        assertTrue(fuel.consumeFuel(4, true))
        assertFalse(fuel.consumeFuel(6, true))
        assertEquals(20, fuel.fuelCount)
        assertTrue(fuel.consumeFuel(4, false))
        assertEquals(4, fuel.fuelCount)
        fuel.setFuelConsumptionRate(0)
        fuel.setFuelConsumptionRate(4)
        assertEquals(3, fuel.fuelConsumptionRate)
        assertEquals(30, fuel.reduceCooldownAccordingToConsumptionRate(90))
    }

    @Test
    fun chainedChecksShortCircuitAndOnlyContinueAfterSuccess() {
        val denied = MethodResult.of(null, "denied")
        val first = IPeripheralCheck<Int> { denied }
        assertSame(denied, first.checkAlso { error("Second check must not run") }!!.check(1))
        val permitted = IPeripheralCheck<Int> { null }
        assertSame(denied, permitted.checkAlso { denied }!!.check(1))
    }
}
