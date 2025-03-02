package site.siredvin.broccolium.modules.data

import site.siredvin.broccolium.BroccoliumCore
import site.siredvin.broccolium.modules.data.api.TextRecord

enum class BroccoliumText : TextRecord {
    PRESS_FOR_DESCRIPTION,
    EMPTY_ENERGY,
    TURTLE_FUEL_ENERGY,
    FORGE_ENERGY,
    RF_ENERGY,
    ;

    override val textID: String by lazy {
        String.format("text.%s.%s", BroccoliumCore.MOD_ID, name.lowercase())
    }
}
