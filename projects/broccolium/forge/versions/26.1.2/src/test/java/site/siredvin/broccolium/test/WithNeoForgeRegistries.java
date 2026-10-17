package site.siredvin.broccolium.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.util.Util;
import net.minecraft.world.level.WorldDataConfiguration;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Load real datapacks/components after ModDevGradle's supported FML bootstrap. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(WithNeoForgeRegistries.Setup.class)
public @interface WithNeoForgeRegistries {
    final class Setup implements BeforeAllCallback {
        private static RegistryAccess.Frozen registries;

        @Override
        public void beforeAll(ExtensionContext context) throws Exception {
            bootstrap();
        }

        public static synchronized RegistryAccess.Frozen bootstrap() throws Exception {
            if (registries == null) {
                var packs = ServerPacksSource.createVanillaTrustedRepository();
                var config = new WorldLoader.InitConfig(
                        new WorldLoader.PackConfig(packs, WorldDataConfiguration.DEFAULT, false, true),
                        Commands.CommandSelection.DEDICATED,
                        LevelBasedPermissionSet.OWNER);
                registries = WorldLoader.load(
                        config,
                        context -> new WorldLoader.DataLoadOutput<>(Boolean.TRUE, context.datapackDimensions()),
                        (resources, managers, layers, cookie) -> {
                            try (resources) {
                                return layers.compositeAccess();
                            }
                        },
                        Util.backgroundExecutor(),
                        Runnable::run).get(90, TimeUnit.SECONDS);
            }
            return registries;
        }
    }
}
