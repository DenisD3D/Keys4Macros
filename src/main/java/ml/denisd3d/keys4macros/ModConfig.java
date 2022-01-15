package ml.denisd3d.keys4macros;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import ml.denisd3d.config4j.Comment;
import ml.denisd3d.config4j.Config4J;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModConfig extends Config4J {

    @PreserveNotNull
    @Path("Macros.Macro")
    public List<MacroEntry> macros = new ArrayList<>();

    public ModConfig(File file, Function<String, String> translator) {
        super(file, translator);
    }

    @Override
    public void betweenLoadAndSave() {

    }

    public static class MacroEntry {
        @Path("key")
        @PreserveNotNull
        public Integer key = 0;

        @Path("modifiers")
        @PreserveNotNull
        public Integer modifiers = 0;

        @Path("command")
        @PreserveNotNull
        public String command = "";

        @Path("send")
        @PreserveNotNull
        public boolean send = true;

        public MacroEntry() { }

        public MacroEntry(Integer key, Integer modifiers, String command, boolean send) {
            this.key = key;
            this.modifiers = modifiers;
            this.command = command;
            this.send = send;
        }
    }
}
