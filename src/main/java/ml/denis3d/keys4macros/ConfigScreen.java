package ml.denis3d.keys4macros;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.io.File;

public class ConfigScreen extends Screen {
    private final Screen parentScreen;

    public ConfigScreen(Minecraft minecraftIn, Screen parentIn) {
        super(new StringTextComponent("Keys4Macros Config"));
        this.parentScreen = parentIn;
    }

    @Override
    public void init() {
        super.init();

        this.addButton(new Button(this.width / 2 - 150 - 10, this.height / 2, 150, 20, new StringTextComponent("Open Config"), (p_213124_1_) -> Util.getOSType().openFile(new File("config", "keys4macros.toml"))));

        this.addButton(new Button(this.width / 2 + 10, this.height / 2, 150, 20, new StringTextComponent("Reload Config"), (p_213124_1_) -> {
            Keys4Macros.INSTANCE.config = ModConfig.load(Keys4Macros.CONFIG_FILE);
            this.onClose();
        }));

        this.addButton(new Button(this.width / 2 - 75, this.height - 29, 150, 20, new TranslationTextComponent("gui.done"), (p_213124_1_) -> this.onClose()));
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int cursorX, int cursorY, float ticks) {
        if (this.minecraft != null) {
            this.renderBackground(matrixStack);
            this.drawCenteredString(matrixStack, this.minecraft.fontRenderer, this.title.getString(), this.width / 2, 20, 16777215);
            super.render(matrixStack, cursorX, cursorY, ticks);
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.parentScreen != null)
            this.minecraft.displayGuiScreen(parentScreen);
        else
            super.onClose();
    }
}