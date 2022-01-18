package ml.denisd3d.keys4macros.client;

import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import it.unimi.dsi.fastutil.Hash;
import ml.denisd3d.config4j.Config4J;
import ml.denisd3d.keys4macros.IMacro;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class ClientConfig extends Config4J {

    @PreserveNotNull
    @Path("Macros.Macro")
    public List<MacroEntry> macros = new ArrayList<>();

    @PreserveNotNull
    @Path("ServerMacros.ServerMacro")
    public List<ServerMacroEntry> server_macros = new ArrayList<>();

    @PreserveNotNull
    @Path("Test")
    public HashMap<String, String> test = new HashMap<>();

    public ClientConfig(File file, Function<String, String> translator) {
        super(file, translator);
    }

    @Override
    public void betweenLoadAndSave() {
        if (test.isEmpty())
            test.put("a", "b");
    }

    public static class MacroEntry implements IMacro {
        @Path("key")
        @PreserveNotNull
        public Integer key = -1;

        @Path("modifiers")
        @PreserveNotNull
        public Integer modifiers = 0;

        @Path("command")
        @PreserveNotNull
        public String command = "";

        @Path("send")
        @PreserveNotNull
        public boolean send = true;

        @Path("server")
        @PreserveNotNull
        public String server = "";

        public MacroEntry() {
        }

        public MacroEntry(Integer key, Integer modifiers, String command, boolean send, String server) {
            this.key = key;
            this.modifiers = modifiers;
            this.command = command;
            this.send = send;
            this.server = server;
        }

        @Override
        public int getKey() {
            return this.key;
        }

        @Override
        public void setKey(int key) {
            this.key = key;
        }

        @Override
        public int getModifiers() {
            return this.modifiers;
        }

        @Override
        public void setModifiers(int modifiers) {
            this.modifiers = modifiers;
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

    public static class ServerMacroEntry {

        @Path("id")
        @PreserveNotNull
        public Integer id = 0;

        @Path("server")
        @PreserveNotNull
        public String server = "";

        @Path("key")
        @PreserveNotNull
        public Integer key = -1;

        @Path("modifiers")
        @PreserveNotNull
        public Integer modifiers = 0;

        public ServerMacroEntry() {
        }

        public ServerMacroEntry(Integer id, String server, Integer key, Integer modifiers) {
            this.id = id;
            this.server = server;
            this.key = key;
            this.modifiers = modifiers;
        }
    }
}
