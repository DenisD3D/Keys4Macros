package ml.denis3d.keys4macros;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;
import java.io.File;

public class ConfigScreen extends Screen {
    private final Screen parentScreen;
    public ModConfig.MacroEntry buttonId;

    //public MacroList macrosList;

    public ConfigScreen(Minecraft minecraftIn, Screen parentIn) {
        super(new TextComponent("Keys4Macros Config"));
        this.parentScreen = parentIn;
    }

    @Override
    public void init() {
        super.init();

        this.addRenderableWidget(new Button(this.width / 2 - 150 - 10, this.height / 2, 150, 20, new TextComponent("Open Config"), (p_213124_1_) -> Util.getPlatform().openFile(new File("config", "keys4macros.toml"))));
        this.addRenderableWidget(new Button(this.width / 2 + 10, this.height / 2, 150, 20, new TextComponent("Reload Config"), (p_213124_1_) -> {
            Keys4Macros.INSTANCE.config = ModConfig.load(Keys4Macros.CONFIG_FILE);
            this.onClose();
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height - 29, 150, 20, new TranslatableComponent("gui.done"), (p_213124_1_) -> this.onClose()));

        //this.macrosList = new MacroList(this, this.minecraft);
        //this.addWidget(this.macrosList);
    }

    /*@Override
    public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
        if (this.buttonId != null) {
            Keys4Macros.INSTANCE.config.macros_map.get(this.buttonId.key).key = InputConstants.Type.MOUSE.getOrCreate(p_231044_5_).getValue();
            this.buttonId = null;
            ModConfig.reload();
            this.macrosList.update();
            return true;
        } else {
            return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
        }
    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        if (this.buttonId != null) {
            if (p_231046_1_ == 256) {
                Keys4Macros.INSTANCE.config.macros_map.get(this.buttonId.key).key = InputConstants.UNKNOWN.getValue();
            } else {
                Keys4Macros.INSTANCE.config.macros_map.get(this.buttonId.key).key = InputConstants.getKey(p_231046_1_, p_231046_2_).getValue();
            }
            ModConfig.reload();
            this.macrosList.update();

            this.buttonId = null;
            return true;
        } else {
            return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
        }
    }*/

    @Override
    public void render(@Nonnull PoseStack matrixStack, int cursorX, int cursorY, float ticks) {
        if (this.minecraft != null) {
            this.renderBackground(matrixStack);
            //this.macrosList.render(matrixStack, cursorX, cursorY, ticks);
            drawCenteredString(matrixStack, this.minecraft.font, this.title.getString(), this.width / 2, 20, 16777215);
            super.render(matrixStack, cursorX, cursorY, ticks);
        }
    }

    @Override
    public void tick() {
        //this.macrosList.children().forEach(entry -> ((MacroList.KeyEntry)entry).tick());
        super.tick();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.parentScreen != null)
            this.minecraft.setScreen(parentScreen);
        else
            super.onClose();
    }
}