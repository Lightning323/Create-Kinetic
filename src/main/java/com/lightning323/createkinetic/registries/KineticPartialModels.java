package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.Createkinetic;
import com.lightning323.createkinetic.blocks.helm.WoodTypeEnum;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.Map;

import static com.lightning323.createkinetic.Createkinetic.MOD_ID;

public class KineticPartialModels {
    public static final PartialModel
            SAIL_COIL = block("sail_pulley/sail_coil"),
            ROPE_HALF = block("sail_pulley/rope_half"),
            ROPE_HALF_MAGNET = block("sail_pulley/rope_half_magnet"),
            ROPE = block("sail_pulley/rope"),
            PULLEY_MAGNET = block("sail_pulley/pulley_magnet"),
            ROPE_HALF_WEIGHT = block("retractable_sail/rope_half_weight"),
            PULLEY_WEIGHT = block("retractable_sail/pulley_weight"),
            HELM_WHEEL = block("helm/oak_ship_helm_wheel");


    public static final Map<WoodTypeEnum, PartialModel> HELM_WHEELS = new EnumMap<>(WoodTypeEnum.class);
    /**
     * Registers a specific wheel model.
     * Use this if a specific wood type doesn't follow the standard naming convention.
     */
    public static void registerHelmWheel(WoodTypeEnum srType, String path) {
        HELM_WHEELS.put(srType, block("helm/" + path));
    }

    public static PartialModel getHelmWheel(WoodTypeEnum srType) {
        return HELM_WHEELS.getOrDefault(srType, HELM_WHEEL);
    }


    private static PartialModel block(String path) {
        return PartialModel.of(new ResourceLocation(MOD_ID, "block/" + path));
    }

    private static PartialModel entity(String path) {
        return PartialModel.of(new ResourceLocation(MOD_ID, "entity/" + path));
    }

    public static void init() {
        //Registers all helm wheel models
        for (WoodTypeEnum type : WoodTypeEnum.values()) {
            // Automatically builds "helm/oak_ship_helm_wheel", etc.
            registerHelmWheel(type, type.getId() + "_ship_helm_wheel");
            Createkinetic.LOGGER.debug("Registering type {} as {}", type, type.getId() + "_ship_helm_wheel");
        }
    }
}
