package ml.denisd3d.keys4macros.packets;

import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.server.ServerConfig;
import ml.denisd3d.keys4macros.structures.ProcessMode;
import ml.denisd3d.keys4macros.structures.ServerMacro;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class ServerMacrosPacket {

    public final List<ServerMacro> macros;

    public ServerMacrosPacket(List<ServerMacro> macros) {
        this.macros = macros;
    }

    public static void encode(ServerMacrosPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.macros.size());
        for (ServerMacro entry : packet.macros) {
            buf.writeUtf(entry.getId().toString());
            buf.writeInt(entry.getKey());
            buf.writeInt(entry.getModifiers());
            buf.writeUtf(entry.getCommand());
            buf.writeEnum(entry.getMode());
        }
    }

    public static ServerMacrosPacket decode(FriendlyByteBuf buf) {
        List<ServerMacro> macros = new ArrayList<>();
        int count = buf.readInt();
        for (int i = 0; i < count; i++) {
            ServerMacro serverMacro = new ServerMacro();
            serverMacro.setId(UUID.fromString(buf.readUtf()));
            serverMacro.setKey(buf.readInt());
            serverMacro.setModifiers(buf.readInt());
            serverMacro.setCommand(buf.readUtf());
            serverMacro.setMode(buf.readEnum(ProcessMode.class));
            macros.add(serverMacro);
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
