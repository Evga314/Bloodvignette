package com.evga314.bloodvignette.client;

import com.evga314.bloodvignette.BloodVignetteMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

/**
 * Client-side entrypoint for Blood Vignette by Evga314.
 * Registers HUD elements and tick logic for pulse + heartbeat.
 */
public class BloodVignetteClient implements ClientModInitializer {

    public static final Identifier VIGNETTE_LAYER = BloodVignetteMod.id("blood_vignette_layer");
    public static final Identifier ICON_LAYER = BloodVignetteMod.id("blood_warning_icon_layer");

    @Override
    public void onInitializeClient() {
        BloodVignetteMod.LOGGER.info("Blood Vignette client init by Evga314");

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                VIGNETTE_LAYER,
                BloodVignetteHud::renderVignette
        );

        HudElementRegistry.attachElementAfter(
                VIGNETTE_LAYER,
                ICON_LAYER,
                BloodVignetteHud::renderWarningIcon
        );

        // Tick only when not paused — pulse + heartbeat
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;
            if (client.isPaused()) return;
            BloodVignettePulse.tick(client);
        });
    }
}
