package site.siredvin.broccolium.modules.base.ext

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.phys.Vec3

fun BlockPos.toVec3() = Vec3(x.toDouble(), y.toDouble(), z.toDouble())

fun BlockPos.toRelative(facing: Direction): BlockPos = when (facing) {
    Direction.UP, Direction.DOWN -> throw IllegalArgumentException("This works only for horizontal facing")
    Direction.NORTH -> this.rotate(Rotation.CLOCKWISE_90)
    Direction.SOUTH -> this.rotate(Rotation.COUNTERCLOCKWISE_90)
    Direction.EAST -> this
    Direction.WEST -> this.rotate(Rotation.CLOCKWISE_180)
}

fun BlockPos.fromRelative(facing: Direction): BlockPos = when (facing) {
    Direction.UP, Direction.DOWN -> throw IllegalArgumentException("This works only for horizontal facing")
    Direction.NORTH -> this.rotate(Rotation.COUNTERCLOCKWISE_90)
    Direction.SOUTH -> this.rotate(Rotation.CLOCKWISE_90)
    Direction.EAST -> this
    Direction.WEST -> this.rotate(Rotation.CLOCKWISE_180)
}

fun Vec3.toBlockPos() = BlockPos(x.toInt(), y.toInt(), z.toInt())

fun Vec3.rotate(rotation: Rotation): Vec3 = when (rotation) {
    Rotation.NONE -> this
    Rotation.CLOCKWISE_90 -> Vec3(-this.z, this.y, this.x)
    Rotation.CLOCKWISE_180 -> Vec3(-this.x, this.y, -this.z)
    Rotation.COUNTERCLOCKWISE_90 -> Vec3(this.z, this.y, -this.x)
}

fun Vec3.toRelative(facing: Direction): Vec3 = when (facing) {
    Direction.UP, Direction.DOWN -> throw IllegalArgumentException("This works only for horizontal facing")
    Direction.NORTH -> this.rotate(Rotation.CLOCKWISE_90)
    Direction.SOUTH -> this.rotate(Rotation.COUNTERCLOCKWISE_90)
    Direction.EAST -> this
    Direction.WEST -> this.rotate(Rotation.CLOCKWISE_180)
}

fun Vec3.fromRelative(facing: Direction): Vec3 = when (facing) {
    Direction.UP, Direction.DOWN -> throw IllegalArgumentException("This works only for horizontal facing")
    Direction.NORTH -> this.rotate(Rotation.COUNTERCLOCKWISE_90)
    Direction.SOUTH -> this.rotate(Rotation.CLOCKWISE_90)
    Direction.EAST -> this
    Direction.WEST -> this.rotate(Rotation.CLOCKWISE_180)
}
