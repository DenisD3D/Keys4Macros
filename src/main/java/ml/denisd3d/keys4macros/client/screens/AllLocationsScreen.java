package ml.denisd3d.keys4macros.client.screens;

import com.google.common.collect.Streams;
import com.mojang.blaze3d.vertex.PoseStack;
import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.structures.ForcedMacro;
import ml.denisd3d.keys4macros.structures.LocatedMacro;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class AllLocationsScreen extends Screen {

    private final Screen parentScreen;
    private AllLocationsList allLocationsList;
    private boolean isInitialised = false;

    public AllLocationsScreen(Screen parent) {
        super(Component.literal("All Macros Settings"));
        this.parentScreen = parent;
    }

    @Override
    public void init() {

        if (!this.isInitialised) {
            Stream<String> locatedLocations = Keys4Macros.INSTANCE.clientHandler.config.locatedMacros.macros.stream().map(LocatedMacro::getLocation);
            Stream<String> forcedLocations = Keys4Macros.INSTANCE.clientHandler.config.forcedMacros.macros.stream().map(ForcedMacro::getLocation);
            this.allLocationsList = new AllLocationsList(this.minecraft, this, Streams.concat(locatedLocations, forcedLocations).distinct().toList());
        } else {
            this.allLocationsList.updateSize(this.width + 20, this.height, 20, this.height - 32);
        }
        this.isInitialised = true;

        this.addWidget(this.allLocationsList);

        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height - 29, 150, 20, Component.translatable("gui.done"), (button) -> this.onClose()));
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.minecraft == null)
            return;

        this.renderBackground(pPoseStack);
        this.allLocationsList.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        drawCenteredString(pPoseStack, this.font, this.title, this.width / 2, 8, 16777215);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.parentScreen != null)
            this.minecraft.setScreen(parentScreen);
        else
            super.onClose();
    }
}