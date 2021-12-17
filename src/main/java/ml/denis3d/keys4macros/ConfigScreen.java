package ml.denis3d.keys4macros;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

public class ConfigScreen extends Screen {
    private final Screen parentScreen;
    public ModConfig.MacroEntry buttonId;

    EditBox keyCode;
    EditBox modifiers;

    public ConfigScreen(Minecraft minecraftIn, Screen parentIn) {
        super(new TextComponent("Keys4Macros Config"));
        this.parentScreen = parentIn;
    }

    @Override
    public void init() {
        super.init();
        this.addRenderableWidget(new Button(this.width / 2 - 150 - 10, this.height / 3, 150, 20, new TextComponent("Open Config"), (p_213124_1_) -> Util.getPlatform().openFile(new File("config", "keys4macros.toml"))));
        this.addRenderableWidget(new Button(this.width / 2 + 10, this.height / 3, 150, 20, new TextComponent("Reload Config"), (p_213124_1_) -> {
            Keys4Macros.INSTANCE.config = ModConfig.load(Keys4Macros.CONFIG_FILE);
            this.onClose();
        }));

        this.keyCode = new EditBox(font, this.width / 2 - 150 - 10, 2 * this.height / 3 - 20, 50, 20, new TextComponent("KeyCode"));
        this.keyCode.setEditable(false);
        this.keyCode.setCanLoseFocus(false);
        this.keyCode.setFocus(false);
        this.keyCode.setTextColorUneditable(14737632);

        this.modifiers = new EditBox(font, this.width / 2 - 150 + 50, 2 * this.height / 3 - 20, 50, 20, new TextComponent("KeyCode"));
        this.modifiers.setEditable(false);
        this.modifiers.setCanLoseFocus(false);
        this.modifiers.setFocus(false);
        this.modifiers.setTextColorUneditable(14737632);

        this.addRenderableWidget(this.keyCode);
        this.addRenderableWidget(this.modifiers);
        if (this.minecraft != null) {
            this.addRenderableWidget(new Button(this.width / 2 - 20, 2 * this.height / 3 - 20, 200, 20, new TextComponent("Copy config entry for this key"), (p_213124_1_) -> {
                Transferable stringSelection = new StringSelection("    [[Macros.Macro]]\n        key = %d\n        modifiers = %d\n        command = \"PUT THE COMMAND HERE\"\n        comment = \"\"".formatted(keyCode.getValue().isEmpty() ? 0 : Integer.parseInt(keyCode.getValue()), modifiers.getValue().isBlank() ? 0 : Integer.parseInt(modifiers.getValue())));
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }));
        }


        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height - 29, 150, 20, new TranslatableComponent("gui.done"), (p_213124_1_) -> this.onClose()));
    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        this.keyCode.setValue(String.valueOf(p_231046_1_));
        this.modifiers.setValue(String.valueOf(p_231046_3_));
        return true;
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int cursorX, int cursorY, float ticks) {
        if (this.minecraft != null) {
            this.renderBackground(matrixStack);
            drawCenteredString(matrixStack, this.minecraft.font, this.title.getString(), this.width / 2, 20, 16777215);
            drawString(matrixStack, this.minecraft.font, "Key :", this.width / 2 - 150 - 10, 2 * this.height / 3 - 32, 16777215);
            drawString(matrixStack, this.minecraft.font, "Modifiers :", this.width / 2 - 150 + 50, 2 * this.height / 3 - 32, 16777215);
            super.render(matrixStack, cursorX, cursorY, ticks);
        }
    }

    @Override
    public void onClose() {
        ModConfig.reload();
        if (this.minecraft != null && this.parentScreen != null)
            this.minecraft.setScreen(parentScreen);
        else
            super.onClose();
    }
}