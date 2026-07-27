package site.siredvin.broccolium.modules.storage.base.api

interface AccessibleAgnosticStorage<T, L : Number> : AgnosticStorage<T, L> {
    fun get(slot: Int): T
}
