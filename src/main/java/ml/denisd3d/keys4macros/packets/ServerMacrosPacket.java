package ml.denisd3d.keys4macros.packets;

import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.server.ServerConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ServerMacrosPacket {

    public final List<ServerConfig.MacroEntry> macros;

    public ServerMacrosPacket(List<ServerConfig.MacroEntry> macros) {
        this.macros = macros;
    }

    public static void encode(ServerMacrosPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.macros.size());
        for (ServerConfig.MacroEntry entry : packet.macros) {
            buf.writeInt(entry.id);
            buf.writeInt(entry.default_key);
            buf.writeInt(entry.default_modifiers);
            buf.writeUtf(entry.command);
            buf.writeBoolean(entry.send);
        }
    }

    public static ServerMacrosPacket decode(FriendlyByteBuf buf) {
        List<ServerConfig.MacroEntry> macros = new ArrayList<>();
        int count = buf.readInt();
        for (int i = 0; i < count; i++) {
            macros.add(new ServerConfig.MacroEntry(buf.readInt(), buf.readInt(), buf.readInt(), buf.readUtf(), buf.readBoolean()));
        }
        return new ServerMacrosPacket(macros);
    }

    public static void handle(ServerMacrosPacket msg, Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Keys4Macros.INSTANCE.clientHandler.handleServerMacrosPacket(msg));
        });
        ctx.get().setPacketHandled(true);
    }
}
