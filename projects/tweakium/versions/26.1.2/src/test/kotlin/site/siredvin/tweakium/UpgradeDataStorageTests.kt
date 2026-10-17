package site.siredvin.tweakium

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import site.siredvin.broccolium.test.WithMinecraft
import site.siredvin.tweakium.modules.peripheral.api.IDataStorage
import site.siredvin.tweakium.modules.peripheral.util.PocketComputerDataStorage
import site.siredvin.tweakium.modules.peripheral.util.TurtleComputerDataStorage
import java.lang.reflect.Proxy
import java.util.Optional

@WithMinecraft
class UpgradeDataStorageTests {
    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun customDataWritesPreserveComponentRemovals(turtle: Boolean) {
        var patch = DataComponentPatch.builder().set(DataComponents.DAMAGE, 5).remove(DataComponents.CUSTOM_NAME).build()
        val api = if (turtle) ITurtleAccess::class.java else IPocketAccess::class.java
        val access = Proxy.newProxyInstance(api.classLoader, arrayOf(api)) { _, method, args ->
            when (method.name) {
                "getUpgradeData" -> patch
                "setUpgradeData" -> { patch = args.last() as DataComponentPatch; null }
                else -> error("Unexpected CC API call: ${method.name}")
            }
        }
        val storage: IDataStorage = if (turtle) TurtleComputerDataStorage(access as ITurtleAccess, TurtleSide.LEFT) else PocketComputerDataStorage(access as IPocketAccess)
        storage.putString("owner", "test")
        assertEquals("test", storage.getString("owner"))
        assertEquals(Optional.empty<Any>(), patch.entrySet().first { it.key == DataComponents.CUSTOM_NAME }.value)
        assertEquals(Optional.of(5), patch.entrySet().first { it.key == DataComponents.DAMAGE }.value)
        storage.putInt("fuel", 12)
        storage.remove("owner")
        storage.mutate { it.putBoolean("enabled", true) }
        assertEquals(12, storage.getInt("fuel"))
        assertTrue(storage.getBoolean("enabled"))
        assertEquals(Optional.empty<Any>(), patch.entrySet().first { it.key == DataComponents.CUSTOM_NAME }.value)
        assertEquals(Optional.of(5), patch.entrySet().first { it.key == DataComponents.DAMAGE }.value)
    }
}
