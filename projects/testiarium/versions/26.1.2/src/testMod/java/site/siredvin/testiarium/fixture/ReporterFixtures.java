package site.siredvin.testiarium.fixture;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.RetryOptions;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;

/** Private native instance holders: these children never enter the world's registry/ticker. */
public final class ReporterFixtures {
    private ReporterFixtures() {}

    private static GameTestInfo create(ServerLevel level, boolean required) {
        var registry = new MappedRegistry<GameTestInstance>(Registries.TEST_INSTANCE, Lifecycle.stable());
        var id = Identifier.fromNamespaceAndPath("testiarium", "observer.child");
        var instance = new FunctionGameTestInstance(
            ResourceKey.create(Registries.TEST_FUNCTION, id),
            new TestData<>(Holder.direct(new TestEnvironmentDefinition.AllOf()), Identifier.withDefaultNamespace("empty"), 20, 0, required)
        );
        var holder = registry.register(ResourceKey.create(Registries.TEST_INSTANCE, id), instance, RegistrationInfo.BUILT_IN);
        return new GameTestInfo(holder, Rotation.NONE, level, RetryOptions.noRetries()).startExecution(0);
    }

    public static GameTestInfo running(ServerLevel level) {
        return create(level, true);
    }

    public static void tick(GameTestInfo info) {
        info.tick(null);
    }

    public static GameTestInfo failed(ServerLevel level, boolean required, String message) {
        var info = create(level, required);
        info.fail(new GameTestAssertException(Component.literal(message), 0));
        return info;
    }
}
