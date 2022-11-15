package ml.denisd3d.keys4macros.client.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import ml.denisd3d.keys4macros.client.widgets.DoubleClickableEditBox;
import ml.denisd3d.keys4macros.structures.IMacro;
import ml.denisd3d.keys4macros.structures.ProcessMode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class EditMacrosList extends ContainerObjectSelectionList<EditMacrosList.Entry> {

    private final EditMacrosScreen macrosScreen;

    public EditMacrosList(Minecraft pMinecraft, List<? extends IMacro> macros, EditMacrosScreen macrosScreen, int pWidth, int pHeight, int pY0, int pY1) {
        super(pMinecraft, pWidth, pHeight, pY0, pY1, 20);
        this.macrosScreen = macrosScreen;

        this.setRenderHeader(true, 10);

        if (pMinecraft.level != null) {
            this.setRenderBackground(false);
            this.setRenderTopAndBottom(false);
        }

        for (IMacro macro : macros) {
            this.addEntry(new Entry(macro));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6 - 20;
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    public int getMaxPosition() {
        return super.getMaxPosition() + 10;
    }

    @Override
    public int getRowLeft() {
        return super.getRowLeft();
    }

    public void tick() {
        for (Entry entry : this.children()) {
            entry.tick();
        }
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        for (Entry entry : this.children()) {
            if (entry.commandBox.isMouseOver(pMouseX, pMouseY))
                entry.commandBox.renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (Entry entry : this.children()) {
            if (entry == getEntryAtPosition(pMouseX, pMouseY)) continue;
            entry.commandBox.setFocus(false);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public void newEntry(IMacro macro) {
        this.addEntry(new Entry(macro));
    }

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        public final IMacro macro;
        public final DoubleClickableEditBox commandBox;
        private final Button changeButton, deleteButton, modeButton;
        public InputConstants.Key key;
        public KeyModifier activeModifier = KeyModifier.NONE;

        public Entry(IMacro macro) {
            this.macro = macro;
            this.key = macro.getKey() == -1 ? InputConstants.UNKNOWN : InputConstants.getKey(this.macro.getKey(), 0);

            switch (this.macro.getModifiers()) {
                case 2 -> this.activeModifier = KeyModifier.CONTROL;
                case 1 -> this.activeModifier = KeyModifier.SHIFT;
                case 4 -> this.activeModifier = KeyModifier.ALT;
            }

            this.commandBox = new DoubleClickableEditBox(EditMacrosList.this.minecraft.font, 0, 0, 180, 18, Component.literal("Command"), doubleClickableEditBox -> {
                if (macro.isLocked())
                    return;
                EditMacrosList.this.minecraft.setScreen(new EditMacroInExternalEditorScreen(EditMacrosList.this.macrosScreen, this));
            });
            this.commandBox.setMaxLength(256);
            this.commandBox.setValue(this.macro.getCommand());
            this.commandBox.moveCursorToStart();

            this.changeButton = new Button(0, 0, 75 + 20, 20, this.activeModifier.getCombinedName(this.key, () -> this.key.getDisplayName()), pButton -> EditMacrosList.this.macrosScreen.selectedMacro = this);
            this.modeButton = new Button(0, 0, 40, 20, Component.literal(macro.getMode().toString()), pButton -> pButton.setMessage(Component.literal(ProcessMode.valueOf(pButton.getMessage().getString()).next().toString())));
            this.deleteButton = new Button(0, 0, 20, 20, Component.literal(this.macro.isLocked() && this.macro.isComplete() ? "R" : "X"), pButton -> EditMacrosList.this.minecraft.setScreen(new ConfirmScreen(t -> {
                EditMacrosList.this.minecraft.setScreen(EditMacrosList.this.macrosScreen);
                if (t) {
                    if (this.macro.isLocked() && this.macro.isComplete()) {
                        this.key = macro.getDefaultKey() == -1 ? InputConstants.UNKNOWN : InputConstants.getKey(this.macro.getDefaultKey(), 0);
                        switch (this.macro.getDefaultModifiers()) {
                            case 2 -> this.activeModifier = KeyModifier.CONTROL;
                            case 1 -> this.activeModifier = KeyModifier.SHIFT;
                            case 4 -> this.activeModifier = KeyModifier.ALT;
                        }
                    } else {
                        EditMacrosList.this.removeEntry(this);
                        EditMacrosList.this.macrosScreen.macroDeleter.accept(this.macro);
                    }
                }
            }, Component.literal(this.macro.isLocked() && this.macro.isComplete() ? "Are you sure you want to reset this macro?" : "Are you sure to delete this macro?"), Component.literal("Command: " + this.commandBox.getValue() + "\nKey: ").append(this.activeModifier.getCombinedName(this.key, () -> this.key.getDisplayName())).append(Component.literal("\nMode: " + macro.getMode())))));

            if (this.macro.isLocked()) {
                this.commandBox.active = false;
                this.commandBox.setEditable(false);
                this.modeButton.active = false;
                this.commandBox.setOnTooltip((pButton, pPoseStack, pMouseX, pMouseY) -> {
                    EditMacrosList.this.macrosScreen.renderTooltip(pPoseStack, Component.literal("This macro is locked by the server you are on"), pMouseX, pMouseY);
                });
            }

            updateExternal();
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.commandBox, this.changeButton, this.modeButton, this.deleteButton);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.commandBox, this.changeButton, this.modeButton, this.deleteButton);
        }

        public void tick() {
            this.commandBox.tick();
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            int widthCommandBox = pWidth - (10 + 10 + 95 + 10 + 40 + 10 + 20 + 20 + 10);

            this.commandBox.x = pLeft + 10;
            this.commandBox.y = pTop;
            this.commandBox.setWidth(widthCommandBox);
            this.commandBox.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            this.changeButton.x = pLeft + 10 + widthCommandBox + 10;
            this.changeButton.y = pTop;
            this.changeButton.setMessage(this.activeModifier.getCombinedName(this.key, () -> this.key.getDisplayName()));
            if (EditMacrosList.this.macrosScreen.selectedMacro == this) {
                this.changeButton.setMessage((Component.literal("> ")).append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.YELLOW)).append(" <").withStyle(ChatFormatting.YELLOW));
            }
            this.changeButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            this.modeButton.x = pLeft + 10 + widthCommandBox + 10 + 95 + 10;
            this.modeButton.y = pTop;
            this.modeButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            this.deleteButton.x = pLeft + 10 + widthCommandBox + 10 + 95 + 10 + 40 + 10;
            this.deleteButton.y = pTop;
            this.deleteButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        public void updateMacro() {
            this.macro.setCommand(this.commandBox.getValue());
            this.macro.setKey(this.key.getValue());
            switch (this.activeModifier) {
                case CONTROL -> this.macro.setModifiers(2);
                case SHIFT -> this.macro.setModifiers(1);
                case ALT -> this.macro.setModifiers(4);
                case NONE -> this.macro.setModifiers(0);
            }
            this.macro.setMode(ProcessMode.valueOf(this.modeButton.getMessage().getString()));
        }

        public void updateExternal() {
            if (macro.isLocked())
                return;
            if (EditMacroInExternalEditorScreen.externalMacros.containsKey(this.macro.getId())) {
                this.commandBox.active = false;
                this.commandBox.setEditable(false);
                this.commandBox.setOnTooltip((pButton, pPoseStack, pMouseX, pMouseY) -> {
                    EditMacrosList.this.macrosScreen.renderTooltip(pPoseStack, Component.literal("Macro is under edit in external editor"), pMouseX, pMouseY);
                });
            } else {
                this.commandBox.active = true;
                this.commandBox.setEditable(true);
                this.commandBox.setOnTooltip(DoubleClickableEditBox.NO_TOOLTIP);
            }
        }
    }
}
