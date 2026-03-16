package com.lightning323.createkinetic.ship;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import static com.lightning323.createkinetic.ship.KineticShipControl.LOGGER;

public class WindManager {
    private static final SimplexNoise WIND_NOISE = new SimplexNoise(RandomSource.create(42L));

    // 2 minutes = 120 seconds * 20 ticks = 2400 ticks
    private static final int UPDATE_INTERVAL = 2400;
    private static final int LERP_INTERVAL = 20;
    private static final double MIN_WIND = 0.4;

    private static double lastStrength = 1.0;
    private static double targetStrength = 1.0;

    private static double lastDirection = 0.0;
    private static double targetDirection = 0.0;

    private static long lastUpdateTick = -1;

    public static void updateWind(Level world) {
        long currentTime = world.getGameTime();

        // 1. Every 2 minutes, pick a new "Target"
        if (currentTime % UPDATE_INTERVAL == 0 && currentTime != lastUpdateTick) {
            lastUpdateTick = currentTime;

            // Move current target to "last" so we can blend from it
            lastStrength = targetStrength;
            lastDirection = targetDirection;

            // Calculate new targets using noise
            double noiseStep = currentTime * 0.0001; //Larger multiplier will increase the frequency of changes

            double rawStrength = WIND_NOISE.getValue(noiseStep, 0);
            double rawDirection = WIND_NOISE.getValue(0, noiseStep);

            // Map Strength: Noise is [-1, 1], we want [MIN_WIND, 1.0]
            double normalized = (rawStrength + 1.0) / 2.0; // Shipped to [0, 1]
            targetStrength = MIN_WIND + (normalized * (1.0 - MIN_WIND));

            //Map Direction: Noise is [-1, 1], we want [0, 360]
            targetDirection = ((rawDirection + 1) / 2.0) * 360.0;
        }
        if (currentTime % LERP_INTERVAL == 0) {// 2. Continuous Interpolation
            long timeInCycle = currentTime % UPDATE_INTERVAL;
            // This calculates how far we are through the current 2-minute window (0.0 to 1.0)
            float delta = (float) timeInCycle / (float) UPDATE_INTERVAL;
            // Smoothly slide from last value to target value
            currentStrength = Mth.lerp(delta, lastStrength, targetStrength);
            // For direction, we use lerpAngle to ensure it takes the shortest path (e.g., 350 to 10)
            currentDirection = lerpAngle(delta, (float) lastDirection, (float) targetDirection);
//            LOGGER.debug("Wind: {}x {}°", currentStrength, currentDirection);
        }
    }

    private static double currentStrength;
    private static double currentDirection;

    /**
     *
     * @return the wind strength between MIN_WIND and 1.0
     */
    public static double getWindStrength() {
        return currentStrength;
    }

    /**
     *
     * @return the wind direction in degrees
     */
    public static double getWindDirection() {
        return currentDirection;
    }

    // Helper to wrap angles correctly so the boat doesn't spin 350 degrees the wrong way
    private static float lerpAngle(float delta, float start, float end) {
        float diff = ((end - start + 180 + 360) % 360) - 180;
        return (start + (diff * delta) + 360) % 360;
    }
}
