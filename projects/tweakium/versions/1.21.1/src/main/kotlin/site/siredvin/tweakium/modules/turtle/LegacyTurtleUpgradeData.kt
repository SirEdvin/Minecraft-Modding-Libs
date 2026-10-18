package site.siredvin.tweakium.modules.turtle

import com.mojang.serialization.Dynamic

/** Restore legacy fields on the full serialized DFU value, not only its untyped remainder. */
object LegacyTurtleUpgradeData {
    fun restore(input: Dynamic<*>): Dynamic<*> {
        val id = input.get("id").asString("")
        if (id != "computercraft:turtle_normal" && id != "computercraft:turtle_advanced") return input
        var result = input
        for (side in listOf("Left", "Right")) {
            val upgradeKey = "${side}Upgrade"
            val dataKey = "${side}UpgradeNbt"
            // Never replace modern patches, including explicit removals, with leftover legacy data.
            val upgrade = input.get(upgradeKey).asString().result().orElse(null) ?: continue
            val data = input.get(dataKey).result().orElse(null) ?: continue
            if (upgrade.isEmpty() || data.mapValues.result().map { it.isEmpty() }.orElse(true)) continue
            val components = input.emptyMap().set("minecraft:custom_data", data)
            result = result.set(upgradeKey, input.emptyMap().set("id", input.createString(upgrade)).set("components", components))
        }
        // Keep legacy input fields intact; normal CC saving emits only the restored patch.
        return result
    }
}
