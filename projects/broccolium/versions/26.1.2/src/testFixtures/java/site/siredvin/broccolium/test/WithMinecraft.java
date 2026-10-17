// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0
//
// Shameless copy
package site.siredvin.broccolium.test;

import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bootstrap vanilla registries and their deferred default components for common tests.
 * Loader-dependent behavior still requires the native loader-run fixtures.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(WithMinecraft.Setup.class)
public @interface WithMinecraft {
    class Setup implements Extension, BeforeAllCallback {
        private static HolderLookup.Provider registries;

        @Override
        public void beforeAll(ExtensionContext context) {
            bootstrap();
        }

        public static synchronized void bootstrap() {
            if (registries != null) return;
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            var lookup = VanillaRegistries.createLookup();
            BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(lookup).forEach(pending -> pending.apply());
            registries = lookup;
        }

        public static synchronized HolderLookup.Provider registries() {
            bootstrap();
            return registries;
        }
    }
}
