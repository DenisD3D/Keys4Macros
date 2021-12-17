package ml.denis3d.keys4macros;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.io.File;

@Mod("keys4macros")
public class Keys4Macros {
    public static File CONFIG_FILE = new File("config", "keys4macros.toml");
    public static Keys4Macros INSTANCE;
    public ModConfig config;

    public Keys4Macros() {
        System.setProperty("java.awt.headless", "false");
        MinecraftForge.EVENT_BUS.register(this);
        this.config = ModConfig.load(CONFIG_FILE);

        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory(ConfigScreen::new));

        INSTANCE = this;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onEvent(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().screen != null)
            return;
        if (event.getAction() == GLFW.GLFW_PRESS) {
            if (Minecraft.getInstance().player != null) {
                for (ModConfig.MacroEntry macroEntry : this.config.macros) {
                    if (event.getKey() == macroEntry.key && event.getModifiers() == macroEntry.modifiers) {
                        Minecraft.getInstance().player.chat(macroEntry.command);
                        break;
                    }
                }
            }
        }
    }
}
