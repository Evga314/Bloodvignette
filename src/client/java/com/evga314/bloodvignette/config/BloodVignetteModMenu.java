package com.evga314.bloodvignette.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Mod Menu integration for Blood Vignette by Evga314.
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

        general.addEntry(entry.startFloatField(
                        Component.translatable("config.bloodvignette.heartbeat_volume"),
                        BloodVignetteConfig.heartbeatVolume)
                .setDefaultValue(0.55f)
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

        general.addEntry(entry.startIntSlider(
                        Component.translatable("config.bloodvignette.fade_in_ms"),
                        BloodVignetteConfig.fadeInMs,
                        0, 2000)
                .setDefaultValue(750)
                .setTooltip(Component.translatable("config.bloodvignette.fade_in_ms.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.fadeInMs = v)
                .build());

        general.addEntry(entry.startIntSlider(
                        Component.translatable("config.bloodvignette.fade_out_ms"),
                        BloodVignetteConfig.fadeOutMs,
                        0, 2000)
                .setDefaultValue(750)
                .setTooltip(Component.translatable("config.bloodvignette.fade_out_ms.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.fadeOutMs = v)
                .build());

        icon.addEntry(entry.startFloatField(
                        Component.translatable("config.bloodvignette.icon_opacity"),
                        BloodVignetteConfig.iconOpacity)
                .setDefaultValue(0.85f)
                .setMin(0f).setMax(1f)
                .setTooltip(Component.translatable("config.bloodvignette.icon_opacity.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.iconOpacity = v)
                .build());

        icon.addEntry(entry.startIntSlider(
                        Component.translatable("config.bloodvignette.icon_size"),
                        BloodVignetteConfig.iconSize,
                        16, 128)
                .setDefaultValue(48)
                .setTooltip(Component.translatable("config.bloodvignette.icon_size.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.iconSize = v)
                .build());

        icon.addEntry(entry.startFloatField(
                        Component.translatable("config.bloodvignette.icon_pos_x"),
                        BloodVignetteConfig.iconPosX)
                .setDefaultValue(0.5f)
                .setMin(0f).setMax(1f)
                .setTooltip(Component.translatable("config.bloodvignette.icon_pos_x.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.iconPosX = v)
                .build());

        icon.addEntry(entry.startFloatField(
                        Component.translatable("config.bloodvignette.icon_pos_y"),
                        BloodVignetteConfig.iconPosY)
                .setDefaultValue(0.18f)
                .setMin(0f).setMax(1f)
                .setTooltip(Component.translatable("config.bloodvignette.icon_pos_y.tooltip"))
                .setSaveConsumer(v -> BloodVignetteConfig.iconPosY = v)
                .build());

        return builder.build();
    }
}
