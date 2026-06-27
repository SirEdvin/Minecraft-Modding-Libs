package site.siredvin.broccolium.modules.storage.base.api

interface SomethingOperator<T, L : Number> {
    fun isEmpty(something: T): Boolean
    fun getSize(something: T): L
    fun canStack(first: T, second: T): Boolean
    fun canMerge(first: T, second: T, stackLimit: L? = null): Boolean

    /**
     * Merge second item stack into first one and returns remains
     */
    fun inplaceMerge(first: T, second: T, mergeLimit: L? = null): T

    fun getZero(): L
    fun isZero(value: L): Boolean
    fun biggerThanZero(value: L): Boolean
    fun min(first: L, second: L): L
    fun subtract(first: L, second: L): L
    fun add(first: L, second: L): L
}
