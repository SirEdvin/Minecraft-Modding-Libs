package site.siredvin.tweakium.modules.peripheral.owner

import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwnerBoon
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwnerBoonKey
import site.siredvin.tweakium.modules.peripheral.boon.OperationBoon
import site.siredvin.tweakium.modules.peripheral.boon.PeripheralOwnerBoonKey

abstract class BasePeripheralOwner : IPeripheralOwner {
    private val _abilities: MutableMap<IPeripheralOwnerBoonKey<*>, IPeripheralOwnerBoon> = HashMap()

    override val abilities: Collection<IPeripheralOwnerBoon>
        get() = _abilities.values

    override fun <T : IPeripheralOwnerBoon> attachBoon(ability: IPeripheralOwnerBoonKey<T>, abilityImplementation: T) {
        if (_abilities.containsKey(ability)) {
            throw IllegalArgumentException("Ability $ability already registered")
        }
        _abilities[ability] = abilityImplementation
    }

    override fun <T : IPeripheralOwnerBoon> getBoon(ability: IPeripheralOwnerBoonKey<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return _abilities[ability] as T?
    }

    fun attachOperations(reduceRate: Double = 1.0, cooldownThreshold: Int = 0) {
        val operationAbility = OperationBoon(this, reduceRate = reduceRate, cooldownThreshold = cooldownThreshold)
        attachBoon(PeripheralOwnerBoonKey.OPERATION, operationAbility)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BasePeripheralOwner) return false

        if (_abilities != other._abilities) return false

        return true
    }

    override fun hashCode(): Int = _abilities.hashCode()
}
