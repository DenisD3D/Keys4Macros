package ml.denisd3d.keys4macros.client;

import com.google.common.collect.Streams;
import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.structures.ForcedMacro;
import ml.denisd3d.keys4macros.structures.IMacro;
import ml.denisd3d.keys4macros.structures.ProcessMode;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ClientUtils {

    private static final Pattern pattern = Pattern.compile("\\$\\{(.*?)}"); // ${variable} regex matcher

    /**
     * @return The current location or empty if not connected
     */
    public static String getCurrentLocationOrEmpty() {
        if (Minecraft.getInstance().getConnection() == null) // We are not connected to any server
            return "";

        if (Minecraft.getInstance().getSingleplayerServer() != null) { // We are on a singleplayer server
            return Minecraft.getInstance().getSingleplayerServer().getWorldPath(new LevelResource("")).toFile().getPath();
        } else { // We are on a multiplayer server
            if (Minecraft.getInstance().getCurrentServer() == null) // This case shouldn't happen
                return "";
            return Minecraft.getInstance().getCurrentServer().ip;
        }
    }

    /**
     * @param command The macro command to parse
     * @return The list of variables found
     */
    public static List<String> findVariablesInCommand(String command) {
        Matcher matcher = pattern.matcher(command);
        List<String> variables = new ArrayList<>();

        while (matcher.find()) {
            if (!variables.contains(matcher.group(1)))
                variables.add(matcher.group(1));
        }
        return variables;
    }

    /**
     * @param mode    The mode to process the command with
     * @param command The command
     */
    public static void processAction(ProcessMode mode, String command) {
        String[] messages = command.split("(?<!\\\\)(?:\\\\\\\\)*\\\\n");

        for (String message : messages) {
            switch (mode) {
                case SEND -> {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.chat(message.replace("\\\\n", "\\n"));
                    }
                }
                case WRITE -> Keys4Macros.INSTANCE.clientHandler.command = message.replace("\\\\n", "\\n");
            }
        }
    }

    /**
     * @param command   The macro command to replace in
     * @param variables An HashMap of replacement with key the
     * @return The built command
     */
    public static String replaceVariablesInCommand(String command, HashMap<String, String> variables) {
        Matcher matcher = pattern.matcher(command);
        StringBuilder stringBuilder = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(stringBuilder, variables.get(matcher.group(1).split("\\|")[0]));
        }
        matcher.appendTail(stringBuilder);

        return stringBuilder.toString();
    }

    /**
     * @param macros The list of forced macros to search in
     * @param uuid   The uuid to find
     * @return The macro with th uuid
     */
    public static ForcedMacro getForcedMacroById(List<ForcedMacro> macros, UUID uuid) {
        return macros.stream().filter(macro -> macro.getId().equals(uuid)).findFirst().orElse(null);
    }

    public static List<? extends IMacro> getMacrosForLocation(String location) {
        Stream<? extends IMacro> locatedMacroStream = Keys4Macros.INSTANCE.clientHandler.config.locatedMacros.macros.stream()
                .filter(macro -> macro.getLocation().equals(location));
        Stream<? extends IMacro> forcedMacroStream = Keys4Macros.INSTANCE.clientHandler.config.forcedMacros.macros.stream()
                .filter(macro -> macro.getLocation().equals(location));

        return Streams.concat(locatedMacroStream, forcedMacroStream).toList();
    }

    public static IMacro getMacroById(UUID id) {
        return Keys4Macros.INSTANCE.clientHandler.config.globalMacros.macros.stream().map(globalMacro -> (IMacro) globalMacro)
                .filter(globalMacro -> globalMacro.getId().equals(id))
                .findFirst()
                .orElse(Keys4Macros.INSTANCE.clientHandler.config.locatedMacros.macros.stream()
                        .filter(macro -> macro.getId().equals(id))
                        .findFirst()
                        .orElse(null)
                );
    }
}
