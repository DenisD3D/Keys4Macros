package ml.denis3d.keys4gamemode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("keys4gamemode")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Keys4Gamemode {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final KeyBinding keyBindGamemode0 = new KeyBinding("key.gm_survival", GLFW.GLFW_KEY_KP_1, "key.categories.keys4gamemode");
    public static final KeyBinding keyBindGamemode1 = new KeyBinding("key.gm_creative", GLFW.GLFW_KEY_KP_2, "key.categories.keys4gamemode");
    public static final KeyBinding keyBindGamemode2 = new KeyBinding("key.gm_adventure", GLFW.GLFW_KEY_KP_0, "key.categories.keys4gamemode");
    public static final KeyBinding keyBindGamemode3 = new KeyBinding("key.gm_spectator", GLFW.GLFW_KEY_KP_3, "key.categories.keys4gamemode");
    public static final KeyBinding keyBindTimeSetDay = new KeyBinding("key.timesetday", GLFW.GLFW_KEY_KP_4, "key.categories.keys4gamemode");
    public static final KeyBinding keyBindTimeSetNight = new KeyBinding("key.timesetnight", GLFW.GLFW_KEY_KP_5, "key.categories.keys4gamemode");
    public static final KeyBinding keyBindWeatherClear = new KeyBinding("key.weatherclear", GLFW.GLFW_KEY_KP_6, "key.categories.keys4gamemode");
    public static KeyBinding[] keyBindings = new KeyBinding[]{keyBindGamemode0, keyBindGamemode1, keyBindGamemode2, keyBindGamemode3, keyBindTimeSetDay, keyBindTimeSetNight, keyBindWeatherClear};

    public Keys4Gamemode() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        for (KeyBinding keyBinding: keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority= EventPriority.NORMAL, receiveCanceled=true)
    public static void onEvent(InputEvent.KeyInputEvent event)
    {
        if (keyBindings[0].isPressed())
        {
            Minecraft.getInstance().player.sendChatMessage("/gamemode survival");
        }

        if (keyBindings[1].isPressed())
        {
            Minecraft.getInstance().player.sendChatMessage("/gamemode creative");
        }

        if (keyBindings[2].isPressed())
        {
            Minecraft.getInstance().player.sendChatMessage("/gamemode adventure");
        }

        if (keyBindings[3].isPressed())
        {
            Minecraft.getInstance().player.sendChatMessage("/gamemode spectator");
        }

        if (keyBindings[4].isPressed())
        {
            Minecraft.getInstance().player.sendChatMessage("/time set day");
        }

        if (keyBindings[5].isPressed())
        {
            Minecraft.getInstance().player.sendChatMessage("/time set night");
        }

        if (keyBindings[6].isPressed())
        {
            Minecraft.getInstance().player.sendChatMessage("/weather clear");
        }
    }
}