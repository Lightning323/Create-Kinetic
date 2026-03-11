package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.Createkinetic;
import com.simibubi.create.Create;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;
import net.minecraft.resources.ResourceLocation;

import static com.lightning323.createkinetic.Createkinetic.MOD_ID;

public class KineticSpriteShifts {
    public static final SpriteShiftEntry
            SAIL_COIL = getCreate("block/rope_pulley_coil", "block/rope_pulley_coil_scroll");
//            SAIL_COIL = get("block/sail_coil", "block/sail_coil_scroll");
//            SAIL_COIL = getCreate("block/blaze_burner_flame", "block/blaze_burner_flame_scroll");

    private static SpriteShiftEntry getCreate(String originalLocation, String targetLocation) {
        return SpriteShifter.get(Create.asResource(originalLocation), Create.asResource(targetLocation));
    }

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get(Createkinetic.resource(originalLocation), Createkinetic.resource(targetLocation));
    }

    public static void init() {

    }
}
