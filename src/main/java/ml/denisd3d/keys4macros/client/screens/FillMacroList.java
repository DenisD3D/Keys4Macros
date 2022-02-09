package ml.denisd3d.keys4macros.client.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class FillMacroList extends ContainerObjectSelectionList<FillMacroList.Entry> {
    private int maxNameWidth;

    public FillMacroList(Minecraft pMinecraft, FillMacroScreen fillMacroScreen) {
        super(pMinecraft, fillMacroScreen.width /*Width*/, 123 /* Height*/, fillMacroScreen.height / 2 - 123 / 2 /*Y0*/, fillMacroScreen.height / 2 + 123 / 2 /*Y1*/, 20 /*Item height*/);

        for (String variable : fillMacroScreen.variables) {
            this.addEntry(new Entry(variable));
        }

        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
    }

    public void tick() {
        for (Entry entry : this.children()) {
            entry.tick();
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (Entry entry : this.children()) {
            if (entry == getEntryAtPosition(pMouseX, pMouseY))
                continue;
            entry.valueBox.setFocus(false);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 5;
    }

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        private final String name;
        private final EditBox valueBox;

        public Entry(String variable) {

            String[] split = variable.split("\\|");
            this.name = split[0];
            String default_value = split.length > 1 ? split[1] : "";

            int i = FillMacroList.this.minecraft.font.width(name);
            if (i > FillMacroList.this.maxNameWidth) {
                FillMacroList.this.maxNameWidth = i;
            }

            this.valueBox = new EditBox(FillMacroList.this.minecraft.font, 0, 0, 155, 18, new TextComponent("Value"));
            this.valueBox.setMaxLength(256);
            this.valueBox.setValue(default_value);
        }

        public void tick() {
            this.valueBox.tick();
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            float f = (float) (pLeft + pWidth / 4 - FillMacroList.this.maxNameWidth);
            FillMacroList.this.minecraft.font.draw(pPoseStack, this.name, f, (float) (pTop + pHeight / 2 - 9 / 2), 14737632);

            this.valueBox.x = pLeft + pWidth / 4 + 10;
            this.valueBox.y = pTop;
            this.valueBox.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.valueBox);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.valueBox);
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.valueBox.getValue();
        }
    }
}
