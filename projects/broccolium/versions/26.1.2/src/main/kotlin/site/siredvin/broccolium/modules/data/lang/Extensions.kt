package site.siredvin.broccolium.modules.data.lang

import net.minecraft.resources.Identifier

fun Identifier.toStatTranslationKey(): String = "stat.${this.namespace}.${this.path}"
