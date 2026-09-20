package com.evga314.bloodvignette.client;

import com.evga314.bloodvignette.BloodVignetteMod;
import com.evga314.bloodvignette.config.BloodVignetteConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix3x2fStack;

/**
 * HUD rendering for the blood vignette and warning icon.
 * Author: Evga314
 */
public final class BloodVignetteHud {

    private static final Identifier VIGNETTE_TEX = BloodVignetteMod.id("textures/gui/blood_vignette.png");
    private static final Identifier WARNING_ICON_TEX = BloodVignetteMod.id("textures/gui/blood_warning_icon.png");

    private BloodVignetteHud() {}

    public static void renderVignette(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!BloodVignetteConfig.enableVignette) return;

        float act = BloodVignettePulse.getActivation();
        if (act <= 0.001f) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        float health = player.getHealth();
        // Allow rendering during fade-out even if health already recovered
        float danger;
        if (health > 0 && health <= BloodVignetteConfig.healthThreshold) {
            danger = 1f - Mth.clamp(health / BloodVignetteConfig.healthThreshold, 0f, 1f);
        } else {
            danger = 0.35f; // mild tint while fading out
        }

        float pulse = BloodVignettePulse.getSmoothPulse();
        float alpha = (0.25f + 0.55f * pulse * (0.4f + 0.6f * danger)) * act;

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        float r = 1f;
        float g = 0.15f + 0.25f * danger;
        float b = 0.1f;
        int color = ARGB.colorFromFloat(alpha, r, g, b);

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                VIGNETTE_TEX,
                0, 0,
                0f, 0f,
                width, height,
                width, height,
                color
        );
    }

    public static void renderWarningIcon(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!BloodVignetteConfig.enableIcon) return;

        float act = BloodVignettePulse.getActivation();
        if (act <= 0.001f) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        float pulse = BloodVignettePulse.getSmoothPulse();
        float scale = 0.85f + 0.25f * pulse;
        float alpha = BloodVignetteConfig.iconOpacity * (0.55f + 0.45f * pulse) * act;

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        int iconSize = BloodVignetteConfig.iconSize;
        float x = width * BloodVignetteConfig.iconPosX - iconSize / 2f;
        float y = height * BloodVignetteConfig.iconPosY - iconSize / 2f;

        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x + iconSize / 2f, y + iconSize / 2f);
        pose.scale(scale, scale);
        pose.translate(-iconSize / 2f, -iconSize / 2f);

        int color = ARGB.colorFromFloat(alpha, 1f, 1f, 1f);

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                WARNING_ICON_TEX,
                0, 0,
                0f, 0f,
                iconSize, iconSize,
                iconSize, iconSize,
                color
        );

        pose.popMatrix();
    }
}
