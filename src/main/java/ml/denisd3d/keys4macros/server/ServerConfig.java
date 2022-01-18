package ml.denisd3d.keys4macros.server;

import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import ml.denisd3d.config4j.Config4J;
import ml.denisd3d.keys4macros.IMacro;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ServerConfig extends Config4J {

    @PreserveNotNull
    @Path("Macros.Macro")
    public List<MacroEntry> macros = new ArrayList<>();

    public ServerConfig(File file, Function<String, String> translator) {
        super(file, translator);
    }

    @Override
    public void betweenLoadAndSave() {
        if (this.macros.isEmpty()) {
            this.macros.add(new MacroEntry(0, -1, 0, "", true));
        }
    }

    public static class MacroEntry implements IMacro {
        @Path("id")
        @PreserveNotNull
        public Integer id = 0;

        @Path("key")
        @PreserveNotNull
        public Integer default_key = -1;

        @Path("modifiers")
        @PreserveNotNull
        public Integer default_modifiers = 0;

        @Path("command")
        @PreserveNotNull
        public String command = "";

        @Path("send")
        @PreserveNotNull
        public boolean send = true;

        public transient Integer client_key = null;
        public transient Integer client_modifiers = null;

        public MacroEntry() {
        }

        public MacroEntry(Integer id, Integer default_key, Integer default_modifiers, String command, boolean send) {
            this.id = id;
            this.default_key = default_key;
            this.default_modifiers = default_modifiers;
            this.command = command;
            this.send = send;
        }

        @Override
        public int getKey() {
            return this.client_key == null ? this.default_key : this.client_key;
        }

        @Override
        public void setKey(int key) {
            this.client_key = key;
        }

        @Override
        public int getModifiers() {
            return this.client_modifiers == null ? this.default_modifiers : this.client_modifiers;
        }

        @Override
        public void setModifiers(int modifiers) {
            this.client_modifiers = modifiers;
        }

        @Override
        public String getCommand() {
            return this.command;
        }

        @Override
        public void setCommand(String command) {
            this.command = command;
        }

        @Override
        public boolean getSend() {
            return this.send;
        }

        @Override
        public void setSend(boolean send) {
            this.send = send;
        }
    }
}
