package ml.denisd3d.keys4macros.client.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

public class DoubleClickableEditBox extends EditBox {
    public static final DoubleClickableEditBox.OnTooltip NO_TOOLTIP = (p_93740_, p_93741_, p_93742_, p_93743_) -> {
    };
    private final OnPress onDoubleClick;
    protected DoubleClickableEditBox.OnTooltip onTooltip = NO_TOOLTIP;
    private long lastClick = 0L;

    public DoubleClickableEditBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress onDoubleClick) {
        super(pFont, pX, pY, pWidth, pHeight, pMessage);
        this.onDoubleClick = onDoubleClick;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (super.mouseClicked(pMouseX, pMouseY, pButton)) {
            if (System.currentTimeMillis() - lastClick < 200) {
                onDoubleClick.onPress(this);
            }
            lastClick = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        this.onTooltip.onTooltip(this, pPoseStack, pMouseX, pMouseY);
    }

    public void setOnTooltip(OnTooltip onTooltip) {
        this.onTooltip = onTooltip;
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnPress {
        void onPress(DoubleClickableEditBox doubleClickableEditBox);
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnTooltip {
        void onTooltip(DoubleClickableEditBox pEditableTextBox, PoseStack pPoseStack, int pMouseX, int pMouseY);

        default void narrateTooltip(Consumer<Component> p_168842_) {
        }
    }
}
