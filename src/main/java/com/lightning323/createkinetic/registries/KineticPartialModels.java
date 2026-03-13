package com.lightning323.createkinetic.registries;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;

import static com.lightning323.createkinetic.Createkinetic.MOD_ID;

public class KineticPartialModels {
    public static final PartialModel
            SAIL_COIL = block("sail_pulley/sail_coil"),
            ROPE_HALF = block("sail_pulley/rope_half"),
            ROPE_HALF_MAGNET = block("sail_pulley/rope_half_magnet"),
            ROPE = block("sail_pulley/rope"),
            PULLEY_MAGNET = block("sail_pulley/pulley_magnet"),
            ROPE_HALF_WEIGHT = block("retractable_sail/rope_half_weight"),
            PULLEY_WEIGHT = block("retractable_sail/pulley_weight");

    private static PartialModel block(String path) {
        return PartialModel.of(new ResourceLocation(MOD_ID, "block/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of(new ResourceLocation(MOD_ID, "entity/" + path));
    }

    public static void init() {
        // init static fields
    }
}
