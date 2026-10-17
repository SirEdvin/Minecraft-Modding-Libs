package site.siredvin.tweakium.test

import net.neoforged.neoforge.transfer.energy.EnergyHandler
import net.neoforged.neoforge.transfer.transaction.TransactionContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import site.siredvin.tweakium.modules.plugins.ForgeEnergyPlugin

class ForgeEnergyPluginTests {
    @Test
    fun nativeLongAmountsAreNotNarrowedToSignedInts() {
        val handler = object : EnergyHandler {
            override fun getAmountAsLong(): Long = 3_000_000_000L
            override fun getCapacityAsLong(): Long = 4_000_000_000L
            override fun insert(amount: Int, transaction: TransactionContext): Int = error("Query must not transfer")
            override fun extract(amount: Int, transaction: TransactionContext): Int = error("Query must not transfer")
        }
        val plugin = ForgeEnergyPlugin(handler)
        assertEquals(3_000_000_000L, plugin.getEnergy())
        assertEquals(4_000_000_000L, plugin.getEnergyCapacity())
        assertEquals("FE", plugin.getEnergyUnit())
    }
}
