package com.evga314.bloodvignette.client;

import com.evga314.bloodvignette.BloodVignetteMod;
import com.evga314.bloodvignette.config.BloodVignetteConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fStack;

/**
 * Live icon position / size editor for Blood Vignette (Evga314).
 * Coordinates: 0.0–1000.0 with decimals.
 * Drag works via mouseMoved + tick polling (reliable on 26.3 / mobile launchers).
 */
public class IconPositionScreen extends Screen {

    private static final Identifier WARNING_ICON_TEX = BloodVignetteMod.id("textures/gui/blood_warning_icon.png");
    private static final float COORD_MAX = 1000f;

    public static boolean active = false;

    private final Screen parent;

    private float draftX;
    private float draftY;
    private int draftSize;
    private float draftOpacity;

    /** Last committed values — Back reverts to these; Save updates them. */
    private float savedX;
    private float savedY;
    private int savedSize;
    private float savedOpacity;

    private EditBox sizeBox;
    private EditBox xBox;
    private EditBox yBox;
    private EditBox opacityBox;
    private boolean dragging;
    private float dragOffsetX;
    private float dragOffsetY;
    private boolean syncingBoxes;
    private double lastMouseX;
    private double lastMouseY;

    public IconPositionScreen(Screen parent) {
        super(Component.translatable("config.bloodvignette.position_editor.title"));
        this.parent = parent;
        this.draftX = BloodVignetteConfig.iconPosX;
        this.draftY = BloodVignetteConfig.iconPosY;
        this.draftSize = BloodVignetteConfig.iconSize;
        this.draftOpacity = BloodVignetteConfig.iconOpacity;
        this.savedX = this.draftX;
        this.savedY = this.draftY;
        this.savedSize = this.draftSize;
        this.savedOpacity = this.draftOpacity;
    }

