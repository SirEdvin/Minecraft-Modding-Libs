package site.siredvin.tweakium.modules.peripheral.api

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage
import site.siredvin.tweakium.modules.peripheral.ability.OperationBoon
import site.siredvin.tweakium.modules.peripheral.ability.PeripheralOwnerBoonKey
import site.siredvin.tweakium.modules.player.FakePlayerProxy
import java.util.function.BiConsumer
import java.util.function.Consumer

interface IPeripheralOwner {
    val name: String?
        @LuaFunction get() {
            return owner?.customName.toString()
        }
    val targetRepresentation: Any?
    val level: Level?
    val pos: BlockPos
    val facing: Direction
    val owner: Player?
    val dataStorage: IDataStorage
    val storage: SlottedAgnosticItemStorage?

    fun <T> withPlayer(function: (FakePlayerProxy) -> T, overwrittenDirection: Direction? = null, skipInventory: Boolean = false): T
    val toolInMainHand: ItemStack
    fun storeItem(stored: ItemStack): ItemStack
    fun destroyUpgrade()
    fun isMovementPossible(level: Level, pos: BlockPos): Boolean
    fun move(level: Level, pos: BlockPos): Boolean
    fun <T : IPeripheralOwnerBoon> attachBoon(ability: IPeripheralOwnerBoonKey<T>, abilityImplementation: T)
    fun <T : IPeripheralOwnerBoon> getBoon(ability: IPeripheralOwnerBoonKey<T>): T?
    val abilities: Collection<IPeripheralOwnerBoon>

    @Throws(LuaException::class)
    fun <T> withOperation(
        operation: IPeripheralOperation<T>,
        context: T,
        method: IPeripheralFunction<T, MethodResult>,
        check: IPeripheralCheck<T>? = null,
        successCallback: Consumer<T>? = null,
        failCallback: BiConsumer<MethodResult, OperationBoon.FailReason>? = null,
    ): MethodResult {
        val operationAbility = getBoon(PeripheralOwnerBoonKey.OPERATION)
            ?: throw IllegalArgumentException("Owner doesn't have ability to store operations logic, which is very strange!")
        return operationAbility.performOperation(operation, context, check, method, successCallback, failCallback)
    }

    override fun equals(other: Any?): Boolean
}
