package site.siredvin.tweakium.modules.minecraft.block

import site.siredvin.broccolium.modules.base.api.ISavableBlockEntity
import site.siredvin.broccolium.modules.base.block.BaseNBTBlock
import site.siredvin.broccolium.modules.base.util.BlockUtil
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.blockentity.PeripheralBlockEntity

abstract class StatefulPeripheralNBTBlock<T, V : IOwnedPeripheral<*>>(
    belongToTickingEntity: Boolean,
    properties: Properties = BlockUtil.defaultProperties(),
) : BaseNBTBlock<T>(belongToTickingEntity, properties) where T : PeripheralBlockEntity<V>, T : ISavableBlockEntity
