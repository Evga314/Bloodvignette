package com.evga314.bloodvignette.sound;

import com.evga314.bloodvignette.BloodVignetteMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

/**
 * Custom sounds registered by Blood Vignette (Evga314).
 */
public class BloodVignetteSounds {

    public static final Identifier HEARTBEAT_ID = BloodVignetteMod.id("heartbeat");
    public static SoundEvent HEARTBEAT;

    public static void register() {
        HEARTBEAT = SoundEvent.createVariableRangeEvent(HEARTBEAT_ID);
        Registry.register(BuiltInRegistries.SOUND_EVENT, HEARTBEAT_ID, HEARTBEAT);
        BloodVignetteMod.LOGGER.info("Registered Blood Vignette heartbeat sound");
    }
}
