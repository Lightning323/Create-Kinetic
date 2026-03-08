package com.lightning323.createkinetic.registries;

import com.simibubi.create.Create;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;

public class SpriteShifts {

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get(Create.asResource(originalLocation), Create.asResource(targetLocation));
    }

    public static final SpriteShiftEntry ROPE_PULLEY_COIL = get("block/rope_pulley_coil", "block/rope_pulley_coil_scroll");

}
