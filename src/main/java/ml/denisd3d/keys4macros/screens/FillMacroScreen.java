package ml.denisd3d.keys4macros.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import ml.denisd3d.keys4macros.IMacro;
import ml.denisd3d.keys4macros.client.ClientConfig;
import ml.denisd3d.keys4macros.Keys4Macros;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class FillMacroScreen extends Screen {
    protected static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("keys4macros", "textures/gui/fill_macro.png");
    public final List<String> variables;
    private final IMacro macroEntry;
    private FillMacroList fillMacrosList;

    public FillMacroScreen(IMacro macroEntry, List<String> variables) {
        super(new TextComponent("Fill macro variables"));
        this.macroEntry = macroEntry;
        this.variables = variables;
    }

    @Override
    public void init() {
        this.fillMacrosList = new FillMacroList(this.minecraft, this);
        this.addWidget(this.fillMacrosList);

        this.addRenderableWidget(new Button(this.width / 2 - 75 - 10, this.height / 2 + 166 / 2 - 24, 75, 20, new TranslatableComponent(this.macroEntry.getSend() ? "Send" : "Write"), (button) -> {
            Keys4Macros.INSTANCE.clientHandler.build_variable_and_send_write(macroEntry, this.fillMacrosList.children().stream().map(FillMacroList.Entry::get_value).collect(Collectors.toList()));
            this.onClose();
        }));
        this.addRenderableWidget(new Button(this.width / 2 + 10, this.height / 2 + 166 / 2 - 24, 75, 20, new TextComponent("Cancel"), (button) -> this.onClose()));
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.minecraft == null)
            return;

        this.renderBackground(pPoseStack);

        this.renderBg(pPoseStack);
        this.fillMacrosList.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        this.renderHeaderFooter(pPoseStack);
        this.font.draw(pPoseStack, this.title, (this.width >> 1) - (248 >> 1) + 10, (this.height >> 1) - (166 >> 1) + 10, 4210752);

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    private void renderHeaderFooter(PoseStack pPoseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);

        this.blit(pPoseStack, this.width / 2 - 248 / 2, this.height / 2 - 166 / 2, 0, 0, 248, 25);
        this.blit(pPoseStack, this.width / 2 - 248 / 2, this.height / 2 - 166 / 2 + 166 - 25, 0, 166 - 25, 248, 166);
    }

    protected void renderBg(PoseStack pPoseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);

        this.blit(pPoseStack, this.width / 2 - 248 / 2, this.height / 2 - 166 / 2 + 25, 0, 25, 248, 166 - 25 * 2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
