package site.siredvin.broccolium.modules.base.api

import net.minecraft.world.entity.player.Player

interface IOwnedBlockEntity {
    var player: Player?
}
