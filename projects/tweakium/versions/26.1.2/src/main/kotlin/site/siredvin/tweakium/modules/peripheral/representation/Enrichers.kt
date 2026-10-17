package site.siredvin.tweakium.modules.peripheral.representation

import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.animal.bee.Bee
import net.minecraft.world.entity.npc.villager.Villager
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.Bees
import net.minecraft.world.item.trading.Merchant
import net.minecraft.world.level.block.entity.BeehiveBlockEntity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.IntegerProperty
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import site.siredvin.broccolium.modules.platform.PlatformTags
import java.util.function.BiConsumer

val animalData = BiConsumer<Entity, MutableMap<String, Any>> { entity, data ->
    if (entity is Animal) {
        data["baby"] = entity.isBaby
        data["inLove"] = entity.isInLove
        data["aggressive"] = entity.isAggressive
        val isShearable = PlatformTags.get().isShearable(entity, Items.SHEARS.defaultInstance)
        if (isShearable.first) data["shareable"] = isShearable.second
    }
}

val merchantData = BiConsumer<Entity, MutableMap<String, Any>> { entity, data ->
    if (entity is Merchant) data["offers"] = LuaRepresentation.forMerchantOffers(entity)
}

val villagerData = BiConsumer<Entity, MutableMap<String, Any>> { entity, data ->
    if (entity is Villager) {
        LuaRepresentation.forVillager(entity).forEach { data[it.key] = it.value }
    }
}

val effectsData = BiConsumer<Entity, MutableMap<String, Any>> { entity, data ->
    if (entity is LivingEntity) {
        val effects: MutableList<MutableMap<String, Any>> = mutableListOf()
        entity.activeEffectsMap.forEach { effects.add(LuaRepresentation.forMobEffectInstance(it.value)) }
        data["effects"] = effects
    }
}

val stateProperties = BiConsumer<BlockState, MutableMap<String, Any>> { state, data ->
    val properties: MutableMap<String, Any> = mutableMapOf()
    state.properties.forEach { properties[it.name] = state.getValue(it).toString() }
    data["properties"] = properties
}

val cropAge = BiConsumer<BlockState, MutableMap<String, Any>> { state, data ->
    val ageProperty = state.properties.find { it.name == "age" } as IntegerProperty?
    if (ageProperty != null) {
        data["age"] = state.getValue(ageProperty)
        data["maxAge"] = ageProperty.possibleValues.maxOf { it }
    }
}

val honeyLevel = BiConsumer<BlockState, MutableMap<String, Any>> { state, data ->
    val ageProperty = state.properties.find { it.name == "honey_level" } as IntegerProperty?
    if (ageProperty != null) data["honeyLevel"] = state.getValue(ageProperty)
}

val beeNestAnalyze = BiConsumer<BlockEntity, MutableMap<String, Any>> { entity, data ->
    if (entity is BeehiveBlockEntity) {
        data["isSmoked"] = entity.isSedated
        data["isFull"] = entity.isFull
        data["bees"] = entity.collectComponents().getOrDefault(DataComponents.BEES, Bees.EMPTY).bees().map {
            val beeData = hashMapOf<String, Any>()
            beeData["ticksInHive"] = it.ticksInHive()
            beeData["minTicksInHive"] = it.minTicksInHive()
            val entityData = it.entityData().copyTagWithoutId()
            beeData["hasFlower"] = entityData.contains(Bee.TAG_FLOWER_POS)
            beeData["health"] = entityData.getIntOr("Health", 0)
            beeData["hasStung"] = entityData.getBooleanOr(Bee.TAG_HAS_STUNG, false)
            beeData["hasNectar"] = entityData.getBooleanOr(Bee.TAG_HAS_NECTAR, false)
            val type = it.entityData().type()
            beeData["id"] = PlatformRegistries.ENTITY_TYPES.getKey(type).toString()
            beeData["name"] = type.description.string
            beeData
        }
    }
}
