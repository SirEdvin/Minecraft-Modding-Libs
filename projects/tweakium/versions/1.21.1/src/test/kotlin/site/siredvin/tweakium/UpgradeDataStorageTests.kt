package site.siredvin.tweakium

import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.SharedConstants
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.server.Bootstrap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import site.siredvin.tweakium.modules.peripheral.api.IDataStorage
import site.siredvin.tweakium.modules.peripheral.util.PocketComputerDataStorage
import site.siredvin.tweakium.modules.peripheral.util.TurtleComputerDataStorage
import java.lang.reflect.Proxy
import java.util.Optional

class UpgradeDataStorageTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun bootstrap() {
            SharedConstants.tryDetectVersion()
            Bootstrap.bootStrap()
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun customDataWritesPreserveComponentRemovals(turtle: Boolean) {
        // A narrow CC API fixture: exercise the real storage adapter, not a live computer.
        var patch = DataComponentPatch.builder()
            .set(DataComponents.DAMAGE, 5)
            .remove(DataComponents.CUSTOM_NAME)
            .build()
        val api = if (turtle) ITurtleAccess::class.java else IPocketAccess::class.java
        val access = Proxy.newProxyInstance(api.classLoader, arrayOf(api)) { _, method, args ->
            when (method.name) {
                "getUpgradeData" -> patch
                "setUpgradeData" -> {
                    patch = args.last() as DataComponentPatch
                    null
                }
                else -> error("Unexpected CC API call: ${method.name}")
            }
        }
        val storage: IDataStorage = if (turtle) {
            TurtleComputerDataStorage(access as ITurtleAccess, TurtleSide.LEFT)
        } else {
            PocketComputerDataStorage(access as IPocketAccess)
        }

        storage.putString("owner", "test")
        assertEquals("test", storage.getString("owner"))
        assertEquals(Optional.empty<Any>(), patch.get(DataComponents.CUSTOM_NAME))
        assertEquals(Optional.of(5), patch.get(DataComponents.DAMAGE))

        storage.putInt("fuel", 12)
        storage.remove("owner")
        storage.mutate { it.putBoolean("enabled", true) }
        assertEquals(12, storage.getInt("fuel"))
        assertTrue(storage.getBoolean("enabled"))
        assertEquals(Optional.empty<Any>(), patch.get(DataComponents.CUSTOM_NAME))
        assertEquals(Optional.of(5), patch.get(DataComponents.DAMAGE))
    }
}
