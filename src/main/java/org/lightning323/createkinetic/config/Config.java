package org.lightning323.createkinetic.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.DoubleValue GYROSCOPE_OMEGA_TARGET;
    private static final ModConfigSpec.DoubleValue GYROSCOPE_DAMPING_RATIO;
    private static final ModConfigSpec.DoubleValue GYROSCOPE_AUTHORITY_PER_UNIT;
    private static final ModConfigSpec.DoubleValue GYROSCOPE_FEED_FORWARD_GAIN;
    private static final ModConfigSpec.DoubleValue GYROSCOPE_FEED_FORWARD_SMOOTHING;
    private static final ModConfigSpec.DoubleValue GYROSCOPE_REFERENCE_RPM;
    private static final ModConfigSpec.DoubleValue GYROSCOPE_STRESS_IMPACT;
    private static final ModConfigSpec.DoubleValue JOYSTICK_PIXELS_PER_STEP;
    private static final ModConfigSpec.IntValue JOYSTICK_KEY_REPEAT_DELAY_MS;
    private static final ModConfigSpec.IntValue JOYSTICK_SPRING_BACK_DELAY_MS;

    public static final ModConfigSpec.BooleanValue ENABLE_RENDER_TUNING_CHEATS;


    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec CLIENT_SPEC;

    private static volatile double gyroscopeOmegaTarget;
    private static volatile double gyroscopeDampingRatio;
    private static volatile double gyroscopeAuthorityPerUnit;
    private static volatile double gyroscopeFeedForwardGain;
    private static volatile double gyroscopeFeedForwardSmoothing;
    private static volatile double gyroscopeReferenceRpm;
    private static volatile double gyroscopeStressImpact;
    private static volatile double joystickPixelsPerStep;
    private static volatile int joystickKeyRepeatDelayMs;
    private static volatile int joystickSpringBackDelayMs;
    private static volatile boolean renderTuningCheatsEnabled;

    private Config() {
    }

    public static double gyroscopeOmegaTarget() {
        return gyroscopeOmegaTarget;
    }

    public static double gyroscopeDampingRatio() {
        return gyroscopeDampingRatio;
    }

    public static double gyroscopeAuthorityPerUnit() {
        return gyroscopeAuthorityPerUnit;
    }

    public static double gyroscopeFeedForwardGain() {
        return gyroscopeFeedForwardGain;
    }

    public static double gyroscopeFeedForwardSmoothing() {
        return gyroscopeFeedForwardSmoothing;
    }

    public static double gyroscopeReferenceRpm() {
        return gyroscopeReferenceRpm;
    }

    public static double gyroscopeStressImpact() {
        return gyroscopeStressImpact;
    }

    public static double joystickPixelsPerStep() {
        return joystickPixelsPerStep;
    }

    public static int joystickKeyRepeatDelayMs() {
        return joystickKeyRepeatDelayMs;
    }

    public static int joystickSpringBackDelayMs() {
        return joystickSpringBackDelayMs;
    }

    public static boolean renderTuningCheatsEnabled() {
        return renderTuningCheatsEnabled;
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        IConfigSpec spec = event.getConfig().getSpec();
        if (spec == SPEC) {
            gyroscopeOmegaTarget = (Double) GYROSCOPE_OMEGA_TARGET.get();
            gyroscopeDampingRatio = (Double) GYROSCOPE_DAMPING_RATIO.get();
            gyroscopeAuthorityPerUnit = (Double) GYROSCOPE_AUTHORITY_PER_UNIT.get();
            gyroscopeFeedForwardGain = (Double) GYROSCOPE_FEED_FORWARD_GAIN.get();
            gyroscopeFeedForwardSmoothing = (Double) GYROSCOPE_FEED_FORWARD_SMOOTHING.get();
            gyroscopeReferenceRpm = (Double) GYROSCOPE_REFERENCE_RPM.get();
            gyroscopeStressImpact = (Double) GYROSCOPE_STRESS_IMPACT.get();
            joystickPixelsPerStep = (Double) JOYSTICK_PIXELS_PER_STEP.get();
            renderTuningCheatsEnabled = (Boolean) ENABLE_RENDER_TUNING_CHEATS.get();

        } else if (spec == CLIENT_SPEC) {
            joystickKeyRepeatDelayMs = (Integer) JOYSTICK_KEY_REPEAT_DELAY_MS.get();
            joystickSpringBackDelayMs = (Integer) JOYSTICK_SPRING_BACK_DELAY_MS.get();
        }

    }

    static {
        //Common
        //Gyro
        BUILDER.comment("Settings for the Gyroscope block.")
                .push("gyroscope");
        GYROSCOPE_OMEGA_TARGET = BUILDER.comment("Target natural frequency of the closed loop in rad/s. Higher = snappier correction. 3.0 rad/s gives ~2 second natural period. Goes up to ~10 before discretization at 80 Hz starts to bite.").defineInRange("omegaTarget", (double) 3.0F, 0.1, (double) 10.0F);
        GYROSCOPE_DAMPING_RATIO = BUILDER.comment("Target damping ratio of the closed loop. 0.9 settles fast with negligible overshoot, 0.7 is faster to first peak but bounces ~5%, 1.0 is critically damped (no overshoot, slower).").defineInRange("dampingRatio", 0.9, 0.1, (double) 2.0F);
        GYROSCOPE_AUTHORITY_PER_UNIT = BUILDER.comment("How much ship inertia (kg*m^2) one gyro fully stabilizes at reference RPM. Bigger ship needs more gyros, ratio determines how many. Under-powered fleets degrade gracefully (slower correction, still stable).").defineInRange("authorityPerUnit", (double) 5000.0F, (double) 100.0F, (double) 1000000.0F);
        GYROSCOPE_FEED_FORWARD_GAIN = BUILDER.comment("Fraction of the observed external disturbance the controller cancels via feed-forward. 1.0 = full cancellation (firmest hold on unbalanced ships), 0.0 = disable feed-forward, leaving only the PD loop. Lower if the controller feels too aggressive on heavily unbalanced contraptions.").defineInRange("feedForwardGain", (double) 1.0F, (double) 0.0F, (double) 1.0F);
        GYROSCOPE_FEED_FORWARD_SMOOTHING = BUILDER.comment("Exponential moving average factor for the disturbance estimate. Lower = smoother but slower to track changes, higher = more responsive but noisier. 0.2 (default) corresponds to a ~5-substep time constant at 80 Hz.").defineInRange("feedForwardSmoothing", 0.2, 0.01, (double) 1.0F);
        GYROSCOPE_REFERENCE_RPM = BUILDER.comment("RPM at which the gyroscope reaches 100% effectiveness. Above this, output is capped.").defineInRange("referenceRpm", (double) 256.0F, (double) 1.0F, (double) 4096.0F);
        GYROSCOPE_STRESS_IMPACT = BUILDER.comment("Base stress impact in SU per RPM. Total SU draw is roughly impact * |RPM|.").defineInRange("stressImpact", (double) 16.0F, (double) 0.0F, (double) 1024.0F);
        BUILDER.pop();

        //Joystick
        BUILDER.comment("Settings for the Joystick block.")
                .push("joystick");
        JOYSTICK_PIXELS_PER_STEP = BUILDER.comment("Raw mouse pixels per tilt step. Lower = more sensitive. Full deflection (15 steps = 45 deg) is reached after 15x this many pixels of mouse movement.").defineInRange("pixelsPerStep", (double) 30.0F, (double) 1.0F, (double) 500.0F);
        BUILDER.pop();

        //Tracks
        ENABLE_RENDER_TUNING_CHEATS = BUILDER.comment("Allows operators to open the in-game tracks render tuning menu with J. Disabled by default.").define("enableRenderTuningCheats", false);

        //Client
        CLIENT_BUILDER.comment("Client-only joystick feel settings (input pacing).").push("joystick");
        JOYSTICK_KEY_REPEAT_DELAY_MS = CLIENT_BUILDER.comment("Milliseconds between repeat tilt steps while a direction key is held. Lower = full deflection reached faster (snappier); higher = slower sweep.").defineInRange("keyRepeatDelayMs", 100, 10, 2000);
        JOYSTICK_SPRING_BACK_DELAY_MS = CLIENT_BUILDER.comment("Grace window after the last direction-key press/release before spring-back starts decaying. Lets you tap a key repeatedly without fighting the spring between taps. Set to 0 to spring back immediately.").defineInRange("springBackDelayMs", 300, 0, 5000);
        CLIENT_BUILDER.pop();
        SPEC = BUILDER.build();
        CLIENT_SPEC = CLIENT_BUILDER.build();
    }


}
