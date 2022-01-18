package ml.denisd3d.keys4macros.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import ml.denisd3d.keys4macros.IMacro;
import ml.denisd3d.keys4macros.Keys4Macros;
import ml.denisd3d.keys4macros.client.ClientConfig;
import ml.denisd3d.keys4macros.server.ServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MacrosList extends ContainerObjectSelectionList<MacrosList.Entry> {

    private final MacrosScreen macrosScreen;
    public List<ClientConfig.MacroEntry> ignoredMacros = new ArrayList<>();

    public MacrosList(Minecraft pMinecraft, MacrosScreen macrosScreen) {
        super(pMinecraft, macrosScreen.width + 20 /*Width*/, macrosScreen.height /* Height*/, 20 /*Y0*/, macrosScreen.height - 32 /*Y1*/, 20 /*Item height*/);
        this.macrosScreen = macrosScreen;

        this.setRenderHeader(true, 10);

        if (pMinecraft.level != null) {
            this.setRenderBackground(false);
            this.setRenderTopAndBottom(false);
        }

        for (ClientConfig.MacroEntry macro : Keys4Macros.INSTANCE.clientHandler.config.macros) {
            if (macro.server.isEmpty() || (this.isConnectedToServer() && macro.server.equals(this.getCurrentServerIp()))) {
                this.addEntry(new Entry(macro));
            } else {
                this.ignoredMacros.add(macro);
            }
        }
        if (Keys4Macros.INSTANCE.clientHandler.serverMacros != null && isConnectedToServer() && pMinecraft.getCurrentServer() != null && Keys4Macros.INSTANCE.clientHandler.serverMacrosIp.equals(pMinecraft.getCurrentServer().ip)) {
            for (ServerConfig.MacroEntry macro : Keys4Macros.INSTANCE.clientHandler.serverMacros) {
                this.addEntry(new Entry(macro));
            }
        }
    }

    public String getCurrentServerIp() {
        if(this.minecraft.getCurrentServer() != null)
            return this.minecraft.getCurrentServer().ip;
        else
            return "";
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 60;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 180;
    }

    @Override
    public int getMaxPosition() {
        return super.getMaxPosition() + 10;
    }

    public void tick() {
        for (MacrosList.Entry entry : this.children()) {
            entry.tick();
        }
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        for (MacrosList.Entry entry : this.children()) {
            entry.sideSelect.renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (MacrosList.Entry entry : this.children()) {
            if (entry == getEntryAtPosition(pMouseX, pMouseY))
                continue;
            entry.commandBox.setFocus(false);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public void addNewMacro() {
        this.addEntry(new Entry(new ClientConfig.MacroEntry(-1, 0, "", true, "")));
    }

    public boolean isConnectedToServer() {
        return this.minecraft.getConnection() != null && this.minecraft.getConnection().getConnection().isConnected();
    }

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ContainerObjectSelectionList.Entry<MacrosList.Entry> {

        public final IMacro macro;
        private final Button changeButton, deleteButton, shouldSend, sideSelect;
        private final EditBox commandBox;
        public InputConstants.Key key;
        public KeyModifier activeModifier = KeyModifier.NONE;

        public Entry(IMacro macro) {
            this.macro = macro;
            this.key = macro.getKey() == -1 ? InputConstants.UNKNOWN : InputConstants.getKey(this.macro.getKey(), 0);
            switch (this.macro.getModifiers()) {
                case 2 -> this.activeModifier = KeyModifier.CONTROL;
                case 1 -> this.activeModifier = KeyModifier.SHIFT;
                case 4 -> this.activeModifier = KeyModifier.ALT;
            }

            this.commandBox = new EditBox(MacrosList.this.minecraft.font, 0, 0, 180, 18, new TextComponent("Command"));
            this.commandBox.setMaxLength(256);
            this.commandBox.setValue(this.macro.getCommand());
            this.commandBox.moveCursorToStart();

            this.changeButton = new Button(0, 0, 75 + 20, 20, this.activeModifier.getCombinedName(this.key, () -> this.key.getDisplayName()), pButton -> MacrosList.this.macrosScreen.selectedMacro = this);
            this.shouldSend = new Button(0, 0, 40, 20, new TextComponent(macro.getSend() ? "SEND" : "WRITE"), pButton -> {
                this.macro.setSend(!this.macro.getSend());
                pButton.setMessage(new TextComponent(macro.getSend() ? "SEND" : "WRITE"));
            });
            this.deleteButton = new Button(0, 0, 20, 20, new TextComponent("X"), pButton -> MacrosList.this.minecraft.setScreen(new ConfirmScreen(t -> {
                MacrosList.this.minecraft.setScreen(MacrosList.this.macrosScreen);
                if (t) MacrosList.this.removeEntry(this);
            }, new TextComponent("Are you sure to delete this macro ?"), new TextComponent("Command: " + this.commandBox.getValue() + "\nKey: ").append(this.activeModifier.getCombinedName(this.key, () -> this.key.getDisplayName())).append(new TextComponent("\nMode: " + (macro.getSend() ? "SEND" : "WRITE"))))));

            this.sideSelect = new FixedTooltipButton(0, 0, 20, 20, new TextComponent(this.macro instanceof ClientConfig.MacroEntry && ((ClientConfig.MacroEntry) this.macro).server.isEmpty() ? "C" : "S"), pButton -> {
                if (MacrosList.this.isConnectedToServer() && MacrosList.this.minecraft.getCurrentServer() != null) {
                    if (!(this.macro instanceof ClientConfig.MacroEntry))
                        return;
                    if (((ClientConfig.MacroEntry) this.macro).server.isEmpty()) {
                        ((ClientConfig.MacroEntry) this.macro).server = MacrosList.this.minecraft.getCurrentServer().ip;
                    } else {
                        ((ClientConfig.MacroEntry) this.macro).server = "";
                    }
                }
            }, (pButton, pPoseStack, pMouseX, pMouseY) -> {
                if (pButton.active) {
                    MacrosList.this.macrosScreen.renderTooltip(pPoseStack, MacrosList.this.minecraft.font.split(FormattedText.of("C : Client macro (available everywhere)\nS : Server macro (available only on the current server)"), Math.max(MacrosList.this.macrosScreen.width / 2 - 43, 170)), pMouseX, pMouseY);
                } else {
                    if (this.macro instanceof ClientConfig.MacroEntry) {
                        MacrosList.this.macrosScreen.renderTooltip(pPoseStack, MacrosList.this.minecraft.font.split(FormattedText.of("Setting macros for a specific server is only available while connected to it"), Math.max(MacrosList.this.macrosScreen.width / 2 - 43, 170)), pMouseX, pMouseY);
                    } else {
                        MacrosList.this.macrosScreen.renderTooltip(pPoseStack, MacrosList.this.minecraft.font.split(FormattedText.of("This macro is defined by the server. You may only change the key"), Math.max(MacrosList.this.macrosScreen.width / 2 - 43, 170)), pMouseX, pMouseY);
                    }
                }
            });
            if (!MacrosList.this.isConnectedToServer()) {
                this.sideSelect.active = false;
            }

            if (this.macro instanceof ServerConfig.MacroEntry) {
                this.sideSelect.active = false;
                this.commandBox.active = false;
                this.commandBox.setEditable(false);
                this.shouldSend.active = false;
                this.deleteButton.active = false;
            }
        }


        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.deleteButton, this.changeButton, this.shouldSend, this.commandBox, this.sideSelect);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.deleteButton, this.changeButton, this.shouldSend, this.commandBox, this.sideSelect);
        }

        public void tick() {
            this.commandBox.tick();
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            int widthCommandBox = pWidth - (20 + 10 + 10 + 95 + 10 + 40 + 10 + 20 + 20);

            this.sideSelect.x = pLeft;
            this.sideSelect.y = pTop;
            if (this.macro instanceof ClientConfig.MacroEntry) {
                this.sideSelect.setMessage(new TextComponent(((ClientConfig.MacroEntry) this.macro).server.isEmpty() ? "C" : "S"));
            }
            this.sideSelect.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            this.commandBox.x = pLeft + 30;
            this.commandBox.y = pTop;
            this.commandBox.setWidth(widthCommandBox);
            this.commandBox.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            this.changeButton.x = pLeft + 30 + widthCommandBox + 10; //pLeft + 30 + widthCommandBox + 10;
            this.changeButton.y = pTop;
            this.changeButton.setMessage(this.activeModifier.getCombinedName(this.key, () -> this.key.getDisplayName()));
            if (MacrosList.this.macrosScreen.selectedMacro == this) {
                this.changeButton.setMessage((new TextComponent("> ")).append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.YELLOW)).append(" <").withStyle(ChatFormatting.YELLOW));
            }
            this.changeButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            this.shouldSend.x = pLeft + 30 + widthCommandBox + 10 + 95 + 10;//pLeft + pWidth / 2 + 75 + 20 + 10;
            this.shouldSend.y = pTop;
            this.shouldSend.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            this.deleteButton.x = pLeft + 30 + widthCommandBox + 10 + 95 + 10 + 40 + 10; //pLeft + pWidth / 2 + 75 + 20 + 10 + 40 + 10;
            this.deleteButton.y = pTop;
            this.deleteButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }

        @Override
        public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
            return super.mouseReleased(pMouseX, pMouseY, pButton);
        }

        public IMacro getUpdatedConfigEntry() {
            this.macro.setCommand(this.commandBox.getValue());
            this.macro.setKey(this.key.getValue());
            switch (this.activeModifier) {
                case CONTROL -> this.macro.setModifiers(2);
                case SHIFT -> this.macro.setModifiers(1);
                case ALT -> this.macro.setModifiers(4);
                case NONE -> this.macro.setModifiers(0);
            }
            return this.macro;
        }
    }
}
