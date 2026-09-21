package com.evga314.bloodvignette;

import com.evga314.bloodvignette.config.BloodVignetteConfig;
import com.evga314.bloodvignette.sound.BloodVignetteSounds;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entrypoint for Blood Vignette mod by Evga314.
 * Handles common (client+server) initialization.
 */
public class BloodVignetteMod implements ModInitializer {

    public static final String MOD_ID = "bloodvignette";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Blood Vignette by Evga314 is waking up...");
        BloodVignetteConfig.load();
        BloodVignetteSounds.register();
        LOGGER.info("Blood Vignette initialized. Low-health threshold: {}", BloodVignetteConfig.healthThreshold);
    }

    /** Helper to create identifiers under the bloodvignette namespace. */
    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
