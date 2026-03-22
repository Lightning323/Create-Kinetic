package com.lightning323.createkinetic;

import kotlin.reflect.jvm.internal.impl.resolve.constants.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = CreateKinetic.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KineticConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue FORGIVING_SAILS
            = BUILDER.comment("Should sails be forgiving?").define("forgiving_sails", false);

    private static final ForgeConfigSpec.DoubleValue KEEL_STRENGTH
            = BUILDER.comment("Keel Strength").defineInRange("keel_strength", 4.0, 0.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue ENCHANTED_BALLAST_FORCE
            = BUILDER.comment("Enchanted Ballast Righting Force").defineInRange("enchanted_ballast_force", 2, 0.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue BALLAST_STRENGTH
            = BUILDER.comment("Ballast Float Strength").defineInRange("ballast_strength", 0.0625, 0.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue BUOY_STRENGTH
            = BUILDER.comment("Buoy Float Strength").defineInRange("buoy_strength", 0.125, 0.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue NO_SAIL_ZONE
            = BUILDER.comment("No sail zone (in degrees)")
            .defineInRange("no_sail_zone", 45.0, 0.0, 360.0);

    private static final ForgeConfigSpec.IntValue SAIL_SPEED
            = BUILDER.defineInRange("sail_speed", 60000, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue MIN_WIND_SPEED
            = BUILDER.defineInRange("min_wind", 0.5, 0, 1);


    private static final ForgeConfigSpec.DoubleValue TURN_ACCELERATION
            = BUILDER.comment("The maximum linear acceleration at any point on the ship caused by helm torque")
            .defineInRange("turn_acceleration", 10.0, 0.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue MAX_SIZE_FOR_TURN_SPEED_PENALTY
            = BUILDER.comment("The maximum distance from center of mass to one end of the ship considered by " +
                    "the turn speed. At it's default of 16, it ensures that really large ships will turn at the same " +
                    "speed as a ship with a center of mass only 16 blocks away from the farthest point in the ship. " +
                    "That way, large ships do not turn painfully slowly")
            .defineInRange("max_size_for_turn_speed_penalty", 16.0, 0.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue TURN_SPEED
            = BUILDER.comment("The maximum linear speed at any point on the ship caused by helm torque")
            .defineInRange("turn_speed", 3.0, 0.0, Double.MAX_VALUE);

//    private static final ForgeConfigSpec.BooleanValue WIND_PARTICLES
//            = BUILDER.comment("Should wind particles be enabled?")
//            .define("wind_particles", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean forgivingSails;
    public static double keelStrength;
    public static double enchantedBallastForce;
    public static double ballastStrength;
    public static double buoyStrength;
    public static double noSailZone;
    public static double sailSpeed;
    public static double minWindSpeed;

    public static double turnAcceleration;
    public static double maxSizeForTurnSpeedPenalty;
    public static double turnSpeed;
    public static boolean windParticles;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        forgivingSails = FORGIVING_SAILS.get();
        keelStrength = KEEL_STRENGTH.get();
        enchantedBallastForce = ENCHANTED_BALLAST_FORCE.get();
        ballastStrength = BALLAST_STRENGTH.get();
        buoyStrength = BUOY_STRENGTH.get();
        noSailZone = NO_SAIL_ZONE.get();
        sailSpeed = SAIL_SPEED.get();
        minWindSpeed = MIN_WIND_SPEED.get();
        turnAcceleration = TURN_ACCELERATION.get();
        maxSizeForTurnSpeedPenalty = MAX_SIZE_FOR_TURN_SPEED_PENALTY.get();
        turnSpeed = TURN_SPEED.get();
//        windParticles = WIND_PARTICLES.get();

//        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
//        items = ITEM_STRINGS.get().stream().map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))).collect(Collectors.toSet());
    }
}
