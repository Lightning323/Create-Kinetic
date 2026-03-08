package com.lightning323.createkinetic;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Createkinetic.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KineticConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue FORGIVING_SAILS
        = BUILDER.comment("Should sails be forgiving?").define("forgiving_sails", false);

    private static final ForgeConfigSpec.BooleanValue ENABLE_WIND
            = BUILDER.define("enable_wind", true);

    private static final ForgeConfigSpec.IntValue SAIL_POWER =
        BUILDER.comment("Sail Power").defineInRange("sail_power", 25000, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue KEEL_STRENGTH
        = BUILDER.comment("Keel Strength").defineInRange("keel_strength", 4.0, 0.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue MAGIC_BALLAS_FORCE
        = BUILDER.comment("Magic Ballast Righting Force").defineInRange("magic_ballast_force", 0.25, 0.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue BALLAST_STRENGTH
        = BUILDER.comment("Ballast Float Strength").defineInRange("ballast_strength", 0.0625, 0.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec.DoubleValue BUOY_STRENGTH
        = BUILDER.comment("Buoy Float Strength").defineInRange("buoy_strength", 0.125, 0.0, Double.MAX_VALUE);

//    private static final double noSailZone = toRadians((double)Integer.parseInt(ConfigUtils.config.getOrDefault("no-sail-zone", "90"))/2);
    private static final ForgeConfigSpec.DoubleValue NO_SAIL_ZONE
            = BUILDER.comment("No sail zone (in degrees)")
            .defineInRange("no_sail_zone", 45.0, 0.0, 360.0);

//    private static final int sailSpeed = Integer.parseInt(ConfigUtils.config.getOrDefault("sail-power","25000"));
    private static final ForgeConfigSpec.IntValue SAIL_SPEED
            = BUILDER.defineInRange("sail_speed", 1000, 0, Integer.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean forgivingSails;
    public static int sailPower;
    public static boolean enableWind;
    public static double keelStrength;
    public static double magicBallastForce;
    public static double ballastStrength;
    public static double buoyStrength;
    public static double noSailZone;
    public static double sailSpeed;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        forgivingSails = FORGIVING_SAILS.get();
        sailPower = SAIL_POWER.get();
        keelStrength = KEEL_STRENGTH.get();
        magicBallastForce = MAGIC_BALLAS_FORCE.get();
        ballastStrength = BALLAST_STRENGTH.get();
        buoyStrength = BUOY_STRENGTH.get();
        enableWind = ENABLE_WIND.get();
        noSailZone = NO_SAIL_ZONE.get();
        sailSpeed = SAIL_SPEED.get();

//        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
//        items = ITEM_STRINGS.get().stream().map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))).collect(Collectors.toSet());
    }
}
