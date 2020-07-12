package ml.denis3d.keys4gamemode.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ConfigScreen extends Screen {
    private final Screen parentScreen;
    private TextFieldWidget macroOne, macroTwo, macroThree;

    public ConfigScreen(Minecraft minecraftIn, Screen parentIn) {
        super(new StringTextComponent("Keys4Survival Config"));
        this.parentScreen = parentIn;
    }

    @Override
    public void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        macroOne = new TextFieldWidget(minecraft.fontRenderer, 30 + this.minecraft.fontRenderer.getStringWidth("Macro Key 1"), this.height / 2 - 27, this.width - 50 - this.minecraft.fontRenderer.getStringWidth("Macro Key 1"), 15, new StringTextComponent("Macro 1"));
        this.macroOne.setMaxStringLength(256);
        this.macroOne.setText(Config.CLIENT.macroOne.get());
        this.children.add(this.macroOne);
        macroTwo = new TextFieldWidget(minecraft.fontRenderer, 30 + this.minecraft.fontRenderer.getStringWidth("Macro Key 2"), this.height / 2 - 2, this.width - 50 - this.minecraft.fontRenderer.getStringWidth("Macro Key 1"), 15, new StringTextComponent("Macro 2"));
        this.macroTwo.setMaxStringLength(256);
        this.macroTwo.setText(Config.CLIENT.macroTwo.get());
        this.children.add(this.macroTwo);
        macroThree = new TextFieldWidget(minecraft.fontRenderer, 30 + this.minecraft.fontRenderer.getStringWidth("Macro Key 3"), this.height / 2 + 25, this.width - 50 - this.minecraft.fontRenderer.getStringWidth("Macro Key 1"), 15, new StringTextComponent("Macro 3"));
        this.macroThree.setMaxStringLength(256);
        this.macroThree.setText(Config.CLIENT.macroThree.get());
        this.children.add(this.macroThree);

        this.addButton(new Button(this.width / 2 - 75, this.height - 29, 150, 20, new TranslationTextComponent("gui.done"), (p_213124_1_) -> {
            this.onClose();
        }));
    }

    @Override
    public void tick() {
        macroOne.tick();
        macroTwo.tick();
        macroThree.tick();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int cursorX, int cursorY, float ticks)
    {
        this.renderBackground(matrixStack);
        this.drawCenteredString(matrixStack, minecraft.fontRenderer, this.title.getString(), this.width / 2, 20, 16777215);
        this.drawString(matrixStack, minecraft.fontRenderer, "Macro Key 1", 10, this.height / 2 - 25, 16777215);
        this.drawString(matrixStack, minecraft.fontRenderer, "Macro Key 2", 10, this.height / 2, 16777215);
        this.drawString(matrixStack, minecraft.fontRenderer, "Macro Key 3", 10, this.height / 2 + 25, 16777215);
        macroOne.render(matrixStack, cursorX, cursorY, ticks);
        macroTwo.render(matrixStack, cursorX, cursorY, ticks);
        macroThree.render(matrixStack, cursorX, cursorY, ticks);
        super.render(matrixStack, cursorX, cursorY, ticks);
    }

    @Override
    public void onClose() {
        Config.CLIENT.macroOne.set(macroOne.getText());
        Config.CLIENT.macroTwo.set(macroTwo.getText());
        Config.CLIENT.macroThree.set(macroThree.getText());
        this.minecraft.displayGuiScreen(parentScreen);
    }
}
