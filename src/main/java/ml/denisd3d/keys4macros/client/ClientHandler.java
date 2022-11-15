package ml.denisd3d.keys4macros.client;

import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.client.config.ClientConfig;
import ml.denisd3d.keys4macros.client.screens.FillMacroScreen;
import ml.denisd3d.keys4macros.client.screens.MacrosScreen;
import ml.denisd3d.keys4macros.packets.ServerMacrosPacket;
import ml.denisd3d.keys4macros.structures.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientHandler {

    public static File CONFIG_FILE = new File("config", "keys4macros.toml");
    public KeyMapping openConfigGuiKeyMapping = new KeyMapping("Open Keys4Macros config", GLFW.GLFW_KEY_K, "key.categories.misc");
    public ClientConfig config;
    public String command = null;

    public ClientHandler() {
        this.config = new ClientConfig(CONFIG_FILE, null).loadAndCorrect();
    }

    @SubscribeEvent
    public void onKeyInput(RegisterKeyMappingsEvent event) {
        event.register(this.openConfigGuiKeyMapping);
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void onKeyInputEvent(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null) // Do not process if player in a gui screen
            return;

        if (event.getAction() != GLFW.GLFW_PRESS) // Process only on press
            return;

        if (Minecraft.getInstance().player == null) return;

        for (GlobalMacro globalMacro : Keys4Macros.INSTANCE.clientHandler.config.globalMacros.macros) {
            processMacro(globalMacro, event.getKey(), event.getModifiers());
        }

        for (LocatedMacro locatedMacro : Keys4Macros.INSTANCE.clientHandler.config.locatedMacros.macros) {
            processMacro(locatedMacro, event.getKey(), event.getModifiers());
        }

        for (ForcedMacro forcedMacros : Keys4Macros.INSTANCE.clientHandler.config.forcedMacros.macros) {
            processMacro(forcedMacros, event.getKey(), event.getModifiers());
        }
    }

    public static void processMacro(IMacro macro, int key, int modifiers) {
        if (macro.getKey() != key || macro.getModifiers() != modifiers || (!macro.getLocation().isEmpty() && !macro.getLocation().equals(ClientUtils.getCurrentLocationOrEmpty())) || !macro.isComplete()) // For macro sent by server
            return;

        List<String> variables = ClientUtils.findVariablesInCommand(macro.getCommand());

        if (variables.size() != 0) { // Display GUI for filling variables
            Minecraft.getInstance().setScreen(new FillMacroScreen(macro, variables));
        } else { // Make Action
            ClientUtils.processAction(macro.getMode(), macro.getCommand());
        }
    }

    @SubscribeEvent
    public static void onEvent(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        if (Keys4Macros.INSTANCE.clientHandler.command != null) {
            Minecraft.getInstance().setScreen(new ChatScreen(Keys4Macros.INSTANCE.clientHandler.command));
            Keys4Macros.INSTANCE.clientHandler.command = null;
        }

        if (Keys4Macros.INSTANCE.clientHandler.openConfigGuiKeyMapping.consumeClick()) {
            Minecraft.getInstance().setScreen(new MacrosScreen(null, null));
        }
    }

    public void handleServerMacrosPacket(ServerMacrosPacket msg) {
        for (ServerMacro serverMacro : msg.macros) {
            ForcedMacro forcedMacro = ClientUtils.getForcedMacroById(Keys4Macros.INSTANCE.clientHandler.config.forcedMacros.macros, serverMacro.getId());
            if (forcedMacro != null && forcedMacro.getLocation().equals(ClientUtils.getCurrentLocationOrEmpty())) {
                forcedMacro.setCommand(serverMacro.getCommand());
                forcedMacro.setMode(serverMacro.getMode());
                forcedMacro.setDefaultKey(serverMacro.getKey());
                forcedMacro.setDefaultModifiers(serverMacro.getModifiers());
                forcedMacro.setComplete(true);
            } else {
                ForcedMacro newForcedMacro = new ForcedMacro();
                newForcedMacro.setId(serverMacro.getId());
                newForcedMacro.setCommand(serverMacro.getCommand());
                newForcedMacro.setMode(serverMacro.getMode());
                newForcedMacro.setKey(serverMacro.getKey());
                newForcedMacro.setDefaultKey(serverMacro.getKey());
                newForcedMacro.setModifiers(serverMacro.getModifiers());
                newForcedMacro.setDefaultModifiers(serverMacro.getModifiers());
                newForcedMacro.setLocation(ClientUtils.getCurrentLocationOrEmpty());
                newForcedMacro.setComplete(true);
                Keys4Macros.INSTANCE.clientHandler.config.forcedMacros.macros.add(newForcedMacro);
            }
        }
    }
}
