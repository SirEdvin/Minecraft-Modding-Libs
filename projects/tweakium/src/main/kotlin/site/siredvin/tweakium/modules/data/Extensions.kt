package site.siredvin.tweakium.modules.data

import net.minecraft.resources.ResourceLocation

fun ResourceLocation.toTurtleTranslationKey(): String = "turtle.${this.toString().replace(":", ".")}"

fun ResourceLocation.toPocketTranslationKey(): String = "pocket.${this.toString().replace(":", ".")}"
