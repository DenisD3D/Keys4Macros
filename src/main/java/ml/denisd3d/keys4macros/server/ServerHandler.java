package ml.denisd3d.keys4macros.server;

import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.packets.ServerMacrosPacket;
import ml.denisd3d.keys4macros.structures.ServerMacro;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.List;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class ServerHandler {

    public static File CONFIG_FILE = new File("config", "keys4macros.toml");
    public ServerConfig config;
    private boolean config_is_errored = false;

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        RestartCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (Keys4Macros.INSTANCE.serverHandler.config_is_errored)
            return;

        List<ServerMacro> filteredEntries = Keys4Macros.INSTANCE.serverHandler.config.serverMacros.macros.stream().filter(macroEntry -> !macroEntry.getCommand().isEmpty()).toList();
        if (filteredEntries.size() != 0) {
            Keys4Macros.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new ServerMacrosPacket(filteredEntries));
        }
    }

    public void setup() {
        readConfig();
    }

    public boolean readConfig() {
        this.config = new ServerConfig(CONFIG_FILE, null).loadAndCorrect();

        if (this.config.serverMacros.macros.stream().map(ServerMacro::getId).distinct().count() != this.config.serverMacros.macros.size()) {
            LogManager.getLogger().error("At least 2 entry have the same id in the server config. Please delete the line starting by id= for the created macros. A new id will be given automatically");
            this.config_is_errored = true;
            return false;
        }
        this.config_is_errored = false;
        return true;
    }
}
