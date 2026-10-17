package site.siredvin.broccolium.test

import com.mojang.serialization.MapCodec
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.matcher.ElementMatchers.named
import net.bytebuddy.matcher.ElementMatchers.takesArguments
import net.minecraft.SharedConstants
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.Bootstrap
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.redstone.Orientation
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import site.siredvin.broccolium.modules.base.api.IObservingBlockEntity
import site.siredvin.broccolium.modules.base.block.BaseBlockEntityBlock
import sun.misc.Unsafe
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class ObserverCallbackTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun bootstrap() {
            SharedConstants.tryDetectVersion()
            Bootstrap.bootStrap()
        }
    }

    @Test
    fun forwardsTheNativeBlockAndOrientationExactlyOnce() {
        val observer = Observer()
        val block = allocate(ProbeBlock::class.java)
        val orientation = Orientation.of(Direction.UP, Direction.NORTH, Orientation.SideBias.LEFT)
        block.notify(level(observer), BlockPos(4, 7, 9), Blocks.REDSTONE_BLOCK, orientation, false)
        assertEquals(1, observer.notifications)
        assertSame(Blocks.REDSTONE_BLOCK, observer.neighbourBlock)
        assertSame(orientation, observer.orientation)
    }

    @Test
    fun forwardsNullOrientationWithoutInventingADirectionOrPosition() {
        val observer = Observer()
        val block = allocate(ProbeBlock::class.java)
        block.notify(level(observer), BlockPos(20, 5, -8), Blocks.STONE, null, true)
        assertEquals(1, observer.notifications)
        assertSame(Blocks.STONE, observer.neighbourBlock)
        assertNull(observer.orientation)
    }

    @Test
    fun absentBlockEntityDoesNotReceiveASyntheticNotification() {
        val block = allocate(ProbeBlock::class.java)
        block.notify(level(null), BlockPos.ZERO, Blocks.STONE, null, false)
    }

    @Test
    fun ordinaryBlockEntityDoesNotRequireTheObserverInterface() {
        val block = allocate(ProbeBlock::class.java)
        val entity = BlockEntityType.CHEST.create(BlockPos.ZERO, Blocks.CHEST.defaultBlockState())
        block.notify(level(entity), BlockPos.ZERO, Blocks.STONE, null, false)
    }

    private class Observer : BlockEntity(BlockEntityType.CHEST, BlockPos.ZERO, Blocks.CHEST.defaultBlockState()), IObservingBlockEntity {
        var notifications = 0
        var neighbourBlock: Block? = null
        var orientation: Orientation? = null

        override fun onNeighbourChange(neighbourBlock: Block, orientation: Orientation?) {
            notifications++
            this.neighbourBlock = neighbourBlock
            this.orientation = orientation
        }
    }

    private class ProbeBlock : BaseBlockEntityBlock<BlockEntity>(false) {
        override fun codec(): MapCodec<out ProbeBlock> = simpleCodec { ProbeBlock() }
        override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? = null

        fun notify(level: Level, pos: BlockPos, block: Block, orientation: Orientation?, moved: Boolean) {
            neighborChanged(Blocks.STONE.defaultBlockState(), level, pos, block, orientation, moved)
        }
    }

    // Bypass world construction and post-bootstrap block registration, not the
    // callback body. This isolated test does not prove registry/loader/chunk
    // dispatch; those require the native loader-run regression suite.
    private fun level(entity: BlockEntity?): Level {
        val type = ByteBuddy()
            .subclass(Level::class.java, ConstructorStrategy.Default.NO_CONSTRUCTORS)
            .method(named<net.bytebuddy.description.method.MethodDescription>("getBlockEntity").and(takesArguments(BlockPos::class.java)))
            .intercept(if (entity == null) FixedValue.nullValue() else FixedValue.value(entity))
            .make()
            .load(javaClass.classLoader, ClassLoadingStrategy.Default.WRAPPER)
            .loaded
        return allocate(type)
    }

    private fun <T> allocate(type: Class<T>): T {
        val field = Unsafe::class.java.getDeclaredField("theUnsafe")
        field.isAccessible = true
        return type.cast((field.get(null) as Unsafe).allocateInstance(type))
    }
}
