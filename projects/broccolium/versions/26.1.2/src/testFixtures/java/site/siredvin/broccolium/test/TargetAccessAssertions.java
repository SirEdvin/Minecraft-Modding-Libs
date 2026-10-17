package site.siredvin.broccolium.test;

import java.lang.reflect.Modifier;
import site.siredvin.broccolium.modules.storage.item.LimitedInventory;

/** Checks loader-transformed classes, never setAccessible or the authored access text alone. */
public final class TargetAccessAssertions {
    private TargetAccessAssertions() {}

    public static void verify() throws ReflectiveOperationException {
        field("net.minecraft.world.entity.animal.equine.AbstractHorse", "inventory", "net.minecraft.world.SimpleContainer");
        field("net.minecraft.client.data.models.BlockModelGenerators", "blockStateOutput", "java.util.function.Consumer");
        field("net.minecraft.client.data.models.BlockModelGenerators", "modelOutput", "java.util.function.BiConsumer");
        field("net.minecraft.client.data.models.BlockModelGenerators", "itemModelOutput", "net.minecraft.client.data.models.ItemModelOutput");
        field("net.minecraft.client.data.models.ItemModelGenerators", "modelOutput", "java.util.function.BiConsumer");
        field("net.minecraft.client.data.models.ItemModelGenerators", "itemModelOutput", "net.minecraft.client.data.models.ItemModelOutput");
        var texture = load("net.minecraft.client.data.models.model.TextureSlot");
        var create = texture.getDeclaredMethod("create", String.class);
        if (!Modifier.isPublic(create.getModifiers()) || !Modifier.isStatic(create.getModifiers()) || create.getReturnType() != texture) {
            throw new AssertionError("TextureSlot.create(String) access/descriptor changed: " + create);
        }
        if (Modifier.isFinal(LimitedInventory.class.getModifiers())) throw new AssertionError("LimitedInventory must remain open");
        System.out.println("Verified seven transformed native access members and open LimitedInventory");
    }

    private static Class<?> load(String name) throws ClassNotFoundException {
        return Class.forName(name, false, TargetAccessAssertions.class.getClassLoader());
    }

    private static void field(String owner, String name, String type) throws ReflectiveOperationException {
        var field = load(owner).getDeclaredField(name);
        if (!Modifier.isPublic(field.getModifiers()) || !field.getType().getName().equals(type)) {
            throw new AssertionError("Native field access/descriptor changed: " + field);
        }
    }
}
