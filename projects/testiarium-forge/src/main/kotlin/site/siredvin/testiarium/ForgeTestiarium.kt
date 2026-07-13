package site.siredvin.testiarium

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(Testiarium.MOD_ID)
object ForgeTestiarium {
    init {
        Testiarium.init()
    }

    @JvmStatic
    fun registerTests() {
        FMLJavaModLoadingContext.get().modEventBus.addListener { event: net.minecraftforge.event.RegisterGameTestsEvent ->
            Testiarium.loadTests(event::register)
        }
    }
}
