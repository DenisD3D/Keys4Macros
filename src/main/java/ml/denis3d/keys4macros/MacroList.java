package ml.denis3d.keys4macros;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.controls.ControlList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;


@OnlyIn(Dist.CLIENT)
public class MacroList extends ContainerObjectSelectionList<ControlList.Entry> {
    private final ConfigScreen configScreen;

    public MacroList(ConfigScreen configScreen, Minecraft mcIn) {
        super(mcIn, configScreen.width + 45, configScreen.height, 43, configScreen.height - 32, 20);
        this.configScreen = configScreen;
        List<ModConfig.MacroEntry> macroEntries = Keys4Macros.INSTANCE.config.macros;

        for (ModConfig.MacroEntry macroEntry : macroEntries) {
            this.addEntry(new MacroList.KeyEntry(macroEntry));
        }
    }

    public void update() {
        this.clearEntries();
        List<ModConfig.MacroEntry> macroEntries = Keys4Macros.INSTANCE.config.macros;
        for (ModConfig.MacroEntry macroEntry : macroEntries) {
            this.addEntry(new MacroList.KeyEntry(macroEntry));
        }
    }

    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15 + 20;
    }

    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    @OnlyIn(Dist.CLIENT)
    public class KeyEntry extends ControlList.Entry {
        /**
         * The macroEntry specified for this KeyEntry
         */
        private final ModConfig.MacroEntry macroEntry;
        private final Button btnChangeKeyBinding;
        private final EditBox commandTextFieldWidget;
        private final Button removeButton;

        private KeyEntry(final ModConfig.MacroEntry macroEntry) {
            this.macroEntry = macroEntry;
            this.btnChangeKeyBinding = new Button(0, 0, 75 + 20, 20, new TextComponent("Some key"), (p_214386_2_) -> MacroList.this.configScreen.buttonId = this.macroEntry);
            this.commandTextFieldWidget = new EditBox(MacroList.this.minecraft.font, 75 + 20 + 20, 0, MacroList.this.width - 75 - 20 - 20 - 20 - 40, 20, new TextComponent("Some command"));
            this.commandTextFieldWidget.setValue(macroEntry.command);
            this.removeButton = new Button(MacroList.this.width - 75 - 20 - 20 - 20 + 10, 0, 20, 20, new TextComponent("X"), p_onPress_1_ -> {
                Keys4Macros.INSTANCE.config.macros.remove(macroEntry);
                ModConfig.reload();
                //MacroList.this.configScreen.macrosList.update();
            });
        }

        public void tick() {
            //this.commandTextFieldWidget.tick();
        }

        @Override
        public void render(PoseStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
            boolean flag = MacroList.this.configScreen.buttonId == this.macroEntry;
            this.btnChangeKeyBinding.x = p_230432_4_ - 90;
            this.btnChangeKeyBinding.y = p_230432_3_;

            InputConstants.Key input = InputConstants.getKey(this.macroEntry.key, -1);
            Component message = input.getDisplayName();
            this.btnChangeKeyBinding.setMessage(message.getString().startsWith("key") ? new TextComponent(message.getString().substring(message.getString().length() - 1)) : message);
            boolean flag1 = false;
            if (!input.equals(InputConstants.UNKNOWN)) {
                for (ModConfig.MacroEntry macroEntry : Keys4Macros.INSTANCE.config.macros) {
                    if (this.macroEntry != macroEntry && this.macroEntry.key.equals(macroEntry.key)) {
                        flag1 = true;
                        break;
                    }
                }
            }

            if (flag) {
                this.btnChangeKeyBinding.setMessage((new TextComponent("> ")).append(this.btnChangeKeyBinding.getMessage().plainCopy().withStyle(style -> style.withColor(ChatFormatting.YELLOW))).append(" <").withStyle(style -> style.withColor(ChatFormatting.YELLOW)));
            } else if (flag1) {
                this.btnChangeKeyBinding.setMessage(this.btnChangeKeyBinding.getMessage().plainCopy().withStyle(style -> style.withColor(ChatFormatting.RED)));
            }

            this.btnChangeKeyBinding.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);

            this.commandTextFieldWidget.x = p_230432_4_ - 90 + 75 + 20;
            this.commandTextFieldWidget.y = p_230432_3_;
            //this.commandTextFieldWidget.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);

            this.removeButton.x = p_230432_4_ + 200;
            this.removeButton.y = p_230432_3_;
            this.removeButton.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.btnChangeKeyBinding);
        }

        public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
            return this.btnChangeKeyBinding.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_) || this.removeButton.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
        }

        public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
            return this.btnChangeKeyBinding.mouseReleased(p_231048_1_, p_231048_3_, p_231048_5_) || this.removeButton.mouseReleased(p_231048_1_, p_231048_3_, p_231048_5_);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.btnChangeKeyBinding);
        }
    }
}