    @Override
    protected void init() {
        active = true;
        BloodVignettePulse.forcePreview(true);

        int right = this.width - 8;
        int top = 8;
        int btnW = 90;
        int btnH = 20;
        int gap = 4;

        this.addRenderableWidget(Button.builder(
                Component.translatable("config.bloodvignette.position_editor.save"),
                b -> saveAndStay()
        ).bounds(right - btnW, top, btnW, btnH).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("config.bloodvignette.position_editor.reset"),
                b -> resetDefaults()
        ).bounds(right - btnW, top + btnH + gap, btnW, btnH).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("config.bloodvignette.position_editor.back"),
                b -> goBack(false)
        ).bounds(right - btnW, top + 2 * (btnH + gap), btnW, btnH).build());

        // Row 1: Icon size label + field (label drawn in extractRenderState)
        this.sizeBox = new EditBox(this.font, 8, 22, 48, 18,
                Component.translatable("config.bloodvignette.position_editor.size_hint"));
        this.sizeBox.setMaxLength(4);
        this.sizeBox.setValue(String.valueOf(draftSize));
        this.sizeBox.setResponder(this::onSizeTyped);
        this.addRenderableWidget(this.sizeBox);

        // Row 2: X / Y fields — labels drawn above them so nothing overlaps
        this.xBox = new EditBox(this.font, 8, 58, 80, 18, Component.literal("X"));
        this.xBox.setMaxLength(10);
        this.xBox.setResponder(this::onXTyped);
        this.addRenderableWidget(this.xBox);

        this.yBox = new EditBox(this.font, 100, 58, 80, 18, Component.literal("Y"));
        this.yBox.setMaxLength(10);
        this.yBox.setResponder(this::onYTyped);
        this.addRenderableWidget(this.yBox);

        // Opacity 0.0–1.0
        this.opacityBox = new EditBox(this.font, 8, 94, 72, 18, Component.literal("Opacity"));
        this.opacityBox.setMaxLength(6);
        this.opacityBox.setResponder(this::onOpacityTyped);
        this.addRenderableWidget(this.opacityBox);

        syncBoxesFromDraft();
        buildNudgePad();
    }

    /**
     * Clean cross layout — no overlap.
     * From center outward: BIG (±10) → MID (±1, almost flush) → TINY (±0.1).
     */
        private void buildNudgePad() {
        int big = 22;
        int mid = 14;
        int tiny = 9;
        int gap = 2;

        int padRadius = big + gap + mid + gap + tiny + 6;
        int cx = this.width - padRadius - 10;
        int cy = this.height - padRadius - 10;

        // UP (from center outward: big → mid → tiny)
        int yBig = cy - big / 2 - gap - big;
        addNudge(cx - big / 2, yBig, big, big, "\u25B2", 0f, -10f);
        int yMid = yBig - gap - mid;
        addNudge(cx - mid / 2, yMid, mid, mid, "\u25B2", 0f, -1f);
        int yTiny = yMid - gap - tiny;
        addNudge(cx - tiny / 2, yTiny, tiny, tiny, "\u2191", 0f, -0.1f);

        // DOWN
        yBig = cy + big / 2 + gap;
        addNudge(cx - big / 2, yBig, big, big, "\u25BC", 0f, 10f);
        yMid = yBig + big + gap;
        addNudge(cx - mid / 2, yMid, mid, mid, "\u25BC", 0f, 1f);
        yTiny = yMid + mid + gap;
        addNudge(cx - tiny / 2, yTiny, tiny, tiny, "\u2193", 0f, 0.1f);

        // LEFT
        int xBig = cx - big / 2 - gap - big;
        addNudge(xBig, cy - big / 2, big, big, "\u25C0", -10f, 0f);
        int xMid = xBig - gap - mid;
        addNudge(xMid, cy - mid / 2, mid, mid, "\u25C0", -1f, 0f);
        int xTiny = xMid - gap - tiny;
        addNudge(xTiny, cy - tiny / 2, tiny, tiny, "\u2190", -0.1f, 0f);

        // RIGHT
        xBig = cx + big / 2 + gap;
        addNudge(xBig, cy - big / 2, big, big, "\u25B6", 10f, 0f);
        xMid = xBig + big + gap;
        addNudge(xMid, cy - mid / 2, mid, mid, "\u25B6", 1f, 0f);
        xTiny = xMid + mid + gap;
        addNudge(xTiny, cy - tiny / 2, tiny, tiny, "\u2192", 0.1f, 0f);
    }


    private void addNudge(int x, int y, int w, int h, String label, float dx, float dy) {
        this.addRenderableWidget(Button.builder(Component.literal(label), b -> nudge(dx, dy))
                .bounds(x, y, w, h)
                .build());
    }

    private void nudge(float dx, float dy) {
        draftX = clampCoord(draftX + dx);
        draftY = clampCoord(draftY + dy);
        pushDraftToConfig();
        syncBoxesFromDraft();
    }

    private void onSizeTyped(String text) {
        if (syncingBoxes) return;
        try {
            int v = Integer.parseInt(text.trim());
            draftSize = Mth.clamp(v, 16, 128);
            pushDraftToConfig();
        } catch (NumberFormatException ignored) {
        }
    }

    private void onXTyped(String text) {
        if (syncingBoxes) return;
        try {
            draftX = clampCoord(Float.parseFloat(text.trim().replace(',', '.')));
            pushDraftToConfig();
        } catch (NumberFormatException ignored) {
        }
    }

    private void onYTyped(String text) {
        if (syncingBoxes) return;
        try {
            draftY = clampCoord(Float.parseFloat(text.trim().replace(',', '.')));
            pushDraftToConfig();
        } catch (NumberFormatException ignored) {
        }
    }

    private void onOpacityTyped(String text) {
        if (syncingBoxes) return;
        try {
            draftOpacity = Mth.clamp(Float.parseFloat(text.trim().replace(',', '.')), 0f, 1f);
            pushDraftToConfig();
        } catch (NumberFormatException ignored) {
        }
    }

    private void syncBoxesFromDraft() {
        if (xBox == null || yBox == null || sizeBox == null || opacityBox == null) return;
        syncingBoxes = true;
        xBox.setValue(formatCoord(draftX));
        yBox.setValue(formatCoord(draftY));
        sizeBox.setValue(String.valueOf(draftSize));
        opacityBox.setValue(formatOpacity(draftOpacity));
        syncingBoxes = false;
    }

    private static String formatOpacity(float v) {
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    private static String formatCoord(float v) {
        if (Math.abs(v - Math.round(v)) < 0.001f) {
            return String.valueOf(Math.round(v));
        }
        return String.format(java.util.Locale.US, "%.1f", v);
    }

    private void resetDefaults() {
        draftX = 500f;
        draftY = 180f;
        draftSize = 48;
        draftOpacity = 0.85f;
        pushDraftToConfig();
        syncBoxesFromDraft();
    }

    private void saveAndStay() {
        applySizeFromBox();
        applyCoordsFromBoxes();
        BloodVignetteConfig.iconPosX = draftX;
        BloodVignetteConfig.iconPosY = draftY;
        BloodVignetteConfig.iconSize = draftSize;
        BloodVignetteConfig.iconOpacity = draftOpacity;
        BloodVignetteConfig.save();
        savedX = draftX;
        savedY = draftY;
        savedSize = draftSize;
        savedOpacity = draftOpacity;
        syncBoxesFromDraft();
    }

    private boolean isDirty() {
        applySizeFromBox();
        applyCoordsFromBoxes();
        return Float.compare(draftX, savedX) != 0
                || Float.compare(draftY, savedY) != 0
                || draftSize != savedSize
                || Float.compare(draftOpacity, savedOpacity) != 0;
    }

    private void goBack(boolean ignored) {
        if (isDirty()) {
            // Ask before discarding uncommitted edits
            Minecraft.getInstance().gui.setScreen(new ConfirmDiscardScreen(this));
            return;
        }
        discardAndClose();
    }

    void discardAndClose() {
        BloodVignetteConfig.iconPosX = savedX;
        BloodVignetteConfig.iconPosY = savedY;
        BloodVignetteConfig.iconSize = savedSize;
        BloodVignetteConfig.iconOpacity = savedOpacity;
        BloodVignetteConfig.save();
        active = false;
        BloodVignettePulse.forcePreview(false);
        Minecraft.getInstance().gui.setScreen(parent);
    }

    void keepEditing() {
        Minecraft.getInstance().gui.setScreen(this);
    }

    private void applySizeFromBox() {
        try {
            draftSize = Mth.clamp(Integer.parseInt(sizeBox.getValue().trim()), 16, 128);
        } catch (NumberFormatException e) {
            sizeBox.setValue(String.valueOf(draftSize));
        }
    }

    private void applyCoordsFromBoxes() {
        try {
            draftX = clampCoord(Float.parseFloat(xBox.getValue().trim().replace(',', '.')));
        } catch (NumberFormatException e) {
            xBox.setValue(formatCoord(draftX));
        }
        try {
            draftY = clampCoord(Float.parseFloat(yBox.getValue().trim().replace(',', '.')));
        } catch (NumberFormatException e) {
            yBox.setValue(formatCoord(draftY));
        }
    }

    private void pushDraftToConfig() {
        BloodVignetteConfig.iconPosX = draftX;
        BloodVignetteConfig.iconPosY = draftY;
        BloodVignetteConfig.iconSize = draftSize;
        BloodVignetteConfig.iconOpacity = draftOpacity;
    }

    private static float clampCoord(float v) {
        return Math.max(0f, Math.min(COORD_MAX, v));
    }

    private float iconPixelXF() {
        return this.width * (draftX / COORD_MAX) - draftSize / 2f;
    }

    private float iconPixelYF() {
        return this.height * (draftY / COORD_MAX) - draftSize / 2f;
    }

    private boolean hitsIcon(double mx, double my) {
        float x = iconPixelXF();
        float y = iconPixelYF();
        // Slightly larger hitbox for easier grab on mobile
        float pad = 4f;
        return mx >= x - pad && mx <= x + draftSize + pad && my >= y - pad && my <= y + draftSize + pad;
    }

    private void updateDragTo(double mouseX, double mouseY) {
        float left = (float) mouseX - dragOffsetX;
        float top = (float) mouseY - dragOffsetY;
        float centerX = left + draftSize / 2f;
        float centerY = top + draftSize / 2f;
        draftX = clampCoord(centerX / this.width * COORD_MAX);
        draftY = clampCoord(centerY / this.height * COORD_MAX);
        pushDraftToConfig();
        syncBoxesFromDraft();
    }

    @Override
    public void tick() {
        super.tick();
        // Fallback drag path: if button is held but mouseDragged isn't delivered (mobile / SDL)
        if (dragging) {
            updateDragTo(lastMouseX, lastMouseY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, ARGB.colorFromFloat(0.25f, 0f, 0f, 0f));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Keep last mouse for tick-based drag
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        float pulse = BloodVignettePulse.getSmoothPulse();
        float scale = 0.85f + 0.25f * pulse;
        float alpha = Math.max(0.15f, draftOpacity);

        float ix = iconPixelXF();
        float iy = iconPixelYF();

        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(ix + draftSize / 2f, iy + draftSize / 2f);
        pose.scale(scale, scale);
        pose.translate(-draftSize / 2f, -draftSize / 2f);

        int color = ARGB.colorFromFloat(alpha, 1f, 1f, 1f);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                WARNING_ICON_TEX,
                0, 0,
                0f, 0f,
                draftSize, draftSize,
                draftSize, draftSize,
                color
        );
        pose.popMatrix();

        if (dragging || hitsIcon(mouseX, mouseY)) {
            int outline = ARGB.colorFromFloat(0.7f, 1f, 0.85f, 0.3f);
            int x0 = (int) Math.floor(ix) - 2;
            int y0 = (int) Math.floor(iy) - 2;
            int x1 = (int) Math.ceil(ix + draftSize) + 2;
            int y1 = (int) Math.ceil(iy + draftSize) + 2;
            graphics.fill(x0, y0, x1, y0 + 2, outline);
            graphics.fill(x0, y1 - 2, x1, y1, outline);
            graphics.fill(x0, y0, x0 + 2, y1, outline);
            graphics.fill(x1 - 2, y0, x1, y1, outline);
        }

        graphics.text(this.font,
                Component.translatable("config.bloodvignette.position_editor.size_label").getString(),
                8, 10, 0xFFCCCCCC, true);
        graphics.text(this.font,
                Component.translatable("config.bloodvignette.position_editor.x_label").getString(),
                8, 46, 0xFFCCCCCC, true);
        graphics.text(this.font,
                Component.translatable("config.bloodvignette.position_editor.y_label").getString(),
                100, 46, 0xFFCCCCCC, true);
        graphics.text(this.font,
                Component.translatable("config.bloodvignette.position_editor.opacity_label").getString(),
                8, 82, 0xFFCCCCCC, true);

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    /** Primary click: GLFW used 0, SDL3 uses 1 for left button — accept both. */
    private static boolean isPrimaryMouse(int button) {
        return button == 0 || button == 1;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        double mouseX = event.x();
        double mouseY = event.y();
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        // Let text fields / buttons handle their own clicks first
        if (super.mouseClicked(event, doubled)) {
            return true;
        }

        if (isPrimaryMouse(event.button()) && hitsIcon(mouseX, mouseY)) {
            dragging = true;
            dragOffsetX = (float) mouseX - iconPixelXF();
            dragOffsetY = (float) mouseY - iconPixelYF();
            this.setFocused(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        lastMouseX = event.x();
        lastMouseY = event.y();
        if (dragging && isPrimaryMouse(event.button())) {
            updateDragTo(event.x(), event.y());
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        if (dragging) {
            updateDragTo(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        lastMouseX = event.x();
        lastMouseY = event.y();
        if (isPrimaryMouse(event.button())) {
            if (dragging) {
                dragging = false;
                return true;
            }
            dragging = false;
        }
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        goBack(false);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
