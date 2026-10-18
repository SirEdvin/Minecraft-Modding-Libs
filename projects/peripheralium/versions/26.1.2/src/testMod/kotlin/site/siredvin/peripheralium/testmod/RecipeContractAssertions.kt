package site.siredvin.peripheralium.testmod

import com.google.gson.JsonObject

internal fun verifyRecipeJson(emitted: Map<String, JsonObject>, resultKey: String, legacy: Boolean) {
    check(!legacy)
    check(emitted.keys == setOf("peripheralium_dust", "peripheralium_block", "peripheralium_block_uncraft", "peripheralium_upgrade_template"))
    val dust = emitted.getValue("peripheralium_dust")
    check(dust["type"].asString == "minecraft:crafting_shapeless")
    check(dust["ingredients"].asJsonArray.size() == 2)
    check(dust["result"].asJsonObject[resultKey].asString == "peripheralium:peripheralium_dust")
    val block = emitted.getValue("peripheralium_block")
    check(block["type"].asString == "minecraft:crafting_shapeless")
    check(block["ingredients"].asJsonArray.size() == 9)
    check(block["ingredients"].asJsonArray.all { it.asString == "peripheralium:peripheralium_dust" })
    check(block["result"].asJsonObject[resultKey].asString == "peripheralium:peripheralium_block")
    val uncraft = emitted.getValue("peripheralium_block_uncraft")
    check(uncraft["ingredients"].asJsonArray.single().asString == "peripheralium:peripheralium_block")
    check(uncraft["result"].asJsonObject[resultKey].asString == "peripheralium:peripheralium_dust")
    check(uncraft["result"].asJsonObject["count"].asInt == 9)
    val template = emitted.getValue("peripheralium_upgrade_template")
    check(template["type"].asString == "minecraft:crafting_shaped")
    check(template["pattern"].asJsonArray.map { it.asString } == listOf("PPP", "PXP", "P P"))
    check(template["key"].asJsonObject["P"].asString == "peripheralium:peripheralium_dust")
    check(template["key"].asJsonObject.keySet() == setOf("P", "X"))
    check(template["result"].asJsonObject[resultKey].asString == "peripheralium:peripheralium_upgrade_template")
    check(template["result"].asJsonObject["count"].asInt == 4)
}

internal fun expectIllegalArgument(action: () -> Unit) {
    try {
        action()
    } catch (expected: IllegalArgumentException) {
        return
    }
    error("Invalid recipe definition was accepted")
}
