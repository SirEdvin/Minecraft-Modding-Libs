// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0
//
// Shameless copy
package site.siredvin.tweakium.test;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import site.siredvin.tweakium.modules.FabricTweakium;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bootstrap Minecraft before running these tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(WithTweakium.Setup.class)
public @interface WithTweakium {
    class Setup implements Extension, BeforeAllCallback {
        @Override
        public void beforeAll(ExtensionContext context) {
            bootstrap();
        }

        public static void bootstrap() {
            FabricTweakium.INSTANCE.sayHi();
        }
    }
}
