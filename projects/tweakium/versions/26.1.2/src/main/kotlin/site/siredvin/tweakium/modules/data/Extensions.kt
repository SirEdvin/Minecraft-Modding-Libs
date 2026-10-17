package site.siredvin.tweakium.modules.data

import net.minecraft.resources.Identifier

fun Identifier.toTurtleTranslationKey(): String = "turtle.${this.toString().replace(":", ".")}"

fun Identifier.toPocketTranslationKey(): String = "pocket.${this.toString().replace(":", ".")}"
