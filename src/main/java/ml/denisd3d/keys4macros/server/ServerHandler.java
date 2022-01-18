package ml.denisd3d.keys4macros.server;

import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.packets.ServerMacrosPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.io.File;
import java.util.List;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class ServerHandler {

    public static File CONFIG_FILE = new File("config", "keys4macros.toml");
    public ServerConfig config;

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        RestartCommand.register(event.getDispatcher());
    }

    public void setup() {
        readConfig();
    }

    @SubscribeEvent
    public static void onEvent(PlayerEvent.PlayerLoggedInEvent event) {
        List<ServerConfig.MacroEntry> filteredEntries = Keys4Macros.INSTANCE.serverHandler.config.macros.stream().filter(macroEntry -> !macroEntry.command.isEmpty()).toList();
        if (filteredEntries.size() != 0) {
            Keys4Macros.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()),
                    new ServerMacrosPacket(filteredEntries));
        }
    }

    public void readConfig() {
        this.config = new ServerConfig(CONFIG_FILE, null).loadAndCorrect();
    }
}
