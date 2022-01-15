package ml.denisd3d.keys4macros;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MacrosList extends ContainerObjectSelectionList<MacrosList.Entry> {

    private final MacrosScreen macrosScreen;

    public MacrosList(Minecraft pMinecraft, MacrosScreen macrosScreen) {
        super(pMinecraft, macrosScreen.width /*Width*/, macrosScreen.height /* Height*/, 20 /*Y0*/, macrosScreen.height - 32 /*Y1*/, 20 /*Item height*/);
        this.macrosScreen = macrosScreen;
        this.setRenderHeader(true, 10);
        for (ModConfig.MacroEntry macro : Keys4Macros.INSTANCE.config.macros) {
            this.addEntry(new MacrosList.Entry(macro));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 60;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 150;
    }

    @Override
    public int getMaxPosition() {
        return super.getMaxPosition() + 10;
    }

    public void tick() {
        for (MacrosList.Entry entry : this.children()) {
            entry.tick();
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (MacrosList.Entry entry : this.children()) {
            if (entry == getEntryAtPosition(pMouseX, pMouseY))
                continue;
            entry.commandBox.setFocus(false);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public void addNewMacro() {
        this.addEntry(new Entry(new ModConfig.MacroEntry(-1, 0, "", true)));
    }

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ContainerObjectSelectionList.Entry<MacrosList.Entry> {

        private final Button changeButton, deleteButton, shouldSend;
        private final EditBox commandBox;
        private final ModConfig.MacroEntry macro;
        public InputConstants.Key key;
        public KeyModifier activeModifier = KeyModifier.NONE;

        public Entry(ModConfig.MacroEntry macro) {
            this.macro = macro;
            this.key = macro.key == -1 ? InputConstants.UNKNOWN : InputConstants.getKey(this.macro.key, 0);
            switch (this.macro.modifiers) {
                case 2 -> this.activeModifier = KeyModifier.CONTROL;
                case 1 -> this.activeModifier = KeyModifier.SHIFT;
                case 4 -> this.activeModifier = KeyModifier.ALT;
            }

            this.commandBox = new EditBox(MacrosList.this.minecraft.font, 0, 0, 180, 18, new TextComponent("Command"));
            this.commandBox.setMaxLength(256);
            this.commandBox.setValue(this.macro.command);
            this.commandBox.moveCursorToStart();

            this.changeButton = new Button(0, 0, 75 + 20, 20, this.activeModifier.getCombinedName(this.key, () -> this.key.getDisplayName()), pButton -> MacrosList.this.macrosScreen.selectedMacro = this);
            this.shouldSend = new Button(0, 0, 40, 20, new TextComponent(macro.send ? "SEND" : "WRITE"), pButton -> {
                this.macro.send = !this.macro.send;
                pButton.setMessage(new TextComponent(macro.send ? "SEND" : "WRITE"));
            });
            this.deleteButton = new Button(0, 0, 20, 20, new TextComponent("X"), pButton -> MacrosList.this.minecraft.setScreen(new ConfirmScreen(t -> {
                MacrosList.this.minecraft.setScreen(MacrosList.this.macrosScreen);
                if (t) MacrosList.this.removeEntry(this);
            }, new TextComponent("Are you sure to delete this macro ?"), new TextComponent("Command: " + this.commandBox.getValue() + "\nKey: ").append(this.activeModifier.getCombinedName(this.key, () -> this.key.getDisplayName())).append(new TextComponent("\nMode: " + (macro.send ? "SEND" : "WRITE"))))));
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.deleteButton, this.changeButton, this.shouldSend, this.commandBox);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.deleteButton, this.changeButton, this.shouldSend, this.commandBox);
        }

        public void tick() {
            this.commandBox.tick();
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            this.changeButton.x = pLeft + pWidth / 2;
            this.changeButton.y = pTop;
            this.changeButton.setMessage(this.activeModifier.getCombinedName(this.key, () -> this.key.getDisplayName()));
            if (MacrosList.this.macrosScreen.selectedMacro == this) {
                this.changeButton.setMessage((new TextComponent("> ")).append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.YELLOW)).append(" <").withStyle(ChatFormatting.YELLOW));
            }
            this.changeButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            this.shouldSend.x = pLeft + pWidth / 2 + 75 + 20 + 10;
            this.shouldSend.y = pTop;
            this.shouldSend.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            this.deleteButton.x = pLeft + pWidth / 2 + 75 + 20 + 10 + 40 + 10;
            this.deleteButton.y = pTop;
            this.deleteButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            this.commandBox.x = pLeft;
            this.commandBox.y = pTop;
            this.commandBox.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }

        @Override
        public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
            return super.mouseReleased(pMouseX, pMouseY, pButton);
        }

        public ModConfig.MacroEntry getUpdatedConfigEntry() {
            this.macro.command = this.commandBox.getValue();
            this.macro.key = this.key.getValue();
            switch (this.activeModifier) {
                case CONTROL -> this.macro.modifiers = 2;
                case SHIFT -> this.macro.modifiers = 1;
                case ALT -> this.macro.modifiers = 4;
                case NONE -> this.macro.modifiers = 0;
            }
            return this.macro;
        }
    }
}
