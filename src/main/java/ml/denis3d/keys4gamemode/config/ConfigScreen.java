package ml.denis3d.keys4gamemode.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

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
        macroOne = new TextFieldWidget(minecraft.fontRenderer, 30 + this.minecraft.fontRenderer.getStringWidth("Macro Key 1"), this.height / 2 - 27, this.width - 50 - this.minecraft.fontRenderer.getStringWidth("Macro Key 1"), 15, "Macro 1");
        this.macroOne.setMaxStringLength(256);
        this.macroOne.setText(Config.CLIENT.macroOne.get());
        this.children.add(this.macroOne);
        macroTwo = new TextFieldWidget(minecraft.fontRenderer, 30 + this.minecraft.fontRenderer.getStringWidth("Macro Key 2"), this.height / 2 - 2, this.width - 50 - this.minecraft.fontRenderer.getStringWidth("Macro Key 1"), 15, "Macro 2");
        this.macroTwo.setMaxStringLength(256);
        this.macroTwo.setText(Config.CLIENT.macroTwo.get());
        this.children.add(this.macroTwo);
        macroThree = new TextFieldWidget(minecraft.fontRenderer, 30 + this.minecraft.fontRenderer.getStringWidth("Macro Key 3"), this.height / 2 + 25, this.width - 50 - this.minecraft.fontRenderer.getStringWidth("Macro Key 1"), 15, "Macro 3");
        this.macroThree.setMaxStringLength(256);
        this.macroThree.setText(Config.CLIENT.macroThree.get());
        this.children.add(this.macroThree);

        this.addButton(new Button(this.width / 2 - 75, this.height - 29, 150, 20, I18n.format("gui.done"), (p_213124_1_) -> {
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
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.drawCenteredString(minecraft.fontRenderer, this.title.getString(), this.width / 2, 20, 16777215);
        this.drawString(minecraft.fontRenderer, "Macro Key 1", 10, this.height / 2 - 25, 16777215);
        this.drawString(minecraft.fontRenderer, "Macro Key 2", 10, this.height / 2, 16777215);
        this.drawString(minecraft.fontRenderer, "Macro Key 3", 10, this.height / 2 + 25, 16777215);
        macroOne.render(p_render_1_, p_render_2_, p_render_3_);
        macroTwo.render(p_render_1_, p_render_2_, p_render_3_);
        macroThree.render(p_render_1_, p_render_2_, p_render_3_);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public void onClose() {
        Config.CLIENT.macroOne.set(macroOne.getText());
        Config.CLIENT.macroTwo.set(macroTwo.getText());
        Config.CLIENT.macroThree.set(macroThree.getText());
        this.minecraft.displayGuiScreen(parentScreen);
    }
}
