package com.evga314.bloodvignette.config;

import com.evga314.bloodvignette.BloodVignetteMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Client config for Blood Vignette by Evga314.
 * Icon position uses a 0.0–1000.0 field (decimals allowed).
 */
public final class BloodVignetteConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("bloodvignette.json");

    public static boolean enableVignette = true;
    public static boolean enableIcon = true;
    public static int healthThreshold = 6;
    public static boolean enableHeartbeat = true;
    public static float heartbeatVolume = 0.40f;
    public static float vignettePulseStrength = 0.85f;
    /** Overall vignette darkness 0–1. */
    public static float vignetteOpacity = 1.0f;
    public static float iconOpacity = 0.85f;
    public static int iconSize = 48;

    /** Horizontal icon position 0.0–1000.0 (500 = center). Supports decimals. */
    public static float iconPosX = 500f;
    /** Vertical icon position 0.0–1000.0 (0 = top, 1000 = bottom). Supports decimals. */
    public static float iconPosY = 180f;

    public static int fadeInMs = 750;
    public static int fadeOutMs = 750;

    private BloodVignetteConfig() {}

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                Data data = GSON.fromJson(json, Data.class);
                if (data != null) {
                    enableVignette = data.enableVignette;
                    enableIcon = data.enableIcon;
                    enableHeartbeat = data.enableHeartbeat;
                    healthThreshold = Math.max(1, Math.min(20, data.healthThreshold));
                    heartbeatVolume = clamp(data.heartbeatVolume, 0f, 1f);
                    vignettePulseStrength = clamp(data.vignettePulseStrength, 0f, 1f);
                    vignetteOpacity = clamp(data.vignetteOpacity, 0f, 1f);
                    iconOpacity = clamp(data.iconOpacity, 0f, 1f);
                    iconSize = Math.max(16, Math.min(128, data.iconSize));
                    fadeInMs = Math.max(0, Math.min(2000, data.fadeInMs));
                    fadeOutMs = Math.max(0, Math.min(2000, data.fadeOutMs));

                    float x = data.iconPosX;
                    float y = data.iconPosY;
                    // Migrate old 0–1 normalized coords → 0–1000
                    if (x >= 0f && x <= 1.0001f && y >= 0f && y <= 1.0001f) {
                        x *= 1000f;
                        y *= 1000f;
                    }
                    // Migrate previous 0–100 field → 0–1000
                    else if (x >= 0f && x <= 100.001f && y >= 0f && y <= 100.001f) {
                        x *= 10f;
                        y *= 10f;
                    }
                    iconPosX = clamp(x, 0f, 1000f);
                    iconPosY = clamp(y, 0f, 1000f);
                }
            } catch (Exception e) {
                BloodVignetteMod.LOGGER.warn("Failed to load bloodvignette.json, using defaults", e);
            }
        }
        save();
    }

    public static void save() {
        Data data = new Data();
        data.enableVignette = enableVignette;
        data.enableIcon = enableIcon;
        data.enableHeartbeat = enableHeartbeat;
        data.healthThreshold = healthThreshold;
        data.heartbeatVolume = heartbeatVolume;
        data.vignettePulseStrength = vignettePulseStrength;
        data.vignetteOpacity = vignetteOpacity;
        data.iconOpacity = iconOpacity;
        data.iconSize = iconSize;
        data.iconPosX = iconPosX;
        data.iconPosY = iconPosY;
        data.fadeInMs = fadeInMs;
        data.fadeOutMs = fadeOutMs;
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            BloodVignetteMod.LOGGER.error("Could not save bloodvignette.json", e);
        }
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    public static class Data {
        public boolean enableVignette = true;
        public boolean enableIcon = true;
        public int healthThreshold = 6;
        public boolean enableHeartbeat = true;
        public float heartbeatVolume = 0.40f;
        public float vignettePulseStrength = 0.85f;
        public float vignetteOpacity = 1.0f;
        public float iconOpacity = 0.85f;
        public int iconSize = 48;
        public float iconPosX = 500f;
        public float iconPosY = 180f;
        public int fadeInMs = 750;
        public int fadeOutMs = 750;
    }
}
