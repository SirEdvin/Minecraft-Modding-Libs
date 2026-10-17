package site.siredvin.testiarium.mixin;

import java.util.List;
import java.util.Map;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.NetworkRegistryLoadTask;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import site.siredvin.testiarium.Testiarium;

@Mixin(RegistryDataLoader.class)
abstract class RegistryDataLoaderMixin {
    // Register after resource loading, before registry freeze. Each world/reload owns its registries.
    @Inject(method = "lambda$load$2", at = @At("HEAD"))
    private static void testiarium$register(List<RegistryLoadTask<?>> tasks, Map<ResourceKey<?>, Exception> errors, Void ignored, CallbackInfoReturnable<RegistryAccess.Frozen> callback) {
        WritableRegistry<TestEnvironmentDefinition<?>> environments = null;
        WritableRegistry<GameTestInstance> tests = null;
        for (var task : tasks) {
            // Synced client registries already contain the server's instances/environments.
            if (task instanceof NetworkRegistryLoadTask<?>) return;
            var registry = ((RegistryLoadTaskAccessor) task).testiarium$registry();
            if (registry.key() == Registries.TEST_ENVIRONMENT) environments = cast(registry);
            if (registry.key() == Registries.TEST_INSTANCE) tests = cast(registry);
        }
        if (environments == null || tests == null) return;
        var environmentRegistry = environments;
        var testRegistry = tests;
        Testiarium.registerInstances(
            (id, environment) -> environmentRegistry.register(ResourceKey.create(Registries.TEST_ENVIRONMENT, id), environment, RegistrationInfo.BUILT_IN),
            (id, test) -> testRegistry.register(ResourceKey.create(Registries.TEST_INSTANCE, id), test, RegistrationInfo.BUILT_IN)
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> WritableRegistry<T> cast(WritableRegistry<?> registry) {
        return (WritableRegistry<T>) registry;
    }
}
