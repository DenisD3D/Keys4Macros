package ml.denisd3d.keys4macros.server;

import com.mojang.brigadier.CommandDispatcher;
import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.packets.ServerMacrosPacket;
import ml.denisd3d.keys4macros.structures.ServerMacro;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class RestartCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("keys4macros")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(3))
                .then(Commands.literal("restart").executes(context -> {
                    if (!Keys4Macros.INSTANCE.serverHandler.readConfig()) { // Error while reading config
                        context.getSource().sendFailure(new TextComponent("At least 2 entry have the same id in the server config. Please delete the line starting by id= for the created macros. A new id will be given automatically"));
                        return -1;
                    }

                    List<ServerMacro> filteredEntries = Keys4Macros.INSTANCE.serverHandler.config.serverMacros.macros.stream().filter(macroEntry -> !macroEntry.getCommand().isEmpty()).toList();
                    if (filteredEntries.size() != 0) {
                        Keys4Macros.NETWORK.send(PacketDistributor.ALL.noArg(), new ServerMacrosPacket(filteredEntries));
                    }
                    context.getSource().sendSuccess(new TextComponent("Keys4Macros config reloaded and shared to players."), true);
                    return 1;
                })));
    }
}
