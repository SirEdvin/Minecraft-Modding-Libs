package site.siredvin.broccolium.test

import net.neoforged.fml.loading.LoadingModList
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext

/** [WithMinecraft] for NeoForge, whose patched vanilla bootstrap reads FML's mod list. */
@Target(AnnotationTarget.CLASS)
@ExtendWith(WithNeoForgeMinecraft.Setup::class)
annotation class WithNeoForgeMinecraft {
    class Setup : BeforeAllCallback {
        override fun beforeAll(context: ExtensionContext) {
            // shortcut: an empty mod list stands in for FML, so nothing here runs with loaded mods; move to ModDevGradle's unitTest support when a test needs them.
            if (LoadingModList.get() == null) LoadingModList.of(emptyList(), emptyList(), emptyList(), emptyList(), emptyMap())
            WithMinecraft.Setup.bootstrap()
        }
    }
}
