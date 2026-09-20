package com.evga314.bloodvignette.config;

import com.evga314.bloodvignette.BloodVignetteMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration for Blood Vignette by Evga314.
 */
public class BloodVignetteConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("bloodvignette.json");

    public static boolean enableVignette = true;
    public static boolean enableIcon = true;
    public static int healthThreshold = 6;
    public static float heartbeatVolume = 0.55f;
    public static float vignettePulseStrength = 0.85f;
    public static float iconOpacity = 0.85f;
    public static int iconSize = 48;
    public static float iconPosX = 0.5f;
    public static float iconPosY = 0.18f;

    /** Fade-in duration in milliseconds (0–2000). Default 750. */
    public static int fadeInMs = 750;

    /** Fade-out duration in milliseconds (0–2000). Default 750. */
    public static int fadeOutMs = 750;

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                Data data = GSON.fromJson(json, Data.class);
                if (data != null) {
                    enableVignette = data.enableVignette;
                    enableIcon = data.enableIcon;
                    healthThreshold = Math.max(1, Math.min(20, data.healthThreshold));
                    heartbeatVolume = clamp(data.heartbeatVolume, 0f, 1f);
                    vignettePulseStrength = clamp(data.vignettePulseStrength, 0f, 1f);
                    iconOpacity = clamp(data.iconOpacity, 0f, 1f);
                    iconSize = Math.max(16, Math.min(128, data.iconSize));
                    iconPosX = clamp(data.iconPosX, 0f, 1f);
                    iconPosY = clamp(data.iconPosY, 0f, 1f);
                    fadeInMs = Math.max(0, Math.min(2000, data.fadeInMs));
                    fadeOutMs = Math.max(0, Math.min(2000, data.fadeOutMs));
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
        data.healthThreshold = healthThreshold;
        data.heartbeatVolume = heartbeatVolume;
        data.vignettePulseStrength = vignettePulseStrength;
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
        public float heartbeatVolume = 0.55f;
        public float vignettePulseStrength = 0.85f;
        public float iconOpacity = 0.85f;
        public int iconSize = 48;
        public float iconPosX = 0.5f;
        public float iconPosY = 0.18f;
        public int fadeInMs = 750;
        public int fadeOutMs = 750;
    }
}
