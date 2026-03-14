package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.sprite.KineticSpriteShifter;
import com.simibubi.create.Create;
import net.createmod.catnip.render.SpriteShiftEntry;

public class KineticSpriteShifts {

    public static final SpriteShiftEntry
            SAIL_COIL = KineticSpriteShifter.get(
            Create.asResource("block/rope_pulley_coil"),
            Create.asResource("block/rope_pulley_coil_scroll"));
//    SAIL_COIL = KineticSpriteShifter.get(
//            Create.asResource("block/blaze_burner_flame"),
//            Create.asResource("block/blaze_burner_flame_scroll"));
//            SAIL_COIL2 = KineticSpriteShifter.get(
//            Createkinetic.resource("block/sail_coil"),
//            Createkinetic.resource("block/sail_coil_scroll"));



    public static void init() {

    }
}
