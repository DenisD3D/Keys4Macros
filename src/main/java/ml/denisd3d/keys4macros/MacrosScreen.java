package ml.denisd3d.keys4macros;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.settings.KeyModifier;

import javax.annotation.Nonnull;

public class MacrosScreen extends Screen {

    private final Screen parentScreen;
    public MacrosList.Entry selectedMacro;
    private MacrosList macrosList;
    private boolean isInitialised = false;

    public MacrosScreen(Minecraft ignoredMinecraft, Screen parent) {
        super(new TextComponent("Keys4Macros Config"));
        this.parentScreen = parent;
    }

    @Override
    public void init() {
        if (!this.isInitialised)
            this.macrosList = new MacrosList(this.minecraft, this);
        this.isInitialised = true;

        this.addWidget(this.macrosList);
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height - 29, 150, 20, new TranslatableComponent("gui.done"), (button) -> this.onClose()));
        this.addRenderableWidget(new Button(this.width / 2 + 75 + 20, this.height - 29, 75, 20, new TextComponent("New Macro"), (button) -> this.macrosList.addNewMacro()));
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.selectedMacro == null)
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);

        InputConstants.Key key = pKeyCode == 256 ? InputConstants.UNKNOWN : InputConstants.getKey(pKeyCode, pScanCode);
        KeyModifier activeModifier = KeyModifier.NONE;

        switch (pModifiers) {
            case 2 -> activeModifier = KeyModifier.CONTROL;
            case 1 -> activeModifier = KeyModifier.SHIFT;
            case 4 -> activeModifier = KeyModifier.ALT;
        }

        if (activeModifier.matches(key))
            activeModifier = KeyModifier.NONE;

        this.selectedMacro.key = key;
        this.selectedMacro.activeModifier = activeModifier;

        if (!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.selectedMacro.key))
            this.selectedMacro = null;

        return true;
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.minecraft == null)
            return;

        this.renderBackground(pPoseStack);
        this.macrosList.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        drawCenteredString(pPoseStack, this.font, this.title, this.width / 2, 8, 16777215);

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void tick() {
        this.macrosList.tick();
    }

    @Override
    public void onClose() {
        Keys4Macros.INSTANCE.config.macros.clear();
        for (MacrosList.Entry macroEntry : this.macrosList.children()) {
            Keys4Macros.INSTANCE.config.macros.add(macroEntry.getUpdatedConfigEntry());
        }
        Keys4Macros.INSTANCE.config.save();
        if (this.minecraft != null && this.parentScreen != null)
            this.minecraft.setScreen(parentScreen);
        else
            super.onClose();
    }
}