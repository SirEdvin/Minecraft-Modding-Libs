package site.siredvin.broccolium.modules.data.api

import net.minecraft.core.HolderLookup
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import java.util.concurrent.CompletableFuture

fun interface TweakedDataProviderFactory<T : DataProvider> {
    fun create(var1: PackOutput, var2: CompletableFuture<HolderLookup.Provider>): T
}
