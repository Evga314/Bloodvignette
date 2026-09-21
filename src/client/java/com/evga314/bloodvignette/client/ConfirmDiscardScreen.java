package com.evga314.bloodvignette.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

/**
 * Simple confirm dialog when leaving the icon editor with unsaved changes.
 */
public class ConfirmDiscardScreen extends Screen {

    private final IconPositionScreen editor;

    public ConfirmDiscardScreen(IconPositionScreen editor) {
        super(Component.translatable("config.bloodvignette.position_editor.unsaved_title"));
        this.editor = editor;
    }

    @Override
    protected void init() {
        int w = 160;
        int h = 20;
        int cx = this.width / 2;
        int cy = this.height / 2;

        this.addRenderableWidget(Button.builder(
                Component.translatable("config.bloodvignette.position_editor.unsaved_discard"),
                b -> editor.discardAndClose()
        ).bounds(cx - w - 8, cy + 20, w, h).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("config.bloodvignette.position_editor.unsaved_cancel"),
                b -> editor.keepEditing()
        ).bounds(cx + 8, cy + 20, w, h).build());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, ARGB.colorFromFloat(0.55f, 0f, 0f, 0f));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        String title = Component.translatable("config.bloodvignette.position_editor.unsaved_title").getString();
        String body = Component.translatable("config.bloodvignette.position_editor.unsaved_body").getString();
        int tw = this.font.width(title);
        int bw = this.font.width(body);
        graphics.text(this.font, title, (this.width - tw) / 2, this.height / 2 - 30, 0xFFFFFF, true);
        graphics.text(this.font, body, (this.width - bw) / 2, this.height / 2 - 12, 0xFFCCCCCC, true);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        editor.keepEditing();
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
