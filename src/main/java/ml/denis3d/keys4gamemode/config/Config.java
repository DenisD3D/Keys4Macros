package ml.denis3d.keys4gamemode.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static final ForgeConfigSpec CLIENT_SPECS;
    public static final Client CLIENT;

    static {
        Pair<Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPECS = clientPair.getRight();
        CLIENT = clientPair.getLeft();
    }

    public static class Client {
        //Discord config
        public final ForgeConfigSpec.ConfigValue<String> macroOne;
        public final ForgeConfigSpec.ConfigValue<String> macroTwo;
        public final ForgeConfigSpec.ConfigValue<String> macroThree;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment(" Define here the commands assigned to your macro. You can use the in-game GUI located under the \"Config\" button of Keys4Gamemode in the forge mod list GUI.")
                    .push("Macro Commands");

            macroOne = builder
                    .comment(" Command for the macro one")
                    .define("macroOne", "/tellraw @s {\"text\":\"This macro isn't configured yet ! Go to the mod option to set it.\"}");

            macroTwo = builder
                    .comment(" Command for the macro one")
                    .define("macroTwo", "/tellraw @s {\"text\":\"This macro isn't configured yet ! Go to the mod option to set it.\"}");

            macroThree = builder
                    .comment(" Command for the macro one")
                    .define("macroThree", "/tellraw @s {\"text\":\"This macro isn't configured yet ! Go to the mod option to set it.\"}");
            builder.pop();
        }
    }
}
