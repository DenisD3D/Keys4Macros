package ml.denisd3d.keys4macros.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.structures.IMacro;
import ml.denisd3d.keys4macros.structures.LocatedMacro;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class EditMacrosScreen extends Screen {

    private final Screen parentScreen;
    private final List<? extends IMacro> macros;
    private final Callable<IMacro> newMacroSupplier;
    public final Consumer<IMacro> macroDeleter;
    public EditMacrosList.Entry selectedMacro;
    public EditMacrosList macrosList;
    private boolean isInitialised = false;

    public EditMacrosScreen(Screen parent, Component pTitle, List<? extends IMacro> macros, Callable<IMacro> newMacroSupplier, Consumer<IMacro> macroDeleter) {
        super(pTitle);
        this.parentScreen = parent;
        this.macros = macros;
        this.newMacroSupplier = newMacroSupplier;
        this.macroDeleter = macroDeleter;
    }

    @Override
    public void init() {
        if (!this.isInitialised)
            this.macrosList = new EditMacrosList(this.minecraft, this.macros, this, this.width + 20, this.height, 20, this.height - 32);
        else
            this.macrosList.updateSize(this.width + 20, this.height, 20, this.height - 32);

        this.isInitialised = true;
        this.addWidget(this.macrosList);

        int btn_width = (this.width - 5 * 10) / 4;
        this.addRenderableWidget(new Button(10, this.height - 29, btn_width, 20, new TranslatableComponent("gui.done"), (button) -> {
            this.save();
            this.onClose();
        }));
        this.addRenderableWidget(new Button(10 * 2 + btn_width, this.height - 29, btn_width, 20, new TranslatableComponent("gui.cancel"), (button) -> this.onClose()));
        this.addRenderableWidget(new Button(10 * 3 + btn_width * 2, this.height - 29, btn_width, 20, new TextComponent("New Macro"), (button) -> {
            try {
                this.macrosList.newEntry(newMacroSupplier.call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        this.addRenderableWidget(new Button(10 * 4 + btn_width * 3, this.height - 29, btn_width, 20, new TextComponent("Cheat sheet"), (button) -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(new CheatSheetScreen(this));
        }));
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.selectedMacro == null) {
            if (pKeyCode == 256 && this.shouldCloseOnEsc() && this.minecraft != null) {
                this.minecraft.setScreen(new ConfirmScreen(t -> {
                    this.minecraft.setScreen(this);
                    if (t) this.onClose();
                }, new TextComponent("Are you sure you want to exit macros settings without saving ?"), new TextComponent("")));
                return true;
            } else {
                return super.keyPressed(pKeyCode, pScanCode, pModifiers);
            }
        }

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

        if (!KeyModifier.isKeyCodeModifier(this.selectedMacro.key))
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

    public void save() {
        for (EditMacrosList.Entry macroEntry : this.macrosList.children()) {
            macroEntry.updateMacro();
        }
        Keys4Macros.INSTANCE.clientHandler.config.save();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.parentScreen != null)
            this.minecraft.setScreen(parentScreen);
        else
            super.onClose();
    }
}