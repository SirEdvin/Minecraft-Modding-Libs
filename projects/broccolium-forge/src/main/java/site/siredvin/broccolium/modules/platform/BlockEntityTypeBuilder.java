package site.siredvin.broccolium.modules.platform;

import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

final class BlockEntityTypeBuilder {
    private BlockEntityTypeBuilder() {}

    static <T extends BlockEntity> BlockEntityType<T> build(
            BiFunction<BlockPos, BlockState, T> factory, Block block) {
        return BlockEntityType.Builder.of(factory::apply, block).build(null);
    }
}
