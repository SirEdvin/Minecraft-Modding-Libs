package site.siredvin.tweakium.modules.turtle.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.mojang.serialization.MapCodec
import dan200.computercraft.api.client.turtle.TurtleUpgradeModel
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.upgrades.UpgradeData
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.item.ItemModelResolver
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.item.TrackingItemStackRenderState
import net.minecraft.client.renderer.special.SpecialModelRenderer
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.client.resources.model.ResolvableModel
import net.minecraft.client.resources.model.cuboid.ItemTransform
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Matrix4f
import org.joml.Vector3fc
import site.siredvin.tweakium.TweakiumCore
import java.util.function.Consumer

class FacingBlockTurtleModeller<T : ITurtleUpgrade> : TurtleUpgradeModel {
    companion object {
        val ID: Identifier = Identifier.fromNamespaceAndPath(TweakiumCore.MOD_ID, "facing_block")
        val CODEC: MapCodec<TurtleUpgradeModel.Unbaked> = MapCodec.unit(Unbaked)
        private val LEFT = TransformedRenderer(transform(TurtleSide.LEFT))
        private val RIGHT = TransformedRenderer(transform(TurtleSide.RIGHT))

        private fun transform(side: TurtleSide): Matrix4f = Matrix4f()
            .scale(0.3f, 0.3f, 0.3f)
            .rotate(Axis.XN.rotationDegrees(90f))
            .translate(0f, -2f, 1.05f)
            .translate(if (side == TurtleSide.LEFT) -0.6f else 2.9f, 0f, 0f)
    }

    override fun renderForItem(upgrade: UpgradeData<ITurtleUpgrade>, side: TurtleSide, renderer: ItemStackRenderState, resolver: ItemModelResolver, transform: ItemTransform, seed: Int) {
        val child = TrackingItemStackRenderState()
        // Resolve the immutable crafting definition only at model extraction.
        resolver.updateForTopItem(child, upgrade.upgrade().craftingItem.create(), ItemDisplayContext.NONE, null, null, seed)
        if (child.isEmpty) return
        renderer.appendModelIdentityElement(this)
        renderer.appendModelIdentityElement(side)
        renderer.appendModelIdentityElement(child.modelIdentity)
        renderer.appendModelIdentityElement(transform)
        val layer = renderer.newLayer()
        layer.setItemTransform(transform)
        layer.setupSpecialModel(if (side == TurtleSide.LEFT) LEFT else RIGHT, child)
    }

    private object Unbaked : TurtleUpgradeModel.Unbaked {
        override fun type(): MapCodec<out TurtleUpgradeModel.Unbaked> = CODEC
        override fun bake(baker: ModelBaker): TurtleUpgradeModel = FacingBlockTurtleModeller<ITurtleUpgrade>()
        override fun resolveDependencies(resolver: ResolvableModel.Resolver) = Unit
    }

    private class TransformedRenderer(private val transform: Matrix4f) : SpecialModelRenderer<ItemStackRenderState> {
        override fun submit(state: ItemStackRenderState?, poses: PoseStack, sink: SubmitNodeCollector, light: Int, overlay: Int, foil: Boolean, outlineColour: Int) {
            if (state == null) return
            poses.pushPose()
            try {
                poses.mulPose(transform)
                state.submit(poses, sink, light, overlay, outlineColour)
            } finally {
                poses.popPose()
            }
        }

        override fun getExtents(set: Consumer<Vector3fc>) = Unit
        override fun extractArgument(itemStack: ItemStack): ItemStackRenderState? = null
    }
}
