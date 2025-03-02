package site.siredvin.peripheralium.data

import site.siredvin.broccolium.modules.data.api.TextRecord
import site.siredvin.peripheralium.PeripheraliumCore

enum class PeripheraliumText : TextRecord {
    CREATIVE_TAB,
    ;

    override val textID: String by lazy {
        String.format("text.%s.%s", PeripheraliumCore.MOD_ID, name.lowercase())
    }
}
