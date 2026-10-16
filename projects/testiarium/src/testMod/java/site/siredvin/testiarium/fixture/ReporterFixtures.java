package site.siredvin.testiarium.fixture;

import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;

/** Native lifecycle metadata without adding the fixture to the global ticker. */
public final class ReporterFixtures {
    private ReporterFixtures() {}

    public static GameTestInfo running(ServerLevel level) throws ReflectiveOperationException {
        TestFunction function = new TestFunction("observer", "observer.child", "empty", 20, 0, true, helper -> {});
        GameTestInfo info = new GameTestInfo(function, Rotation.NONE, level);
        var start = GameTestInfo.class.getDeclaredMethod("startExecution");
        start.setAccessible(true);
        start.invoke(info);
        return info;
    }

    public static void tick(GameTestInfo info) {
        info.tick();
    }

    public static GameTestInfo failed(ServerLevel level, boolean required, String message) throws ReflectiveOperationException {
        TestFunction function = new TestFunction("reporter", "reporter.<fixture>", "empty", 20, 0, required, helper -> {});
        GameTestInfo info = new GameTestInfo(function, Rotation.NONE, level);
        var start = GameTestInfo.class.getDeclaredMethod("startExecution");
        start.setAccessible(true);
        start.invoke(info);
        info.fail(new IllegalStateException(message));
        return info;
    }
}
