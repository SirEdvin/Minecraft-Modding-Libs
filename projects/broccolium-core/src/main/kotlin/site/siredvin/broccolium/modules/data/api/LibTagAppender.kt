package site.siredvin.broccolium.modules.data.api

import net.minecraft.tags.TagBuilder
import net.minecraft.tags.TagKey
import site.siredvin.broccolium.modules.platform.api.RegistryWrapper

class LibTagAppender<T>(private val registry: RegistryWrapper<T>, private val builder: TagBuilder) {
    fun add(`object`: T): LibTagAppender<T> {
        builder.addElement(registry.getKey(`object`))
        return this
    }

    @SafeVarargs
    fun add(vararg objects: T): LibTagAppender<T> {
        for (`object` in objects) add(`object`)
        return this
    }

    fun addTag(tag: TagKey<T>): LibTagAppender<T> {
        builder.addTag(tag.location())
        return this
    }
}
