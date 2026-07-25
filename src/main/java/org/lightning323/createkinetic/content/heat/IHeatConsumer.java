package org.lightning323.createkinetic.content.heat;

public interface IHeatConsumer {
    boolean isActive();

    float getOperatingThreshold();
    
    float consumeHeat(float maxAvailable, float expectedHeatOutput, boolean simulate);
}

