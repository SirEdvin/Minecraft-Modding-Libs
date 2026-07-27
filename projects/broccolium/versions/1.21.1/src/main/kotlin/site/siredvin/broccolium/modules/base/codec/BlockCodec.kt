@file:Suppress("UNCHECKED_CAST")

package site.siredvin.broccolium.modules.base.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import site.siredvin.broccolium.modules.base.block.BaseBlockEntityBlock
import site.siredvin.broccolium.modules.base.block.FacingBlockEntityBlock
import site.siredvin.broccolium.modules.base.block.GenericBlockEntityBlock
import site.siredvin.broccolium.modules.base.codec.api.BlockConstructor
import site.siredvin.broccolium.modules.platform.api.RegistryEntry
import java.util.function.Function

object BlockCodec {
    val BOOLEAN_CODEC = Codec.unit(false)

    fun <B : Block> propertiesCodec(): RecordCodecBuilder<B, BlockBehaviour.Properties> = BlockBehaviour.Properties.CODEC.fieldOf("properties")
        .forGetter { obj: B -> obj.properties() }

    fun <B : BaseBlockEntityBlock<*>> tickingEntityCodec(): RecordCodecBuilder<B, Boolean> = BOOLEAN_CODEC.fieldOf("belongToTickingEntity").forGetter { obj: B -> obj.belongToTickingEntity }

    fun <B : FacingBlockEntityBlock<*>> isRotatableCodec(): RecordCodecBuilder<B, Boolean> = BOOLEAN_CODEC.fieldOf("isRotatable").forGetter { obj: B -> obj.isRotatable }

    @Suppress("UNCHECKED_CAST")
    fun <B : BaseBlockEntityBlock<P>, E : BlockEntityType<P>, P : BlockEntity> blockEntityCodec(getter: Function<B, RegistryEntry<E>>): RecordCodecBuilder<B, RegistryEntry<E>> = (
        GenericCodec.codec(BuiltInRegistries.BLOCK_ENTITY_TYPE)
            .xmap({ x -> x }, { x -> x })
            .fieldOf("block_entity") as MapCodec<RegistryEntry<E>>
        ).forGetter(getter)

    fun <B : GenericBlockEntityBlock<T>, Z : BlockEntityType<T>, T : BlockEntity> buildCodec(constructor: BlockConstructor<B, Z, T>): MapCodec<B> {
        return RecordCodecBuilder.mapCodec {
            return@mapCodec it.group(
                blockEntityCodec<B, Z, T> { b -> b.blockEntityTypeSup as RegistryEntry<Z> },
                isRotatableCodec<B>(),
                tickingEntityCodec<B>(),
                propertiesCodec<B>(),
            ).apply(it, constructor::build)
        }
    }
}
