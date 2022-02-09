package ml.denisd3d.keys4macros.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class FixedTooltipButton extends Button {

    public FixedTooltipButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
    }

    public FixedTooltipButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, OnTooltip pOnTooltip) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pOnTooltip);
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (this.isHoveredOrFocused()) {
            super.renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }
}
