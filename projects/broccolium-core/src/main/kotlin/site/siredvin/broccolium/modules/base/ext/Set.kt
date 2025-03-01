package site.siredvin.broccolium.modules.base.ext

infix fun <T> Set<T>.xor(that: Set<T>): Set<T> = (this - that) + (that - this)
