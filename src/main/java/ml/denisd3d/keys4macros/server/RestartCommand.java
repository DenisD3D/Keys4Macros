package ml.denisd3d.keys4macros.server;

import com.mojang.brigadier.CommandDispatcher;
import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.packets.ServerMacrosPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class RestartCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("keys4macros")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(3))
                .then(Commands.literal("restart").executes(context -> {
                    Keys4Macros.INSTANCE.serverHandler.readConfig();

                    List<ServerConfig.MacroEntry> filteredEntries = Keys4Macros.INSTANCE.serverHandler.config.macros.stream().filter(macroEntry -> !macroEntry.command.isEmpty()).toList();
                    if (filteredEntries.size() != 0) {
                        Keys4Macros.NETWORK.send(PacketDistributor.ALL.noArg(), new ServerMacrosPacket(filteredEntries));
                    }
                    return 1;
                })));
    }
}
