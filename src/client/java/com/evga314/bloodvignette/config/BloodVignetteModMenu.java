package com.evga314.bloodvignette.config;

import com.evga314.bloodvignette.client.IconPositionScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Mod Menu integration for Blood Vignette by Evga314.
 * Icon size & position are edited in the live IconPositionScreen.
 */
public class BloodVignetteModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return BloodVignetteModMenu::createConfigScreen;
    }

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.bloodvignette.title"))
                .setSavingRunnable(BloodVignetteConfig::save);

        ConfigEntryBuilder entry = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.bloodvignette.category.general"));
        ConfigCategory icon = builder.getOrCreateCategory(Component.translatable("config.bloodvignette.category.icon"));

        general.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.bloodvignette.enable_vignette"),
                        BloodVignetteConfig.enableVignette)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.bloodvignette.enable_vignette.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.enableVignette = v)
                .build());

        general.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.bloodvignette.enable_icon"),
                        BloodVignetteConfig.enableIcon)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.bloodvignette.enable_icon.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.enableIcon = v)
                .build());

        general.addEntry(entry.startIntSlider(
                        Component.translatable("config.bloodvignette.health_threshold"),
                        BloodVignetteConfig.healthThreshold,
                        1, 20)
                .setDefaultValue(6)
                .setTooltip(Component.translatable("config.bloodvignette.health_threshold.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.healthThreshold = v)
                .build());

        general.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.bloodvignette.enable_heartbeat"),
                        BloodVignetteConfig.enableHeartbeat)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.bloodvignette.enable_heartbeat.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.enableHeartbeat = v)
                .build());

        general.addEntry(entry.startFloatField(
                        Component.translatable("config.bloodvignette.heartbeat_volume"),
                        BloodVignetteConfig.heartbeatVolume)
                .setDefaultValue(0.40f)
                .setMin(0f).setMax(1f)
                .setTooltip(Component.translatable("config.bloodvignette.heartbeat_volume.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.heartbeatVolume = v)
                .build());

        general.addEntry(entry.startFloatField(
                        Component.translatable("config.bloodvignette.pulse_strength"),
                        BloodVignetteConfig.vignettePulseStrength)
                .setDefaultValue(0.85f)
                .setMin(0f).setMax(1f)
                .setTooltip(Component.translatable("config.bloodvignette.pulse_strength.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.vignettePulseStrength = v)
                .build());

        general.addEntry(entry.startFloatField(
                        Component.translatable("config.bloodvignette.vignette_opacity"),
                        BloodVignetteConfig.vignetteOpacity)
                .setDefaultValue(1.0f)
                .setMin(0f).setMax(1f)
                .setTooltip(Component.translatable("config.bloodvignette.vignette_opacity.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.vignetteOpacity = v)
                .build());

        general.addEntry(entry.startIntField(
                        Component.translatable("config.bloodvignette.fade_in_ms"),
                        BloodVignetteConfig.fadeInMs)
                .setDefaultValue(750)
                .setMin(0).setMax(2000)
                .setTooltip(Component.translatable("config.bloodvignette.fade_in_ms.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.fadeInMs = Math.max(0, Math.min(2000, v)))
                .build());

        general.addEntry(entry.startIntField(
                        Component.translatable("config.bloodvignette.fade_out_ms"),
                        BloodVignetteConfig.fadeOutMs)
                .setDefaultValue(750)
                .setMin(0).setMax(2000)
                .setTooltip(Component.translatable("config.bloodvignette.fade_out_ms.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.fadeOutMs = Math.max(0, Math.min(2000, v)))
                .build());

        icon.addEntry(entry.startTextDescription(
                Component.translatable("config.bloodvignette.position_editor.description")
        ).build());

        // Enable → press «Save & Quit» → opens live editor
        icon.addEntry(entry.startBooleanToggle(
                        Component.translatable("config.bloodvignette.position_editor.open"),
                        false)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.bloodvignette.position_editor.open.tooltip"))
                .setSaveConsumer(v -> {
                    if (v) {
                        Minecraft mc = Minecraft.getInstance();
                        mc.execute(() -> mc.gui.setScreen(new IconPositionScreen(parent)));
                    }
                })
                .build());

        return builder.build();
    }
}
