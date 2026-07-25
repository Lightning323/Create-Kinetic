package org.lightning323.createkinetic.content.heat;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;

public class HeatMapper {
    public static float getMinHeatPercent(HeatLevel heatLevel) {
        if (heatLevel == HeatLevel.SEETHING) return 0.9f;
        if (heatLevel == HeatLevel.KINDLED) return 0.6f;
        if (heatLevel == HeatLevel.FADING) return 0.3f;
        if (heatLevel == HeatLevel.SMOULDERING) return 0.01f;
        return 0.0f;
    } 

    public static HeatLevel getHeatLevel(float percentage) {
        if (percentage > 0.9f) return HeatLevel.SEETHING;
        if (percentage > 0.6f) return HeatLevel.KINDLED;
        if (percentage > 0.3f) return HeatLevel.FADING;
        if (percentage > 0.01f) return HeatLevel.SMOULDERING;
        return HeatLevel.NONE; 
    }

    public static enum HeatLevelString {
        COLD, WARM, HOT, SEARING;
    }

    public static HeatLevelString getHeatString(float percentage) {
        if (percentage > 0.6f) return HeatLevelString.SEARING;
        if (percentage > 0.3f) return HeatLevelString.HOT;
        if (percentage > 0.1f) return HeatLevelString.WARM; 
        return HeatLevelString.COLD;
    }
}

