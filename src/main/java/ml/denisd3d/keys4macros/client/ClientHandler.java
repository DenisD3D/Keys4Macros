package ml.denisd3d.keys4macros.client;

import ml.denisd3d.keys4macros.IMacro;
import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.packets.ServerMacrosPacket;
import ml.denisd3d.keys4macros.screens.FillMacroScreen;
import ml.denisd3d.keys4macros.screens.MacrosScreen;
import ml.denisd3d.keys4macros.server.ServerConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientHandler {

    private static final Pattern pattern = Pattern.compile("\\$\\{(.*?)}");
    public static File CONFIG_FILE = new File("config", "keys4macros.toml");
    public KeyMapping openConfigGuiKeyMapping = new KeyMapping("Open Keys4Macros config", GLFW.GLFW_KEY_K, "key.categories.misc");
    public ClientConfig config;
    public List<ServerConfig.MacroEntry> serverMacros;
    public String serverMacrosIp;
    private String command = null;

    @SubscribeEvent(receiveCanceled = true)
    public static void onEvent(InputEvent.KeyInputEvent event) {
        if (Minecraft.getInstance().screen != null)
            return;

        if (event.getAction() == GLFW.GLFW_PRESS) {
            if (Minecraft.getInstance().player == null)
                return;

            for (ClientConfig.MacroEntry macroEntry : Keys4Macros.INSTANCE.clientHandler.config.macros) {
                if (event.getKey() == macroEntry.key && event.getModifiers() == macroEntry.modifiers) {
                    List<String> variables = find_variables(macroEntry.command);
                    if (variables.size() == 0) {
                        if (macroEntry.send) {
                            Minecraft.getInstance().player.chat(macroEntry.command);
                        } else {
                            Keys4Macros.INSTANCE.clientHandler.command = macroEntry.command;
                        }
                    } else {
                        Minecraft.getInstance().setScreen(new FillMacroScreen(macroEntry, variables));
                    }
                }
            }

            for (ServerConfig.MacroEntry macroEntry : Keys4Macros.INSTANCE.clientHandler.serverMacros) {
                if (event.getKey() == macroEntry.getKey() && event.getModifiers() == macroEntry.getModifiers()) {
                    List<String> variables = find_variables(macroEntry.command);
                    if (variables.size() == 0) {
                        if (macroEntry.send) {
                            Minecraft.getInstance().player.chat(macroEntry.command);
                        } else {
                            Keys4Macros.INSTANCE.clientHandler.command = macroEntry.command;
                        }
                    } else {
                        Minecraft.getInstance().setScreen(new FillMacroScreen(macroEntry, variables));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEvent(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;

        if (Keys4Macros.INSTANCE.clientHandler.command != null) {
            Minecraft.getInstance().setScreen(new ChatScreen(Keys4Macros.INSTANCE.clientHandler.command));
            Keys4Macros.INSTANCE.clientHandler.command = null;
        }

        if (Keys4Macros.INSTANCE.clientHandler.openConfigGuiKeyMapping.consumeClick()) {
            Minecraft.getInstance().setScreen(new MacrosScreen(null, null));
        }
    }

    private static List<String> find_variables(String content) {
        Matcher matcher = pattern.matcher(content);
        List<String> variables = new ArrayList<>();

        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        return variables;
    }

    public void setup() {
        this.config = new ClientConfig(CONFIG_FILE, null).loadAndCorrect();
        ClientRegistry.registerKeyBinding(this.openConfigGuiKeyMapping);
    }

    public void build_variable_and_send_write(IMacro macroEntry, List<String> variables) {
        Matcher matcher = pattern.matcher(macroEntry.getCommand());
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = variables.iterator();

        while (matcher.find()) {
            matcher.appendReplacement(stringBuilder, iterator.next());
        }
        matcher.appendTail(stringBuilder);

        if (macroEntry.getSend()) {
            if (Minecraft.getInstance().player == null)
                return;
            Minecraft.getInstance().player.chat(stringBuilder.toString());
        } else {
            this.command = stringBuilder.toString();
        }
    }

    public void handleServerMacrosPacket(ServerMacrosPacket msg) {
        if (Minecraft.getInstance().getCurrentServer() != null) {
            Keys4Macros.INSTANCE.clientHandler.serverMacros = msg.macros;
            Keys4Macros.INSTANCE.clientHandler.serverMacrosIp = Minecraft.getInstance().getCurrentServer().ip;

            for (ServerConfig.MacroEntry macroEntry : Keys4Macros.INSTANCE.clientHandler.serverMacros) {
                for (int i = 0; i < Keys4Macros.INSTANCE.clientHandler.config.server_macros.size(); i++) {
                    if (Keys4Macros.INSTANCE.clientHandler.config.server_macros.get(i).id.intValue() == macroEntry.id && Keys4Macros.INSTANCE.clientHandler.config.server_macros.get(i).server.equals(Keys4Macros.INSTANCE.clientHandler.serverMacrosIp)) {
                        macroEntry.client_key = Keys4Macros.INSTANCE.clientHandler.config.server_macros.get(i).key;
                        macroEntry.client_modifiers = Keys4Macros.INSTANCE.clientHandler.config.server_macros.get(i).modifiers;
                    }
                }
            }
        }
    }
}
