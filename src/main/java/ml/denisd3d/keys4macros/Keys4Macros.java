package ml.denisd3d.keys4macros;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

import java.io.File;

@Mod("keys4macros")
public class Keys4Macros {
    public static File CONFIG_FILE = new File("config", "keys4macros.toml");
    public static Keys4Macros INSTANCE;
    public KeyMapping openConfigGuiKeyMapping = new KeyMapping("Open Keys4Macros config", GLFW.GLFW_KEY_K, "key.categories.misc");
    public ModConfig config;
    private String command = null;

    public Keys4Macros() {
        MinecraftForge.EVENT_BUS.register(this);
        this.config = new ModConfig(CONFIG_FILE, null).loadAndCorrect();

        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory(MacrosScreen::new));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        INSTANCE = this;
    }

    private void setupClient(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(this.openConfigGuiKeyMapping);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onEvent(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().screen != null)
            return;

        if (event.getAction() == GLFW.GLFW_PRESS) {
            if (Minecraft.getInstance().player == null)
                return;

            for (ModConfig.MacroEntry macroEntry : this.config.macros) {
                if (event.getKey() == macroEntry.key && event.getModifiers() == macroEntry.modifiers) {
                    if (macroEntry.send) {
                        Minecraft.getInstance().player.chat(macroEntry.command);
                    } else {
                        this.command = macroEntry.command;
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onEvent(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;

        if (this.command != null) {
            Minecraft.getInstance().setScreen(new ChatScreen(this.command));
            this.command = null;
        }

        if (this.openConfigGuiKeyMapping.consumeClick()) {
            Minecraft.getInstance().setScreen(new MacrosScreen(null, null));
        }
    }
}
