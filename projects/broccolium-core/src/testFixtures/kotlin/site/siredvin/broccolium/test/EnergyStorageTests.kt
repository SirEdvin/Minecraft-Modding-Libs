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
import kotlin.test.assertEquals

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
        val moved = from.moveTo(to, 1000, -1, EnergyStorageUtils.ALWAYS)
        assertEquals(500, moved)
        assertEquals(500, from.firstEnergy.amount)
        assertEquals(1000, to.firstEnergy.amount)
    }

    @Test
    fun testMoveToFailed() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, true)
        val to = createStorage(AgnosticEnergyStack(DUMMY_ENERGY, 500), 1000, false)
        val moved = from.moveTo(to, 1000, -1, EnergyStorageUtils.ALWAYS)
        assertEquals(0, moved)
        assertEquals(1000, from.firstEnergy.amount)
        assertEquals(defaultUnits, from.firstEnergy.unit)
        assertEquals(500, to.firstEnergy.amount)
        assertEquals(DUMMY_ENERGY, to.firstEnergy.unit)
    }

    @Test
    fun testMoveToEmpty() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, true)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 0), 1000, false)
        val moved = from.moveTo(to, 1000, -1, EnergyStorageUtils.ALWAYS)
        assertEquals(1000, moved)
        assertEquals(0, from.firstEnergy.amount)
        assertEquals(defaultUnits, from.firstEnergy.unit)
        assertEquals(1000, to.firstEnergy.amount)
        assertEquals(defaultUnits, to.firstEnergy.unit)
    }

    @Test
    fun testMoveFrom() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 500), 1000, true)
        val moved = to.moveFrom(from, 1000, -1, EnergyStorageUtils.ALWAYS)
        assertEquals(500, moved)
        assertEquals(500, from.firstEnergy.amount)
        assertEquals(1000, to.firstEnergy.amount)
    }

    @Test
    fun testMoveFromFailed() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(DUMMY_ENERGY, 500), 1000, true)
        val moved = to.moveFrom(from, 1000, -1, EnergyStorageUtils.ALWAYS)
        assertEquals(0, moved)
        assertEquals(1000, from.firstEnergy.amount)
        assertEquals(defaultUnits, from.firstEnergy.unit)
        assertEquals(500, to.firstEnergy.amount)
        assertEquals(DUMMY_ENERGY, to.firstEnergy.unit)
    }

    @Test
    fun testMoveFromEmpty() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 0), 1000, true)
        val moved = to.moveFrom(from, 1000, -1, EnergyStorageUtils.ALWAYS)
        assertEquals(1000, moved)
        assertEquals(0, from.firstEnergy.amount)
        assertEquals(defaultUnits, from.firstEnergy.unit)
        assertEquals(1000, to.firstEnergy.amount)
        assertEquals(defaultUnits, to.firstEnergy.unit)
    }

    @Test
    fun testPredicateSearch() {
        val from = createStorage(AgnosticEnergyStack(defaultUnits, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 0), 1000, true)
        val predicate: Predicate<AgnosticEnergyStack> = Predicate {
            it.unit == defaultUnits
        }
        val movedAmount = from.moveTo(to, 1000, takePredicate = predicate)
        assertEquals(1000, movedAmount)
        assertEquals(0, from.firstEnergy.amount)
        assertEquals(defaultUnits, from.firstEnergy.unit)
        assertEquals(1000, to.firstEnergy.amount)
        assertEquals(defaultUnits, to.firstEnergy.unit)
    }

    @Test
    fun testFailedPredicateSearch() {
        val from = createStorage(AgnosticEnergyStack(DUMMY_ENERGY, 1000), 1000, false)
        val to = createStorage(AgnosticEnergyStack(defaultUnits, 0), 1000, true)
        val predicate: Predicate<AgnosticEnergyStack> = Predicate {
            it.unit == defaultUnits
        }
        val movedAmount = from.moveTo(to, 1000, takePredicate = predicate)
        assertEquals(0, movedAmount)
        assertEquals(1000, from.firstEnergy.amount)
        assertEquals(DUMMY_ENERGY, from.firstEnergy.unit)
        assertEquals(0, to.firstEnergy.amount)
        assertEquals(defaultUnits, to.firstEnergy.unit)
    }
}
