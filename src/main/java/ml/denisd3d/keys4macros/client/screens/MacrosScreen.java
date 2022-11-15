package ml.denisd3d.keys4macros.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.client.ClientUtils;
import ml.denisd3d.keys4macros.structures.ForcedMacro;
import ml.denisd3d.keys4macros.structures.GlobalMacro;
import ml.denisd3d.keys4macros.structures.LocatedMacro;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class MacrosScreen extends Screen {

    private final Screen parentScreen;

    public MacrosScreen(Minecraft ignoredMinecraft, Screen parent) {
        super(Component.literal("Keys4Macros Config"));
        this.parentScreen = parent;
    }

    @Override
    public void init() {
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height / 2 - 40, 150, 20, Component.literal("Global Macros"), (button) -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new EditMacrosScreen(this, Component.literal("Global Macros Settings"), Keys4Macros.INSTANCE.clientHandler.config.globalMacros.macros, () -> {
                    GlobalMacro globalMacro = new GlobalMacro();
                    Keys4Macros.INSTANCE.clientHandler.config.globalMacros.macros.add(globalMacro);
                    return globalMacro;
                }, macro -> Keys4Macros.INSTANCE.clientHandler.config.globalMacros.macros.remove(macro)));
            }
        }));
        Button this_location_macros = new Button(this.width / 2 - 75, this.height / 2 - 10, 150, 20, Component.literal("This location Macros"), (button) -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new EditMacrosScreen(this,
                        Component.literal(ClientUtils.getCurrentLocationOrEmpty() + " Macros Settings"),
                        ClientUtils.getMacrosForLocation(ClientUtils.getCurrentLocationOrEmpty()),
                        () -> {
                            LocatedMacro locatedMacro = new LocatedMacro();
                            locatedMacro.setLocation(ClientUtils.getCurrentLocationOrEmpty());
                            Keys4Macros.INSTANCE.clientHandler.config.locatedMacros.macros.add(locatedMacro);
                            return locatedMacro;
                        },
                        macro -> {
                            if (macro instanceof LocatedMacro) {
                                Keys4Macros.INSTANCE.clientHandler.config.locatedMacros.macros.remove(macro);
                            } else if (macro instanceof ForcedMacro) {
                                Keys4Macros.INSTANCE.clientHandler.config.forcedMacros.macros.remove(macro);
                            }
                        }));
            }
        });
        if (ClientUtils.getCurrentLocationOrEmpty().isEmpty()) {
            this_location_macros.active = false;
        }
        this.addRenderableWidget(this_location_macros);
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height / 2 + 20, 150, 20, Component.literal("All locations Macros"), (button) -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new AllLocationsScreen(this));
            }
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height - 29, 150, 20, Component.translatable("gui.done"), (button) -> this.onClose()));
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.minecraft == null)
            return;

        this.renderBackground(pPoseStack);
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