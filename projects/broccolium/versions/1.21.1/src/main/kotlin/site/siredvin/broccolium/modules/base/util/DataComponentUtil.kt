package site.siredvin.broccolium.modules.base.util

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag

object DataComponentUtil {
    fun patchToNBT(component: DataComponentPatch?): Tag? {
        if (component == null) return null
        return DataComponentPatch.CODEC.encodeStart(
            NbtOps.INSTANCE,
            component,
        ).result().orElse(null)
    }

    fun nbtToPatch(tag: Tag?): DataComponentPatch? {
        if (tag == null) return null
        return DataComponentPatch.CODEC.decode(
            NbtOps.INSTANCE,
            tag,
        ).result().map { it.first }.orElse(null)
    }
}
