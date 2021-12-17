package ml.denis3d.keys4macros;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    @PreserveNotNull
    @Path("Macros.Macro")
    public List<MacroEntry> macros = new ArrayList<>();

    public static ModConfig load(File file) {
        Config.setInsertionOrderPreserved(true);

        ObjectConverter converter = new ObjectConverter();
        CommentedFileConfig config = CommentedFileConfig.of(file);
        config.load();

        ModConfig modConfig = converter.toObject(config, ModConfig::new);

        config.clear();
        if (modConfig.macros.isEmpty()) {
            modConfig.macros.add(new MacroEntry(80, 0, "/gamemode spectator", "Press P to switch to gamemode spectator"));
        }

        converter.toConfig(modConfig, config);

        config.setComment("Macros", """
                Keys4Macros configuration
                 - Curseforge : https://www.curseforge.com/minecraft/mc-mods/keys4macros
                 - Modrinth : https://modrinth.com/mod/keys4macros
                 - Discord : https://discord.gg/rzzd76c
                 - Github : https://github.com/DenisD3D/Keys4Macros

                Duplicate the 'macro' block for each macro that you want to create
                 - key is a number associated with the key. The list can be found here https://www.glfw.org/docs/3.3/group__keys.html
                 - command is the command to execute (you need to put the / too)
                 - comment is for you to remember what the macro does / the key for it (No impact on behavior)
                 
                Don't forget to press the Reload Config button after any edit to this file""".indent(1));

        config.save();

        return modConfig;
    }

    public static void reload() {
        ObjectConverter converter = new ObjectConverter();
        CommentedFileConfig config = CommentedFileConfig.of(Keys4Macros.CONFIG_FILE);
        converter.toConfig(Keys4Macros.INSTANCE.config, config);

        config.save();

        Keys4Macros.INSTANCE.config = load(Keys4Macros.CONFIG_FILE);
    }

    public static class MacroEntry {
        @PreserveNotNull
        public Integer key = 0;
        @PreserveNotNull
        public Integer modifiers = 0;
        @PreserveNotNull
        public String command = "";
        @PreserveNotNull
        public String comment = "";

        public MacroEntry() {
        }

        public MacroEntry(Integer key, Integer modifiers, String command, String comment) {
            this.key = key;
            this.modifiers = modifiers;
            this.command = command;
            this.comment = comment;
        }
    }
}
