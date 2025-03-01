package site.siredvin.broccolium.test

import net.minecraft.network.chat.Component
import org.junit.jupiter.api.Test
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.Energies
import site.siredvin.broccolium.modules.storage.energy.EnergyStorageUtils
import site.siredvin.broccolium.modules.storage.energy.EnergyUnit
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.function.Predicate
import kotlin.test.assertEquals

abstract class EnergyStorageTests {
    companion object {
        val DUMMY_ENERGY = EnergyUnit("dummy", Component.literal("dummy"))
    }
    abstract fun createStorage(energy: AgnosticEnergyStack, capacity: Long, secondary: Boolean): AgnosticEnergyStorage

    open val defaultUnits: EnergyUnit
        get() = Energies.TURTLE_FUEL

    @Test
    fun testMoveTo() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, true)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 500), 1000, false)
        val moved = from.moveTo(to, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals(500, moved)
        assertEquals(500, from.energy.amount)
        assertEquals(1000, to.energy.amount)
    }

    @Test
    fun testMoveToFailed() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, true)
        val to = createStorage(AgnosticEnergyStack(DUMMY_ENERGY, 500), 1000, false)
        val moved = from.moveTo(to, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals(0, moved)
        assertEquals(1000, from.energy.amount)
        assertEquals(defaultUnits, from.energy.unit)
        assertEquals(500, to.energy.amount)
        assertEquals(DUMMY_ENERGY, to.energy.unit)
    }

    @Test
    fun testMoveToEmpty() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, true)
        val to = createStorage(AgnosticEnergyStack(Energies.EMPTY, 0), 1000, false)
        val moved = from.moveTo(to, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals(1000, moved)
        assertEquals(0, from.energy.amount)
        assertEquals(defaultUnits, from.energy.unit)
        assertEquals(1000, to.energy.amount)
        assertEquals(defaultUnits, to.energy.unit)
    }

    @Test
    fun testMoveFrom() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 500), 1000, true)
        val moved = to.moveFrom(from, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals(500, moved)
        assertEquals(500, from.energy.amount)
        assertEquals(1000, to.energy.amount)
    }

    @Test
    fun testMoveFromFailed() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(DUMMY_ENERGY, 500), 1000, true)
        val moved = to.moveFrom(from, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals(0, moved)
        assertEquals(1000, from.energy.amount)
        assertEquals(defaultUnits, from.energy.unit)
        assertEquals(500, to.energy.amount)
        assertEquals(DUMMY_ENERGY, to.energy.unit)
    }

    @Test
    fun testMoveFromEmpty() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(Energies.EMPTY, 0), 1000, true)
        val moved = to.moveFrom(from, 1000, EnergyStorageUtils.ALWAYS)
        assertEquals(1000, moved)
        assertEquals(0, from.energy.amount)
        assertEquals(defaultUnits, from.energy.unit)
        assertEquals(1000, to.energy.amount)
        assertEquals(defaultUnits, to.energy.unit)
    }

    @Test
    fun testPredicateSearch() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(Energies.EMPTY, 0), 1000, true)
        val predicate: Predicate<AgnosticEnergyStack> = Predicate {
            it.unit == defaultUnits
        }
        val movedAmount = from.moveTo(to, 1000, takePredicate = predicate)
        assertEquals(1000, movedAmount)
        assertEquals(0, from.energy.amount)
        assertEquals(defaultUnits, from.energy.unit)
        assertEquals(1000, to.energy.amount)
        assertEquals(defaultUnits, to.energy.unit)
    }

    @Test
    fun testFailedPredicateSearch() {
        val from = createStorage(AgnosticEnergyStack(DUMMY_ENERGY, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(Energies.EMPTY, 0), 1000, true)
        val predicate: Predicate<AgnosticEnergyStack> = Predicate {
            it.unit == defaultUnits
        }
        val movedAmount = from.moveTo(to, 1000, takePredicate = predicate)
        assertEquals(0, movedAmount)
        assertEquals(1000, from.energy.amount)
        assertEquals(DUMMY_ENERGY, from.energy.unit)
        assertEquals(0, to.energy.amount)
        assertEquals(Energies.EMPTY, to.energy.unit)
    }
}
