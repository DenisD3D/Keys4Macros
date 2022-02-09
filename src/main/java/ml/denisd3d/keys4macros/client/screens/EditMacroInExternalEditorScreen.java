package ml.denisd3d.keys4macros.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class EditMacroInExternalEditorScreen extends Screen {
    public static final HashMap<UUID, File> externalMacros = new HashMap<>();
    private final EditMacrosScreen editMacrosScreen;
    private final EditMacrosList.Entry currentMacro;

    public EditMacroInExternalEditorScreen(EditMacrosScreen editMacrosScreen, EditMacrosList.Entry currentMacro) {
        super(new TextComponent("Editing macro in external editor"));
        this.editMacrosScreen = editMacrosScreen;
        this.currentMacro = currentMacro;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height / 2 - 40, 150, 20, new TextComponent("Open external editor"), pButton -> {
            try {
                openExternal(getOrCreateMacroFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height / 2 - 10, 150, 20, new TextComponent("Import changes"), pButton -> {
            reloadAllExternalMacros();
            this.onClose();
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height / 2 + 20, 150, 20, new TranslatableComponent("gui.back"), pButton -> {
            this.onClose();
        }));
    }

    private void reloadAllExternalMacros() {
        externalMacros.forEach((id, file) -> {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                Iterator<String> iterator = Files.readAllLines(file.toPath()).iterator();
                while (iterator.hasNext()) {
                    String s = iterator.next();
                    stringBuilder.append(StringEscapeUtils.escapeJava(s));
                    if (iterator.hasNext()) {
                        stringBuilder.append("\\n");
                    }
                }
                editMacrosScreen.macrosList.children().stream()
                        .filter(entry -> entry.macro.getId().equals(id))
                        .findFirst()
                        .ifPresent(entry -> entry.commandBox.setValue(stringBuilder.toString()));
                this.editMacrosScreen.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private File getOrCreateMacroFile() throws IOException {
        if (externalMacros.containsKey(this.currentMacro.macro.getId()))
            return externalMacros.get(this.currentMacro.macro.getId());

        File tempFile = File.createTempFile("keys4macros-", ".macro");
        tempFile.deleteOnExit();
        FileWriter myWriter = new FileWriter(tempFile);
        myWriter.write(StringEscapeUtils.unescapeJava(this.currentMacro.commandBox.getValue()));
        myWriter.close();

        externalMacros.put(this.currentMacro.macro.getId(), tempFile);
        return tempFile;
    }

    private void openExternal(File file) throws IOException {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + file.getCanonicalPath());
        } else {
            Desktop.getDesktop().edit(file);
        }
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.minecraft == null)
            return;

        this.renderBackground(pPoseStack);
        drawCenteredString(pPoseStack, this.font, this.title, this.width / 2, 8, 16777215);
        drawCenteredString(pPoseStack, this.font, new TranslatableComponent("gui.back").getString() + " : return to macros list without importing change.", this.width / 2, this.height - 39, 16777215);
        drawCenteredString(pPoseStack, this.font, "They can be imported later from this screen", this.width / 2, this.height - 29, 16777215);

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void onClose() {
        this.editMacrosScreen.macrosList.children().forEach(EditMacrosList.Entry::updateExternal);
        if (this.minecraft != null)
            this.minecraft.setScreen(this.editMacrosScreen);
        else
            super.onClose();
    }
}
