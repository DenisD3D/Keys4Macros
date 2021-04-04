package ml.denis3d.keys4macros;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
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
        MinecraftForge.EVENT_BUS.register(this);
        this.config = ModConfig.load(CONFIG_FILE);

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> ConfigScreen::new);

        INSTANCE = this;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onEvent(InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS && this.config.macros_map.containsKey(event.getKey())) {
            if (Minecraft.getInstance().player != null)
                Minecraft.getInstance().player.sendChatMessage(this.config.macros_map.get(event.getKey()).command);
        }
    }
}
