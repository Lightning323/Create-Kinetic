package org.lightning323.createkinetic.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.lightning323.createkinetic.registries.PropulsionDefaultStress;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KineticConfig {
    public static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec CLIENT_SPEC;


    // ── Thruster (reference-style typed values for new code) ────────────────
    public static final ModConfigSpec.DoubleValue BASE_THRUST;
    public static final ModConfigSpec.IntValue OBSTRUCTION_SCAN_LENGTH;
    public static final ModConfigSpec.BooleanValue OBSTRUCTION_IGNORE_OTHER_SUBLEVELS;
    public static final ModConfigSpec.IntValue FUEL_TANK_CAPACITY_MB;

    public static final ModConfigSpec.DoubleValue CREATIVE_THRUSTER_BASE_THRUST;
    public static final ModConfigSpec.DoubleValue CREATIVE_THRUSTER_MAX_THRUST;
    public static final ModConfigSpec.DoubleValue CREATIVE_THRUSTER_MULTIBLOCK_2X2X2_MAX_THRUST;
    public static final ModConfigSpec.DoubleValue CREATIVE_THRUSTER_MULTIBLOCK_3X3X3_MAX_THRUST;
    public static final ModConfigSpec.DoubleValue CREATIVE_VECTOR_THRUSTER_BASE_THRUST;
    public static final ModConfigSpec.DoubleValue CREATIVE_VECTOR_THRUSTER_MAX_THRUST;
    public static final ModConfigSpec.DoubleValue FUEL_MB_PER_TICK_AT_FULL_THROTTLE;

    public static final ModConfigSpec.IntValue ION_THRUSTER_ENERGY_CAPACITY_FE;
    public static final ModConfigSpec.DoubleValue ION_THRUSTER_FE_PER_TICK_AT_FULL_THROTTLE;
    public static final ModConfigSpec.DoubleValue ION_THRUSTER_BASE_THRUST;
    public static final ModConfigSpec.DoubleValue ION_MULTIBLOCK_2X_THRUST_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ION_MULTIBLOCK_3X_THRUST_MULTIPLIER;

    public static final ModConfigSpec.DoubleValue VECTOR_THRUSTER_BASE_THRUST;
    public static final ModConfigSpec.DoubleValue LIQUID_VECTOR_THRUSTER_BASE_THRUST;
    public static final ModConfigSpec.IntValue LIQUID_VECTOR_THRUSTER_FUEL_TANK_CAPACITY_MB;
    public static final ModConfigSpec.DoubleValue LIQUID_VECTOR_THRUSTER_FUEL_MB_PER_TICK_AT_FULL_THROTTLE;

    public static final ModConfigSpec.DoubleValue MULTIBLOCK_2X_THRUST_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue MULTIBLOCK_3X_THRUST_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue MULTIBLOCK_2X_FUEL_EFFICIENCY;
    public static final ModConfigSpec.DoubleValue MULTIBLOCK_3X_FUEL_EFFICIENCY;
    public static final ModConfigSpec.DoubleValue MULTIBLOCK_2X_OXIDIZER_EFFICIENCY;
    public static final ModConfigSpec.DoubleValue MULTIBLOCK_3X_OXIDIZER_EFFICIENCY;
    public static final ModConfigSpec.BooleanValue DAMAGE_ENTITIES;
    public static final ModConfigSpec.DoubleValue NOZZLE_OFFSET_FROM_CENTER;
    public static final ModConfigSpec.BooleanValue USE_ATMOSPHERIC_PRESSURE;
    public static final ModConfigSpec.DoubleValue ATMOSPHERIC_PRESSURE_AMOUNT;
    public static final ModConfigSpec.DoubleValue THRUST_UNITS_PER_KN;
    public static final ModConfigSpec.IntValue CLIENT_PARTICLES_PER_TICK;
    public static final Map<String, ModConfigSpec.IntValue> FUEL_EFFICIENCY_ENTRIES = new LinkedHashMap<>();
    public static final Map<String, ModConfigSpec.IntValue> FUEL_BURN_RATE_ENTRIES = new LinkedHashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<String>> THRUSTER_DYE_COLORS = new LinkedHashMap<>();
    public static final ModConfigSpec.BooleanValue ENABLE_RENDER_TUNING_CHEATS;

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
    private static final ModConfigSpec.BooleanValue JOYSTICK_HUD_SHOW_READOUT;
    private static final ModConfigSpec.BooleanValue JOYSTICK_HUD_SHOW_LINES;

    private static volatile double gyroscopeOmegaTarget;
    private static volatile double gyroscopeDampingRatio;
    private static volatile double gyroscopeAuthorityPerUnit;
    private static volatile double gyroscopeFeedForwardGain;
    private static volatile double gyroscopeFeedForwardSmoothing;
    private static volatile double gyroscopeReferenceRpm;
    private static volatile double gyroscopeStressImpact;
    private static volatile double joystickPixelsPerStep;
    private static volatile boolean joystickShowReadout;
    private static volatile boolean joystickShowLines;
    private static volatile int joystickKeyRepeatDelayMs;
    private static volatile int joystickSpringBackDelayMs;
    private static volatile boolean renderTuningCheatsEnabled;


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

    public static double reactionWheelStressImpact() {
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


    public static boolean joystickShowReadout() {
        return joystickShowReadout;
    }

    public static boolean joystickShowLines() {
        return joystickShowLines;
    }

    public static boolean renderTuningCheatsEnabled() {
        return renderTuningCheatsEnabled;
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        if (event instanceof ModConfigEvent.Loading) {
            IConfigSpec spec = event.getConfig().getSpec();
            if (spec == COMMON_SPEC) {
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
                joystickShowReadout = (Boolean) JOYSTICK_HUD_SHOW_READOUT.get();
                joystickShowLines = (Boolean) JOYSTICK_HUD_SHOW_LINES.get();
            }
        }
    }

    public enum ThrusterPlumeType {
        PARTICLES,
        SPRITE_MESH,
        SPRITE_MESH_SINGLE_MULTIBLOCK
    }

    //flame config options
    public static final ModConfigSpec.EnumValue<ThrusterPlumeType> THRUSTER_PLUME_TYPE;
    public static final ModConfigSpec.EnumValue<ThrusterPlumeType> CREATIVE_THRUSTER_PLUME_TYPE;
    public static final ModConfigSpec.EnumValue<ThrusterPlumeType> ION_THRUSTER_PLUME_TYPE;
    public static final ModConfigSpec.EnumValue<ThrusterPlumeType> VECTOR_THRUSTERS_PLUME_TYPE;

    public static final ModConfigSpec.BooleanValue DEBUG_THRUSTER;

    /**
     * Extra fuel lines {@code fluid=efficiency,burnRate}; merged after defaults; duplicates override.
     */
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ADDITIONAL_THRUSTER_FUEL_PROPERTY_LINES;

    /**
     * CLIENT BUILDER
     */
    static {

        //Joystick
        CLIENT_BUILDER.comment("Joystick settings.").push("Joystick");
        JOYSTICK_HUD_SHOW_READOUT = CLIENT_BUILDER.comment("Show readout in the Joystick HUD").define("showReadout", false);
        JOYSTICK_HUD_SHOW_LINES = CLIENT_BUILDER.comment("Show lines in the Joystick HUD").define("showLines", false);
        JOYSTICK_KEY_REPEAT_DELAY_MS = CLIENT_BUILDER.comment("Milliseconds between repeat tilt steps while a direction key is held. Lower = full deflection reached faster (snappier); higher = slower sweep.").defineInRange("keyRepeatDelayMs", 100, 10, 2000);
        JOYSTICK_SPRING_BACK_DELAY_MS = CLIENT_BUILDER.comment("Grace window after the last direction-key press/release before spring-back starts decaying. Lets you tap a key repeatedly without fighting the spring between taps. Set to 0 to spring back immediately.").defineInRange("springBackDelayMs", 300, 0, 5000);
        CLIENT_BUILDER.pop();

        //Thrusters
        CLIENT_BUILDER.push("Thruster");

        CLIENT_BUILDER.push("Thruster Render Types");
        CLIENT_BUILDER.comment("How the thruster plume should be rendered.");
        THRUSTER_PLUME_TYPE = CLIENT_BUILDER.defineEnum("Thruster Plume Type", ThrusterPlumeType.PARTICLES);
        CREATIVE_THRUSTER_PLUME_TYPE = CLIENT_BUILDER.defineEnum("Creative Thruster Plume Type", ThrusterPlumeType.PARTICLES);
        ION_THRUSTER_PLUME_TYPE = CLIENT_BUILDER.defineEnum("Ion Thruster Plume Type", ThrusterPlumeType.PARTICLES);
        VECTOR_THRUSTERS_PLUME_TYPE = CLIENT_BUILDER.defineEnum("Vector Thrusters Plume Type", ThrusterPlumeType.PARTICLES);
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.push("Debug");
        DEBUG_THRUSTER = CLIENT_BUILDER.comment("Render thruster debug overlays (plume ray, obstruction hits, damage zones).")
                .define("Thruster", false);
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.pop();
    }

    /**
     * COMMON BUILDER
     */
    static {

        //Joystick
        COMMON_BUILDER.push("Joystick");
        JOYSTICK_PIXELS_PER_STEP = COMMON_BUILDER.comment("Raw mouse pixels per tilt step. Lower = more sensitive. Full deflection (15 steps = 45 deg) is reached after 15x this many pixels of mouse movement.").defineInRange("pixelsPerStep", (double) 30.0F, (double) 1.0F, (double) 500.0F);
        COMMON_BUILDER.pop();

        //Tracks
        COMMON_BUILDER.comment("Track settings").push("tracks");
        ENABLE_RENDER_TUNING_CHEATS = COMMON_BUILDER.comment("Allows operators to open the in-game tracks render tuning menu with J. Disabled by default.").define("enableRenderTuningCheats", false);
        COMMON_BUILDER.pop();

        //Gyro
        COMMON_BUILDER.comment("Settings for the Gyroscope block.").push("gyroscope");
        GYROSCOPE_OMEGA_TARGET = COMMON_BUILDER.comment("Target natural frequency of the closed loop in rad/s. Higher = snappier correction. 3.0 rad/s gives ~2 second natural period. Goes up to ~10 before discretization at 80 Hz starts to bite.").defineInRange("omegaTarget", (double) 3.0F, 0.1, (double) 10.0F);
        GYROSCOPE_DAMPING_RATIO = COMMON_BUILDER.comment("Target damping ratio of the closed loop. 0.9 settles fast with negligible overshoot, 0.7 is faster to first peak but bounces ~5%, 1.0 is critically damped (no overshoot, slower).").defineInRange("dampingRatio", 0.9, 0.1, (double) 2.0F);
        GYROSCOPE_AUTHORITY_PER_UNIT = COMMON_BUILDER.comment("How much ship inertia (kg*m^2) one gyro fully stabilizes at reference RPM. Bigger ship needs more gyros, ratio determines how many. Under-powered fleets degrade gracefully (slower correction, still stable).").defineInRange("authorityPerUnit", (double) 5000.0F, (double) 100.0F, (double) 1000000.0F);
        GYROSCOPE_FEED_FORWARD_GAIN = COMMON_BUILDER.comment("Fraction of the observed external disturbance the controller cancels via feed-forward. 1.0 = full cancellation (firmest hold on unbalanced ships), 0.0 = disable feed-forward, leaving only the PD loop. Lower if the controller feels too aggressive on heavily unbalanced contraptions.").defineInRange("feedForwardGain", (double) 1.0F, (double) 0.0F, (double) 1.0F);
        GYROSCOPE_FEED_FORWARD_SMOOTHING = COMMON_BUILDER.comment("Exponential moving average factor for the disturbance estimate. Lower = smoother but slower to track changes, higher = more responsive but noisier. 0.2 (default) corresponds to a ~5-substep time constant at 80 Hz.").defineInRange("feedForwardSmoothing", 0.2, 0.01, (double) 1.0F);
        GYROSCOPE_REFERENCE_RPM = COMMON_BUILDER.comment("RPM at which the gyroscope reaches 100% effectiveness. Above this, output is capped.").defineInRange("referenceRpm", (double) 256.0F, (double) 1.0F, (double) 4096.0F);
        GYROSCOPE_STRESS_IMPACT = COMMON_BUILDER.comment("Base stress impact in SU per RPM. Total SU draw is roughly impact * |RPM|.").defineInRange("stressImpact", (double) 16.0F, (double) 0.0F, (double) 1024.0F);
        COMMON_BUILDER.pop();


        //Thrusters
        COMMON_BUILDER.push("thruster");
        BASE_THRUST = COMMON_BUILDER.comment("Base thrust at redstone 15 and full obstruction efficiency for the standard thruster.",
                        "Default tuned for 1000-unit thrust scale parity with Sable physics.",
                        "Effective thrust uses: baseThrust * fuel_thrust_percent / 100.")
                .defineInRange("baseThrust", 533.333333333d, 1.0d, 10000000.0d);
        OBSTRUCTION_SCAN_LENGTH = COMMON_BUILDER.comment("How many blocks behind the nozzle are checked for obstruction.")
                .defineInRange("obstructionScanLength", 10, 1, 64);
        OBSTRUCTION_IGNORE_OTHER_SUBLEVELS = COMMON_BUILDER.comment(
                        "Ignore non-sublevel blocks when checking for obstruction.")
                .define("obstructionIgnoreOtherSubLevels", true);
        FUEL_TANK_CAPACITY_MB = COMMON_BUILDER.comment("Internal fuel tank capacity in millibuckets.")
                .defineInRange("fuelTankCapacityMb", 1000, 250, 10000000);
        FUEL_MB_PER_TICK_AT_FULL_THROTTLE = COMMON_BUILDER.comment("Fuel consumption in millibuckets per tick at full redstone throttle.")
                .defineInRange("fuelMbPerTickAtFullThrottle", 1.0d, 0.0001d, 1000.0d);
        DAMAGE_ENTITIES = COMMON_BUILDER.comment("If true, entities inside active thruster plume are damaged.")
                .define("damageEntities", true);

        CLIENT_PARTICLES_PER_TICK = COMMON_BUILDER.comment("Max client particles per tick while active.")
                .defineInRange("clientParticlesPerTick", 4, 0, 64);
        COMMON_BUILDER.pop(); // thruster



        COMMON_BUILDER.push("ionThruster");
        ION_THRUSTER_ENERGY_CAPACITY_FE = COMMON_BUILDER.comment("Ion thruster internal FE capacity.")
                .defineInRange("ionThrusterEnergyCapacityFe", 4000, 1, 100000000);
        ION_THRUSTER_FE_PER_TICK_AT_FULL_THROTTLE = COMMON_BUILDER.comment("Ion thruster energy consumption in FE per tick at full redstone throttle.")
                .defineInRange("ionThrusterFePerTickAtFullThrottle", 40.0d, 0.0001d, 1000000.0d);
        ION_THRUSTER_BASE_THRUST = COMMON_BUILDER.comment("Ion thruster base thrust at redstone 15 and full obstruction efficiency.",
                        "Default tuned for 1000-unit thrust scale parity with Sable physics.")
                .defineInRange("ionThrusterBaseThrust", 800.d, 1.d, 10000000.d);
        ION_MULTIBLOCK_2X_THRUST_MULTIPLIER = COMMON_BUILDER.comment("Ion thruster thrust multiplier for 2x2x2 multiblock (1.30 = +30%).")
                .defineInRange("ionMultiblock2xThrustMultiplier", 1.30d, 0.01d, 10.0d);
        ION_MULTIBLOCK_3X_THRUST_MULTIPLIER = COMMON_BUILDER.comment("Ion thruster thrust multiplier for 3x3x3 multiblock (1.40 = +40%).")
                .defineInRange("ionMultiblock3xThrustMultiplier", 1.40d, 0.01d, 10.0d);
        COMMON_BUILDER.pop();



        COMMON_BUILDER.push("Creative Thruster");
        CREATIVE_THRUSTER_BASE_THRUST = COMMON_BUILDER.comment("Starting thrust value (kN) when a creative thruster is placed.",
                        "Default tuned for 1000-unit thrust scale parity with Sable physics.")
                .defineInRange("creativeThrusterBaseThrust", 666.666666667d, 1.0d, 1000000.0d);
        CREATIVE_THRUSTER_MAX_THRUST = COMMON_BUILDER.comment("Maximum thrust (kN) the scroll can reach on a creative thruster.",
                        "Default tuned for 1000-unit thrust scale parity with Sable physics.")
                .defineInRange("creativeThrusterMaxThrust", 10000.0d, 10.0d, 1000000.0d);
        CREATIVE_THRUSTER_MULTIBLOCK_2X2X2_MAX_THRUST = COMMON_BUILDER.comment("Maximum thrust (kN) the scroll can reach on a 2x2x2 creative thruster multiblock.")
                .defineInRange("creativeThrusterMultiblock2x2x2MaxThrust", 100000.0d, 10.0d, 100000000.0d);
        CREATIVE_THRUSTER_MULTIBLOCK_3X3X3_MAX_THRUST = COMMON_BUILDER.comment("Maximum thrust (kN) the scroll can reach on a 3x3x3 creative thruster multiblock.")
                .defineInRange("creativeThrusterMultiblock3x3x3MaxThrust", 5000000.0d, 10.0d, 100000000.0d);
        COMMON_BUILDER.pop();



        COMMON_BUILDER.push("vectorThruster");
        VECTOR_THRUSTER_BASE_THRUST = COMMON_BUILDER.comment("Vector thruster base thrust at redstone 15 and full obstruction efficiency.",
                        "Default tuned for 1000-unit thrust scale parity with Sable physics.")
                .defineInRange("vectorThrusterBaseThrust", 733.333333333d, 1.0d, 10000000.0d);
        COMMON_BUILDER.pop();


        COMMON_BUILDER.push("liquidVectorThruster");
        LIQUID_VECTOR_THRUSTER_BASE_THRUST = COMMON_BUILDER.comment("Liquid vector thruster base thrust at redstone 15 and full obstruction efficiency.",
                        "Default tuned for 1000-unit thrust scale parity with Sable physics.")
                .defineInRange("liquidVectorThrusterBaseThrust", 733.333333333d, 1.0d, 10000000.0d);
        LIQUID_VECTOR_THRUSTER_FUEL_TANK_CAPACITY_MB = COMMON_BUILDER.comment("Liquid vector thruster internal fuel tank capacity in millibuckets.")
                .defineInRange("liquidVectorThrusterFuelTankCapacityMb", 1000, 250, 10000000);
        LIQUID_VECTOR_THRUSTER_FUEL_MB_PER_TICK_AT_FULL_THROTTLE = COMMON_BUILDER.comment("Liquid vector thruster fuel consumption in millibuckets per tick at full redstone throttle.")
                .defineInRange("liquidVectorThrusterFuelMbPerTickAtFullThrottle", 1.0d, 0.0001d, 1000.0d);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("multiblockThruster");
        MULTIBLOCK_2X_THRUST_MULTIPLIER = COMMON_BUILDER.comment("Thrust multiplier for a 2x2x2 multiblock thruster (e.g. 1.10 = 10% bonus).")
                .defineInRange("multiblock2xThrustMultiplier", 1.25d, 0.01d, 10.0d);
        MULTIBLOCK_3X_THRUST_MULTIPLIER = COMMON_BUILDER.comment("Thrust multiplier for a 3x3x3 multiblock thruster (e.g. 1.25 = 25% bonus).")
                .defineInRange("multiblock3xThrustMultiplier", 1.5d, 0.01d, 10.0d);
        MULTIBLOCK_2X_FUEL_EFFICIENCY = COMMON_BUILDER.comment("Fuel cost multiplier for a 2x2x2 multiblock thruster (e.g. 1.0 = no reduction, 0.8 = 20% cheaper).")
                .defineInRange("multiblock2xFuelEfficiency", 0.6d, 0.01d, 10.0d);
        MULTIBLOCK_3X_FUEL_EFFICIENCY = COMMON_BUILDER.comment("Fuel cost multiplier for a 3x3x3 multiblock thruster (e.g. 0.95 = 5% cheaper).")
                .defineInRange("multiblock3xFuelEfficiency", 0.4d, 0.01d, 10.0d);
        MULTIBLOCK_2X_OXIDIZER_EFFICIENCY = COMMON_BUILDER.comment("Oxidizer cost multiplier for a 2x2x2 multiblock thruster. 0.85 = 15% savings.")
                .defineInRange("multiblock2xOxidizerEfficiency", 0.85d, 0.01d, 10.0d);
        MULTIBLOCK_3X_OXIDIZER_EFFICIENCY = COMMON_BUILDER.comment("Oxidizer cost multiplier for a 3x3x3 multiblock thruster. 0.75 = 25% savings.")
                .defineInRange("multiblock3xOxidizerEfficiency", 0.75d, 0.01d, 10.0d);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("creativeVectorThruster");
        CREATIVE_VECTOR_THRUSTER_BASE_THRUST = COMMON_BUILDER.comment("Starting thrust value (kN) when a creative vector thruster is placed.",
                        "Default tuned for 1000-unit thrust scale parity with Sable physics.")
                .defineInRange("creativeVectorThrusterBaseThrust", 666.666666667d, 1.0d, 1000000.0d);
        CREATIVE_VECTOR_THRUSTER_MAX_THRUST = COMMON_BUILDER.comment("Maximum thrust (kN) the scroll can reach on a creative vector thruster.",
                        "Default tuned for 1000-unit thrust scale parity with Sable physics.")
                .defineInRange("creativeVectorThrusterMaxThrust", 6666.666666667d, 10.0d, 1000000.0d);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("physics");
        NOZZLE_OFFSET_FROM_CENTER = COMMON_BUILDER.comment("Offset from the block center where force is applied.")
                .defineInRange("nozzleOffsetFromCenter", 0.45d, 0.0d, 1.5d);
        USE_ATMOSPHERIC_PRESSURE = COMMON_BUILDER.comment("If true, atmospheric pressure affects thruster output at altitude.")
                .define("useAtmosphericPressure", false);
        ATMOSPHERIC_PRESSURE_AMOUNT = COMMON_BUILDER.comment("Strength of atmospheric pressure influence. 1.0 = full effect, 0.0 = no effect.")
                .defineInRange("atmosphericPressureAmount", 1.0d, 0.0d, 2.0d);
        THRUST_UNITS_PER_KN = COMMON_BUILDER.comment(
                        "Shared thrust unit scale: how many internal thrust units correspond to 1 kN.",
                        "Used by physics conversion, tooltip display, and ComputerCraft kN helpers.")
                .defineInRange("thrustUnitsPerKn", 1000.0d, 1.0d, 1000000.0d);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("Fuel Configuration");
        COMMON_BUILDER.comment(
                "Fuel properties by fluid id. Configure efficiency and burn rate separately as percentages.");
        COMMON_BUILDER.push("fuelProperties");
        for (String entry : defaultFuelProperties()) {
            String[] split = entry.split("=", 2);
            if (split.length != 2) continue;
            String fluidId = split[0];
            String[] values = split[1].split(",", 2);
            if (values.length != 2) continue;
            int efficiency;
            int burnRate;
            try {
                efficiency = Integer.parseInt(values[0].trim());
                burnRate = Integer.parseInt(values[1].trim());
            } catch (NumberFormatException ignored) {
                continue;
            }
            COMMON_BUILDER.push(configKeyForFluidId(fluidId));
            FUEL_EFFICIENCY_ENTRIES.put(fluidId,
                    COMMON_BUILDER.comment("Fuel efficiency percentage for " + fluidId + ".")
                            .defineInRange("efficiency", efficiency, 0, 10000));
            FUEL_BURN_RATE_ENTRIES.put(fluidId,
                    COMMON_BUILDER.comment("Fuel burn rate percentage for " + fluidId + ".")
                            .defineInRange("burnRate", burnRate, 0, 10000));
            COMMON_BUILDER.pop();
        }
        COMMON_BUILDER.pop();


        ADDITIONAL_THRUSTER_FUEL_PROPERTY_LINES = COMMON_BUILDER.comment(
                        "Additional thruster fuel lines (same format as defaults: fluid_id=efficiencyPercent,burnRatePercent).",
                        "Use for fluids that do not have a fuelProperties subsection. Entries here override matching fluids from the table above.")
                .defineListAllowEmpty("additionalThrusterFuelLines", ArrayList::new, obj -> obj instanceof String);

        COMMON_BUILDER.push("thrusterDyeColors");
        COMMON_BUILDER.comment("Particle color overrides when a dye is applied to a thruster. Values are RRGGBB hex strings.");
        for (String[] e : new String[][]{
                {"white", "FFFFFF"}, {"orange", "FF8000"}, {"magenta", "FF00FF"}, {"light_blue", "00BFFF"},
                {"yellow", "FFFF00"}, {"lime", "7FFF00"}, {"pink", "FF69B4"}, {"gray", "808080"},
                {"light_gray", "C0C0C0"}, {"cyan", "00FFFF"}, {"purple", "BF00FF"}, {"blue", "5555FF"},
                {"brown", "C86400"}, {"green", "00C800"}, {"red", "FF0000"}, {"black", "2A2A2A"},
        }) {
            THRUSTER_DYE_COLORS.put("minecraft:" + e[0] + "_dye",
                    COMMON_BUILDER.define(e[0], e[1]));
        }
        COMMON_BUILDER.pop();

        PropulsionDefaultStress.INSTANCE.registerAll(COMMON_BUILDER);
    }

    static {
        COMMON_SPEC = COMMON_BUILDER.build();
        CLIENT_SPEC = CLIENT_BUILDER.build();
    }

    private static List<String> defaultFuelProperties() {
        return new ArrayList<>(List.of(
                "createkinetic:turpentine=100,150",
                "minecraft:lava=75,100",
                "createdieselgenerators:plant_oil=55,170",
                "immersiveengineering:plantoil=55,170",
                "createdieselgenerators:ethanol=70,140",
                "immersiveengineering:ethanol=70,140",
                "mekanismgenerators:bioethanol=75,135",
                "northstar:biofuel=80,125",
                "createdieselgenerators:biodiesel=90,110",
                "immersiveengineering:biodiesel=90,110",
                "immersiveengineering:high_power_biodiesel=105,95",
                "createdieselgenerators:diesel=100,100",
                "tfmg:diesel=100,100",
                "stellaris:diesel=100,100",
                "tfmg:naphtha=95,105",
                "tfmg:kerosene=230,90",
                "createdieselgenerators:gasoline=125,80",
                "tfmg:gasoline=125,80",
                "tfmg:lpg=120,85",
                "northstar:hydrocarbon=130,75",
                "stellaris:fuel=115,100",
                "mekanism:hydrogen=230,90",
                "createaddition:bioethanol=75,135",
                "createaddition:seed_oil=55,170",
                "northstar:methane=105,95",
                "northstar:liquid_hydrogen=230,90",
                "immersivepetroleum:diesel_sulfur=100,100"
        ));
    }


    private static String configKeyForFluidId(String fluidId) {
        return fluidId
                .replace(':', '_')
                .replace('/', '_')
                .replace('.', '_')
                .replace('-', '_');
    }


    public static Integer getDyeColor(String dyeId) {
        ModConfigSpec.ConfigValue<String> cv = THRUSTER_DYE_COLORS.get(dyeId);
        if (cv == null) return null;
        try {
            return Integer.parseUnsignedInt(cv.get().trim(), 16);
        } catch (NumberFormatException | IllegalStateException ignored) {
            return null;
        }
    }

    public static boolean isDyeConfigured(String itemId) {
        return THRUSTER_DYE_COLORS.containsKey(itemId);
    }


    public static List<? extends String> getFuelPropertiesOrDefault() {
        LinkedHashMap<String, String> merged = new LinkedHashMap<>();

        if (!FUEL_EFFICIENCY_ENTRIES.isEmpty() && !FUEL_BURN_RATE_ENTRIES.isEmpty()) {
            for (Map.Entry<String, ModConfigSpec.IntValue> e : FUEL_EFFICIENCY_ENTRIES.entrySet()) {
                try {
                    ModConfigSpec.IntValue burnRate = FUEL_BURN_RATE_ENTRIES.get(e.getKey());
                    if (burnRate == null) {
                        continue;
                    }
                    merged.put(e.getKey(), e.getValue().get() + "," + burnRate.get());
                } catch (IllegalStateException ignored) {
                    // Config not ready — skip entry
                }
            }
        }

        try {
            List<? extends String> extra = ADDITIONAL_THRUSTER_FUEL_PROPERTY_LINES.get();
            if (extra != null) {
                for (Object o : extra) {
                    if (!(o instanceof String raw)) {
                        continue;
                    }
                    String line = raw.trim();
                    int sep = line.indexOf('=');
                    if (sep <= 0 || sep >= line.length() - 1) {
                        continue;
                    }
                    String fluidId = line.substring(0, sep).trim();
                    String rhs = line.substring(sep + 1).trim();
                    if (ResourceLocation.tryParse(fluidId) == null || !rhs.contains(",")) {
                        continue;
                    }
                    merged.put(fluidId, rhs);
                }
            }
        } catch (IllegalStateException ignored) {
            // ignore until config load completes
        }

        if (merged.isEmpty()) {
            return defaultFuelProperties();
        }

        List<String> out = new ArrayList<>(merged.size());
        for (Map.Entry<String, String> e : merged.entrySet()) {
            out.add(e.getKey() + "=" + e.getValue());
        }
        return out;
    }

    public static double getLiquidVectorThrusterBaseThrustOrDefault() {
        try {
            return LIQUID_VECTOR_THRUSTER_BASE_THRUST.get();
        } catch (IllegalStateException ignored) {
            return VECTOR_THRUSTER_BASE_THRUST.get();
        }
    }

    public static int getLiquidVectorThrusterFuelTankCapacityMbOrDefault() {
        try {
            return LIQUID_VECTOR_THRUSTER_FUEL_TANK_CAPACITY_MB.get();
        } catch (IllegalStateException ignored) {
            return FUEL_TANK_CAPACITY_MB.get();
        }
    }

    public static double getLiquidVectorThrusterFuelMbPerTickAtFullThrottleOrDefault() {
        try {
            return LIQUID_VECTOR_THRUSTER_FUEL_MB_PER_TICK_AT_FULL_THROTTLE.get();
        } catch (IllegalStateException ignored) {
            return FUEL_MB_PER_TICK_AT_FULL_THROTTLE.get();
        }
    }

    public static double getThrustUnitsPerKnOrDefault() {
        try {
            return THRUST_UNITS_PER_KN.get();
        } catch (IllegalStateException ignored) {
            return 1000.0d;
        }
    }


    public static ThrusterPlumeType getThrusterPlumeType() {
        return THRUSTER_PLUME_TYPE.get();
    }

    public static ThrusterPlumeType getCreativeThrusterPlumeType() {
        return CREATIVE_THRUSTER_PLUME_TYPE.get();
    }

    public static ThrusterPlumeType getIonThrusterPlumeType() {
        return ION_THRUSTER_PLUME_TYPE.get();
    }

    public static ThrusterPlumeType getVectorThrustersPlumeType() {
        return VECTOR_THRUSTERS_PLUME_TYPE.get();
    }

}
