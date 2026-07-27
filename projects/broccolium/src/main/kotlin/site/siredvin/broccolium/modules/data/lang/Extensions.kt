package site.siredvin.broccolium.modules.data.lang

import net.minecraft.resources.ResourceLocation

fun ResourceLocation.toStatTranslationKey(): String = "stat.${this.namespace}.${this.path}"
