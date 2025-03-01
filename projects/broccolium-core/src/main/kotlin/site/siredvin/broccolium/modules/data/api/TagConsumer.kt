package site.siredvin.broccolium.modules.data.api

import net.minecraft.tags.TagKey

fun interface TagConsumer<T> {
    fun tag(tag: TagKey<T>): LibTagAppender<T>
}
