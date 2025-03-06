package site.siredvin.broccolium.modules.base.util

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

fun itemTooltip(descriptionId: String): MutableComponent = Component.translatable("$descriptionId.tooltip")

fun itemExtra(descriptionId: String, extra: String): MutableComponent = Component.translatable("$descriptionId.extra.$extra")

fun itemExtra(descriptionId: String, extra: String, vararg args: Any): MutableComponent = Component.translatable("$descriptionId.extra.$extra", *args)

/* This strange piece of CC:T integrations is here because it is always in my heart */
fun turtleAdjective(turtleID: ResourceLocation): String = java.lang.String.format("turtle.%s.%s", turtleID.namespace, turtleID.path)

fun turtleAdjectiveComponent(turtleID: ResourceLocation): Component = Component.translatable(java.lang.String.format("turtle.%s.%s", turtleID.namespace, turtleID.path))

fun pocketAdjective(pocketID: ResourceLocation): String = java.lang.String.format("pocket.%s.%s", pocketID.namespace, pocketID.path)

fun pocketAdjectiveComponent(pocketID: ResourceLocation): Component = Component.translatable(java.lang.String.format("pocket.%s.%s", pocketID.namespace, pocketID.path))
