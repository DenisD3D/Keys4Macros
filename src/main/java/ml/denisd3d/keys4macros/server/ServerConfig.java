package ml.denisd3d.keys4macros.server;

import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import ml.denisd3d.config4j.Config4J;
import ml.denisd3d.keys4macros.structures.ServerMacro;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ServerConfig extends Config4J {

    @PreserveNotNull
    @Path("ServerMacros")
    public ServerMacros serverMacros = new ServerMacros();

    public ServerConfig(File file, Function<String, String> translator) {
        super(file, translator);
    }

    @Override
    public void betweenLoadAndSave() {
        if (this.serverMacros.macros.isEmpty()) {
            this.serverMacros.macros.add(new ServerMacro());
        }
    }

    public static class ServerMacros {
        @PreserveNotNull
        @Path("Macro")
        List<ServerMacro> macros = new ArrayList<>();
    }
}
