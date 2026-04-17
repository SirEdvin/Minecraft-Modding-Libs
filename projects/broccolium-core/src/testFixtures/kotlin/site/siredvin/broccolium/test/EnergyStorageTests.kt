package site.siredvin.broccolium.test

import net.minecraft.network.chat.Component
import org.junit.jupiter.api.Test
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.energy.EnergyRegistry
import site.siredvin.broccolium.modules.storage.energy.EnergyStorageUtils
import site.siredvin.broccolium.modules.storage.energy.EnergyUnit
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.function.Predicate
import kotlin.test.junit5.JUnit5Asserter.assertEquals

abstract class EnergyStorageTests {
    companion object {
        val DUMMY_ENERGY = EnergyRegistry.register("dummy", Component.literal("dummy"))
    }
    abstract fun createStorage(energy: AgnosticEnergyStack, capacity: Long, secondary: Boolean): AgnosticEnergyStorage

    open val defaultUnits: EnergyUnit
        get() = Energies.TURTLE_FUEL

    @Test
    fun testMoveTo() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, true)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 500), 1000, false)
        val moved = from.moveTo(to, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals("count", 500L, moved)
        assertEquals("from count", 500L, from.energy.amount)
        assertEquals("to count", 1000L, to.energy.amount)
    }

    @Test
    fun testMoveToFailed() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, true)
        val to = createStorage(AgnosticEnergyStack(DUMMY_ENERGY, 500), 1000, false)
        val moved = from.moveTo(to, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals("count", 0L, moved)
        assertEquals("count", 1000L, from.energy.amount)
        assertEquals("count", defaultUnits, from.energy.unit)
        assertEquals("count", 500L, to.energy.amount)
        assertEquals("count", DUMMY_ENERGY, to.energy.unit)
    }

    @Test
    fun testMoveToEmpty() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, true)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 0), 1000, false)
        val moved = from.moveTo(to, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals("count", 1000L, moved)
        assertEquals("count", 0L, from.energy.amount)
        assertEquals("count", defaultUnits, from.energy.unit)
        assertEquals("count", 1000L, to.energy.amount)
        assertEquals("count", defaultUnits, to.energy.unit)
    }

    @Test
    fun testMoveFrom() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 500), 1000, true)
        val moved = to.moveFrom(from, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals("count", 500L, moved)
        assertEquals("count", 500L, from.energy.amount)
        assertEquals("count", 1000L, to.energy.amount)
    }

    @Test
    fun testMoveFromFailed() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(DUMMY_ENERGY, 500), 1000, true)
        val moved = to.moveFrom(from, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals("count", 0L, moved)
        assertEquals("count", 1000L, from.energy.amount)
        assertEquals("count", defaultUnits, from.energy.unit)
        assertEquals("count", 500L, to.energy.amount)
        assertEquals("count", DUMMY_ENERGY, to.energy.unit)
    }

    @Test
    fun testMoveFromEmpty() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 0), 1000, true)
        val moved = to.moveFrom(from, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals("count", 1000L, moved)
        assertEquals("count", 0L, from.energy.amount)
        assertEquals("count", defaultUnits, from.energy.unit)
        assertEquals("count", 1000L, to.energy.amount)
        assertEquals("count", defaultUnits, to.energy.unit)
    }

    @Test
    fun testPredicateSearch() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 0), 1000, true)
        val predicate: Predicate<AgnosticEnergyStack> = Predicate {
            it.unit == defaultUnits
        }
        val movedAmount = from.moveTo(to, 1000, takePredicate = predicate)
        assertEquals("count", 1000L, movedAmount)
        assertEquals("count", 0L, from.energy.amount)
        assertEquals("count", defaultUnits, from.energy.unit)
        assertEquals("count", 1000L, to.energy.amount)
        assertEquals("count", defaultUnits, to.energy.unit)
    }

    @Test
    fun testFailedPredicateSearch() {
        val from = createStorage(AgnosticEnergyStack(DUMMY_ENERGY, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 0), 1000, true)
        val predicate: Predicate<AgnosticEnergyStack> = Predicate {
            it.unit == defaultUnits
        }
        val movedAmount = from.moveTo(to, 1000, takePredicate = predicate)
        assertEquals("count", 0L, movedAmount)
        assertEquals("count", 1000L, from.energy.amount)
        assertEquals("count", DUMMY_ENERGY, from.energy.unit)
        assertEquals("count", 0L, to.energy.amount)
        assertEquals("count", defaultUnits, to.energy.unit)
    }
}
