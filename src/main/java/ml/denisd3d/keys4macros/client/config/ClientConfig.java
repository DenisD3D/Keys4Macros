package ml.denisd3d.keys4macros.client.config;

import com.electronwill.nightconfig.core.conversion.Path;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import ml.denisd3d.config4j.Config4J;
import ml.denisd3d.keys4macros.structures.GlobalMacro;
import ml.denisd3d.keys4macros.structures.LocatedMacro;
import ml.denisd3d.keys4macros.structures.ForcedMacro;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ClientConfig extends Config4J {

    @PreserveNotNull
    @Path("LocalMacros")
    public GlobalMacros globalMacros = new GlobalMacros();

    @PreserveNotNull
    @Path("LocatedMacros")
    public LocatedMacros locatedMacros = new LocatedMacros();

    @PreserveNotNull
    @Path("ForcedMacros")
    public ForcedMacros forcedMacros = new ForcedMacros();

    public ClientConfig(File file, Function<String, String> translator) {
        super(file, translator);
    }

    @Override
    public void betweenLoadAndSave() {
        if (this.globalMacros.macros.size() == 0) {
            this.globalMacros.macros.add(new GlobalMacro());
        }
    }

    public static class GlobalMacros {
        @PreserveNotNull
        @Path("Macro")
        public List<GlobalMacro> macros = new ArrayList<>();
    }

    public static class LocatedMacros {
        @PreserveNotNull
        @Path("Macro")
        public List<LocatedMacro> macros = new ArrayList<>();
    }

    public static class ForcedMacros {
        @PreserveNotNull
        @Path("Macro")
        public List<ForcedMacro> macros = new ArrayList<>();
    }
}
