package site.siredvin.broccolium.test;

import com.google.auto.service.AutoService;
import org.junit.jupiter.api.extension.Extension;
import site.siredvin.broccolium.BroccoliumCore;
import site.siredvin.broccolium.modules.platform.FabricPlatformIngredients;
import site.siredvin.broccolium.modules.platform.FabricPlatformTags;
import site.siredvin.broccolium.modules.platform.FabricPlatformToolkit;

@AutoService(Extension.class)
public class BroccoliumInitialization implements Extension{

    public BroccoliumInitialization() {
        BroccoliumCore.INSTANCE.configure(FabricPlatformToolkit.INSTANCE, FabricPlatformTags.INSTANCE, FabricPlatformIngredients.INSTANCE);
    }
}
