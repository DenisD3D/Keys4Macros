package ml.denisd3d.keys4macros.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class CheatSheetScreen extends Screen {

    private final Screen parentScreen;

    public CheatSheetScreen(Screen parent) {
        super(new TextComponent("Keys4Macros Cheat Sheet"));
        this.parentScreen = parent;
    }

    @Override
    public void init() {
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height - 29, 150, 20, new TranslatableComponent("gui.done"), (button) -> this.onClose()));
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.minecraft == null)
            return;

        this.renderBackground(pPoseStack);
        drawCenteredString(pPoseStack, this.font, this.title, this.width / 2, 8, 16777215);

        drawCenteredString(pPoseStack, this.font, "Variable: ${name} or ${name|default_value}", this.width / 2, this.height / 2 - 50, 16777215);
        drawCenteredString(pPoseStack, this.font, "=> will be asked when executing the macro", this.width / 2, this.height / 2 - 40, 16777215);

        drawCenteredString(pPoseStack, this.font, "Multiple line: use \\n (\\\\n for a \\n in your command)", this.width / 2, this.height / 2 - 20, 16777215);

        drawCenteredString(pPoseStack, this.font, "Edit with external editor:", this.width / 2, this.height / 2, 16777215);
        drawCenteredString(pPoseStack, this.font, "Double click on the command box & click open in external editor.", this.width / 2, this.height / 2 + 10, 16777215);
        drawCenteredString(pPoseStack, this.font, "When done editing, load the change by clicking Import changes.", this.width / 2, this.height / 2 + 20, 16777215);

        drawCenteredString(pPoseStack, this.font, new TextComponent("For any additional help join the discord : ").append(new TextComponent("https://discord.gg/rzzd76c").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/rzzd76c")))), this.width / 2, this.height / 2 + 40, 16777215);

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.parentScreen != null)
            this.minecraft.setScreen(parentScreen);
        else
            super.onClose();
    }
}