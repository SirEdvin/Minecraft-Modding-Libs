package site.siredvin.broccolium.modules.storage.energy

import net.minecraft.network.chat.Component
import org.apache.commons.lang3.math.Fraction
import kotlin.math.roundToLong

object EnergyRegistry {
    val ENERGIES: MutableMap<String, EnergyUnit> = mutableMapOf()
    val CONVERSIONS: MutableMap<EnergyUnit, MutableMap<EnergyUnit, Fraction>> = mutableMapOf()
    val BANNED_CONVERSIONS: MutableMap<EnergyUnit, MutableMap<EnergyUnit, Fraction>> = mutableMapOf()
    val TRANSFERABLE: MutableSet<EnergyUnit> = mutableSetOf()

    fun register(name: String, description: Component, isTransferable: Boolean = false): EnergyUnit {
        val newUnit = EnergyUnit(name, description)
        ENERGIES[name] = newUnit
        if (isTransferable) {
            TRANSFERABLE.add(newUnit)
        }
        return newUnit
    }

    private fun registerSingleConversion(target: MutableMap<EnergyUnit, MutableMap<EnergyUnit, Fraction>>, from: EnergyUnit, to: EnergyUnit, rate: Fraction) {
        if (!target.contains(from)) {
            target[from] = mutableMapOf()
        }
        target[from]?.set(to, rate)
    }

    fun registerConversion(from: EnergyUnit, to: EnergyUnit, rate: Fraction, isReversible: Boolean = true) {
        registerSingleConversion(CONVERSIONS, from, to, rate)
        if (isReversible) {
            registerSingleConversion(CONVERSIONS, to, from, Fraction.getFraction(rate.denominator, rate.numerator))
        } else {
            registerSingleConversion(BANNED_CONVERSIONS, to, from, Fraction.getFraction(rate.denominator, rate.numerator))
        }
    }

    fun isConvertible(source: AgnosticEnergyStack, target: EnergyUnit): Boolean = isConvertible(source.unit, target)

    fun isConvertible(source: EnergyUnit, target: EnergyUnit): Boolean = CONVERSIONS[source]?.get(target) != null

    fun convert(source: AgnosticEnergyStack, target: EnergyUnit, allowBanned: Boolean = false): AgnosticEnergyStack {
        var conversionRate = CONVERSIONS[source.unit]?.get(target)
        if (conversionRate == null && allowBanned) {
            conversionRate = BANNED_CONVERSIONS[source.unit]?.get(target)
        }
        if (conversionRate == null) {
            throw IllegalArgumentException("You suppose to check if units are convertible, this is mod developer issue")
        }
        return AgnosticEnergyStack(target, (source.amount * conversionRate.toDouble()).roundToLong())
    }
}
