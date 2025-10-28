package site.siredvin.broccolium.modules.base.api

import net.minecraft.world.entity.player.Player
import java.util.UUID

interface IOwnedBlockEntity {
    var player: Player?
    val ownerPlayerUUID: UUID?
}
