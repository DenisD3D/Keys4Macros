package ml.denisd3d.keys4macros;

import ml.denisd3d.keys4macros.client.ClientHandler;
import ml.denisd3d.keys4macros.client.screens.MacrosScreen;
import ml.denisd3d.keys4macros.packets.ServerMacrosPacket;
import ml.denisd3d.keys4macros.server.ServerHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

import static net.minecraftforge.network.NetworkRegistry.ACCEPTVANILLA;

@Mod("keys4macros")
public class Keys4Macros {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("keys4macros", "main"),
            () -> PROTOCOL_VERSION,
            s -> s.equals(ACCEPTVANILLA) || PROTOCOL_VERSION.equals(s),
            s -> s.equals(ACCEPTVANILLA) || PROTOCOL_VERSION.equals(s)
    );
    public static Keys4Macros INSTANCE;
    public ClientHandler clientHandler;
    public ServerHandler serverHandler;

    public Keys4Macros() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(MacrosScreen::new));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupServer);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        INSTANCE = this;
    }

    private void setupClient(FMLClientSetupEvent event) {
        clientHandler = new ClientHandler();
    }

    private void setupServer(FMLDedicatedServerSetupEvent event) {
        serverHandler = new ServerHandler();
        serverHandler.setup();
    }

    private void setup(FMLCommonSetupEvent event) {
        NETWORK.registerMessage(0,
                ServerMacrosPacket.class,
                ServerMacrosPacket::encode,
                ServerMacrosPacket::decode,
                ServerMacrosPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
