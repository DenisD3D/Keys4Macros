package ml.denisd3d.keys4macros.client.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.client.ClientUtils;
import ml.denisd3d.keys4macros.structures.ForcedMacro;
import ml.denisd3d.keys4macros.structures.LocatedMacro;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AllLocationsList extends ContainerObjectSelectionList<AllLocationsList.Entry> {
    private final AllLocationsScreen allLocationsScreen;

    public AllLocationsList(Minecraft pMinecraft, AllLocationsScreen allLocationsScreen, List<String> locations) {
        super(pMinecraft, allLocationsScreen.width /*Width*/, allLocationsScreen.height /* Height*/, 20 /*Y0*/, allLocationsScreen.height - 32 /*Y1*/, 20 /*Item height*/);
        this.allLocationsScreen = allLocationsScreen;

        this.setRenderHeader(true, 10);

        for (String variable : locations) {
            this.addEntry(new Entry(variable));
        }

        if (pMinecraft.level != null) {
            this.setRenderBackground(false);
            this.setRenderTopAndBottom(false);
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6 - 20;
    }

    @Override
    public int getMaxPosition() {
        return super.getMaxPosition() + 10;
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        private final Button button;

        public Entry(String location) {
            this.button = new Button(0, 0, 150, 20, new TextComponent(location), pButton -> AllLocationsList.this.minecraft.setScreen(new EditMacrosScreen(
                    AllLocationsList.this.allLocationsScreen, new TextComponent(location + " Macros Settings"),
                    ClientUtils.getMacrosForLocation(location),
                    () -> {
                        LocatedMacro locatedMacro = new LocatedMacro();
                        locatedMacro.setLocation(location);
                        Keys4Macros.INSTANCE.clientHandler.config.locatedMacros.macros.add(locatedMacro);
                        return locatedMacro;
                    },
                    macro -> {
                        if (macro instanceof LocatedMacro) {
                            Keys4Macros.INSTANCE.clientHandler.config.locatedMacros.macros.remove(macro);
                        } else if (macro instanceof ForcedMacro) {
                            Keys4Macros.INSTANCE.clientHandler.config.forcedMacros.macros.remove(macro);
                        }
                    })));
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            this.button.x = pLeft + pWidth / 2 - 75 - 10;
            this.button.y = pTop;
            this.button.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.button);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.button);
        }
    }
}
