package ml.denisd3d.keys4macros.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.client.ClientConfig;
import ml.denisd3d.keys4macros.server.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class MacrosScreen extends Screen {

    private final Screen parentScreen;
    public MacrosList.Entry selectedMacro;
    private MacrosList macrosList;
    private boolean isInitialised = false;

    public MacrosScreen(Minecraft ignoredMinecraft, Screen parent) {
        super(new TextComponent("Keys4Macros Config"));
        this.parentScreen = parent;
    }

    @Override
    public void init() {
        if (!this.isInitialised)
            this.macrosList = new MacrosList(this.minecraft, this);
        this.isInitialised = true;

        this.addWidget(this.macrosList);
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height - 29, 150, 20, new TranslatableComponent("gui.done"), (button) -> this.onClose()));
        this.addRenderableWidget(new Button(this.width / 2 + 75 + 20, this.height - 29, 75, 20, new TextComponent("New Macro"), (button) -> this.macrosList.addNewMacro()));
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.selectedMacro == null)
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);

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

        if (!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.selectedMacro.key))
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

    @Override
    public void onClose() {
        Keys4Macros.INSTANCE.clientHandler.config.macros.clear();
        for (MacrosList.Entry macroEntry : this.macrosList.children()) {
            if (macroEntry.macro instanceof ClientConfig.MacroEntry) {
                Keys4Macros.INSTANCE.clientHandler.config.macros.add((ClientConfig.MacroEntry) macroEntry.getUpdatedConfigEntry());
            } else {
                if (Keys4Macros.INSTANCE.clientHandler.serverMacrosIp.equals(this.macrosList.getCurrentServerIp()))
                {
                    int target = -1;
                    ClientConfig.ServerMacroEntry serverMacroEntry = null;
                    for (int i = 0; i < Keys4Macros.INSTANCE.clientHandler.config.server_macros.size(); i++) {
                        if (Keys4Macros.INSTANCE.clientHandler.config.server_macros.get(i).id.intValue() == ((ServerConfig.MacroEntry) macroEntry.macro).id && this.macrosList.getCurrentServerIp().equals(Keys4Macros.INSTANCE.clientHandler.config.server_macros.get(i).server)) {
                            target = i;
                            serverMacroEntry = Keys4Macros.INSTANCE.clientHandler.config.server_macros.get(i);
                        }
                    }
                    if (target == -1) {
                        serverMacroEntry = new ClientConfig.ServerMacroEntry(((ServerConfig.MacroEntry) macroEntry.macro).id, this.macrosList.getCurrentServerIp(), -1, 0);
                    }

                    serverMacroEntry.key = macroEntry.key.getValue();
                    switch (macroEntry.activeModifier) {
                        case CONTROL -> serverMacroEntry.modifiers = 2;
                        case SHIFT -> serverMacroEntry.modifiers = 1;
                        case ALT -> serverMacroEntry.modifiers = 4;
                        case NONE -> serverMacroEntry.modifiers = 0;
                    }

                    if (target != -1) {
                        Keys4Macros.INSTANCE.clientHandler.config.server_macros.set(target, serverMacroEntry);
                    } else {
                        Keys4Macros.INSTANCE.clientHandler.config.server_macros.add(serverMacroEntry);
                    }

                    for (ServerConfig.MacroEntry macroEntry2 : Keys4Macros.INSTANCE.clientHandler.serverMacros) {
                        for (int i = 0; i < Keys4Macros.INSTANCE.clientHandler.config.server_macros.size(); i++) {
                            if (Keys4Macros.INSTANCE.clientHandler.config.server_macros.get(i).id.intValue() == macroEntry2.id) {
                                macroEntry2.client_key = Keys4Macros.INSTANCE.clientHandler.config.server_macros.get(i).key;
                                macroEntry2.client_modifiers = Keys4Macros.INSTANCE.clientHandler.config.server_macros.get(i).modifiers;
                            }
                        }
                    }
                }
            }
        }
        Keys4Macros.INSTANCE.clientHandler.config.macros.addAll(this.macrosList.ignoredMacros);
        Keys4Macros.INSTANCE.clientHandler.config.save();
        if (this.minecraft != null && this.parentScreen != null)
            this.minecraft.setScreen(parentScreen);
        else
            super.onClose();
    }
}