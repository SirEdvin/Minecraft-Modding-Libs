package site.siredvin.tweakium.modules.turtle;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

/** Repair just the upgrade fields before CC rewrites their untyped legacy data. */
public final class LegacyTurtleUpgradeFix {
    private LegacyTurtleUpgradeFix() {}

    public static Function<Typed<?>, Typed<?>> before(
        Schema schema, Function<Typed<?>, Typed<?>> original
    ) {
        return original.compose(choice(schema, "computercraft:turtle_normal")
            .andThen(choice(schema, "computercraft:turtle_advanced")));
    }

    private static Function<Typed<?>, Typed<?>> choice(Schema schema, String id) {
        var finder = DSL.namedChoice(id, schema.getChoiceType(References.BLOCK_ENTITY, id));
        return typed -> typed.updateTyped(finder, turtle -> {
            var remainder = turtle.get(DSL.remainderFinder());
            var result = turtle;
            for (String side : new String[] {"Left", "Right"}) {
                String key = side + "Upgrade";
                String dataKey = side + "UpgradeNbt";
                var data = remainder.get(dataKey).result();
                if (data.isEmpty()) continue;
                var upgradeFinder = result.getType().findField(key);
                result = result.updateTyped(upgradeFinder, upgrade -> {
                    // Serializing the whole turtle here would traverse item-stack types
                    // while the adjacent vanilla componentization rule is changing them.
                    Dynamic<?> raw = upgrade.write().getOrThrow();
                    Dynamic<?> wrapper = raw.emptyMap().set("id", raw.createString(id))
                        .set(key, raw).set(dataKey, data.get());
                    Dynamic<?> fixed = LegacyTurtleUpgradeData.INSTANCE.restore(wrapper)
                        .get(key).result().orElseThrow();
                    return upgrade.getType().readTyped(fixed).getOrThrow().getFirst();
                });
            }
            return result;
        });
    }
}
