package com.evga314.bloodvignette.client;

import com.evga314.bloodvignette.config.BloodVignetteConfig;
import com.evga314.bloodvignette.sound.BloodVignetteSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

/**
 * Heartbeat pulse + configurable fade in/out for Blood Vignette (Evga314).
 */
public final class BloodVignettePulse {

    private static int ticksUntilBeat = 0;
    private static float intensity = 0f;
    private static float activation = 0f;

    private BloodVignettePulse() {}

    public static void tick(Minecraft client) {
        if (client.player == null) return;
        if (client.isPaused()) return;

        float health = client.player.getHealth();
        boolean low = health <= BloodVignetteConfig.healthThreshold && health > 0
                && (BloodVignetteConfig.enableVignette || BloodVignetteConfig.enableIcon);

        // Convert ms → progress per tick (20 TPS). 0 ms = instant.
        float fadeInStep = BloodVignetteConfig.fadeInMs <= 0
                ? 1f
                : 50f / BloodVignetteConfig.fadeInMs;   // 1000/20 = 50 ms per tick
        float fadeOutStep = BloodVignetteConfig.fadeOutMs <= 0
                ? 1f
                : 50f / BloodVignetteConfig.fadeOutMs;

        if (low) {
            activation = Mth.clamp(activation + fadeInStep, 0f, 1f);
        } else {
            activation = Mth.clamp(activation - fadeOutStep, 0f, 1f);
            intensity = Mth.lerp(0.15f, intensity, 0f);
            ticksUntilBeat = 0;
            return;
        }

        intensity = Mth.lerp(0.18f, intensity, 0f);

        if (ticksUntilBeat <= 0) {
            playHeartbeat(client);
            intensity = 1f;
            ticksUntilBeat = 18 + (int) (Mth.clamp(health, 1f, 20f) * 0.7f);
        } else {
            ticksUntilBeat--;
        }
    }

    private static void playHeartbeat(Minecraft client) {
        if (client.player == null || client.isPaused()) return;

        float vol = BloodVignetteConfig.heartbeatVolume * activation;
        if (vol <= 0.02f) return;

        client.getSoundManager().play(
                new SimpleSoundInstance(
                        BloodVignetteSounds.HEARTBEAT,
                        SoundSource.PLAYERS,
                        vol,
                        0.97f + client.player.getRandom().nextFloat() * 0.06f,
                        client.player.getRandom(),
                        client.player.getX(),
                        client.player.getY(),
                        client.player.getZ()
                )
        );
    }

    public static float getActivation() {
        return activation;
    }

    public static float getIntensity() {
        return intensity * BloodVignetteConfig.vignettePulseStrength;
    }

    public static float getSmoothPulse() {
        return 0.30f + 0.70f * getIntensity();
    }
}
