package site.siredvin.tweakium

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import org.junit.jupiter.api.Test
import site.siredvin.tweakium.modules.peripheral.OwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralPlugin
import site.siredvin.tweakium.modules.peripheral.owner.DisabledPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.util.AbstractDataObject
import site.siredvin.tweakium.modules.peripheral.util.CompoundTagDataStorage
import site.siredvin.tweakium.modules.peripheral.util.OrientationUtil
import site.siredvin.tweakium.modules.peripheral.util.assertBetween
import site.siredvin.tweakium.modules.peripheral.util.isCorrectSlot
import site.siredvin.tweakium.modules.peripheral.util.radiusCorrect
import java.lang.reflect.Proxy
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PeripheralContractTests {
    private fun computer(id: Int): IComputerAccess = Proxy.newProxyInstance(
        IComputerAccess::class.java.classLoader,
        arrayOf(IComputerAccess::class.java),
    ) { proxy, method, args ->
        when (method.name) {
            "getID" -> id
            "equals" -> proxy === args?.get(0)
            "hashCode" -> id
            else -> error("Unexpected computer call ${method.name}")
        }
    } as IComputerAccess

    @Test
    fun attachmentLifecycleNotifiesOnlyFirstAndLastConnection() {
        var attaches = 0
        var detaches = 0
        val peripheral = object : OwnedPeripheral<DisabledPeripheralOwner>("tree", DisabledPeripheralOwner(), "test") {
            override val isEnabled = true
        }
        peripheral.addPlugin(object : IPeripheralPlugin {
            override val additionalType = "leaves"
            override fun onFirstAttach() {
                attaches++
            }
            override fun onLastDetach() {
                detaches++
            }
            override fun collectConfiguration(data: MutableMap<String, Any>) {
                data["limit"] = 8
            }
        })
        val first = computer(10)
        val second = computer(20)
        peripheral.attach(first)
        peripheral.attach(second)
        assertEquals(1, attaches)
        assertEquals(2, peripheral.connectedComputersCount)
        assertTrue(peripheral.isComputerPresent(20))
        peripheral.detach(first)
        assertEquals(0, detaches)
        assertFalse(peripheral.isComputerPresent(10))
        peripheral.detach(second)
        assertEquals(1, detaches)
        assertEquals(0, peripheral.connectedComputersCount)
        assertEquals(setOf("leaves"), peripheral.additionalTypes)
        assertEquals(mapOf("implementationProvider" to "test", "limit" to 8), peripheral.configuration)
    }

    @Test
    fun relativeAndAbsoluteSidesRespectEveryHorizontalFacing() {
        for (facing in Direction.Plane.HORIZONTAL) {
            assertEquals(facing, OrientationUtil.getDirection(facing, "front"))
            assertEquals(facing.opposite, OrientationUtil.getDirection(facing, "back"))
            assertEquals(facing.counterClockWise, OrientationUtil.getDirection(facing, "right"))
            assertEquals(facing.clockWise, OrientationUtil.getDirection(facing, "left"))
            assertEquals(Direction.UP, OrientationUtil.getDirection(facing, "top"))
            assertEquals(Direction.DOWN, OrientationUtil.getDirection(facing, "bottom"))
            for (absolute in Direction.values()) {
                assertEquals(absolute, OrientationUtil.getDirection(facing, absolute.serializedName))
            }
        }
        assertFailsWith<LuaException> { OrientationUtil.getDirection(Direction.NORTH, "somewhere") }
    }

    @Test
    fun slotAndRadiusValidationIncludesBoundsAndRejectsOutside() {
        for (slot in 1..16) isCorrectSlot(slot)
        for (slot in listOf(-1, 0, 17, Int.MAX_VALUE)) assertFailsWith<LuaException> { isCorrectSlot(slot) }
        assertBetween(1, 1, 2, "slot")
        assertBetween(2, 1, 2, "slot")
        assertFailsWith<LuaException> { assertBetween(3, 1, 2, "slot") }
        val origin = BlockPos(-10, 4, 8)
        assertTrue(radiusCorrect(origin, origin.offset(2, -2, 2), 2))
        for (offset in listOf(BlockPos(3, 0, 0), BlockPos(0, -3, 0), BlockPos(0, 0, 3))) {
            assertFalse(radiusCorrect(origin, origin.offset(offset), 2))
        }
    }

    @Test
    fun compoundStoragePersistsEveryMutationAndRoundTripsNativeNbt() {
        val tag = CompoundTag()
        var changes = 0
        val storage = CompoundTagDataStorage(tag) { changes++ }
        storage.putString("name", "oak")
        storage.putInt("count", 3)
        storage.putDouble("rate", 1.25)
        storage.putBoolean("enabled", true)
        storage.mutate { it.putLong("time", 9L) }
        val restored = CompoundTagDataStorage(tag.copy()) {}
        assertEquals("oak", restored.getString("name"))
        assertEquals(3, restored.getInt("count"))
        assertEquals(1.25, restored.getDouble("rate"))
        assertTrue(restored.getBoolean("enabled"))
        assertEquals(9L, tag.getLong("time"))
        assertEquals(5, changes)
        storage.remove("name")
        assertFalse(storage.has("name"))
        assertEquals(6, changes)
        assertTrue(restored.has("name"))
    }

    @Test
    fun nullableDataObjectRemovesUnreadableAndRejectedValues() {
        val tag = CompoundTag()
        val storage = CompoundTagDataStorage(tag) {}
        val value = object : AbstractDataObject<Int>() {
            override val nbtTag = "value"
            override fun read(data: site.siredvin.tweakium.modules.peripheral.api.IDataStorage): Int? = data.getInt(nbtTag).takeIf { it > 0 }
            override fun write(data: site.siredvin.tweakium.modules.peripheral.api.IDataStorage, value: Int): Boolean {
                if (value <= 0) return false
                data.putInt(nbtTag, value)
                return true
            }
        }
        assertEquals(null, value[storage])
        value[storage] = 3
        assertEquals(3, value[storage])
        value[storage] = -1
        assertFalse(storage.has("value"))
        storage.putInt("value", -2)
        assertEquals(null, value[storage])
        assertFalse(storage.has("value"))
        value[storage] = 4
        value[storage] = null
        assertFalse(storage.has("value"))
    }
}
