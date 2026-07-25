package org.lightning323.createkinetic.registries;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import net.minecraft.resources.ResourceLocation;
import org.lightning323.createkinetic.CreateKinetic;

public class PropulsionSpriteShifts {
    public static final CTSpriteShiftEntry WING_TEXTURE = getCT(AllCTTypes.OMNIDIRECTIONAL, "wing");
    public static final CTSpriteShiftEntry TEMPERED_WING_TEXTURE = getCT(AllCTTypes.OMNIDIRECTIONAL, "tempered_wing");
    public static final CTSpriteShiftEntry PLATINUM_CASING_TEXTURE = getCT(AllCTTypes.OMNIDIRECTIONAL, "platinum_casing");
    public static final CTSpriteShiftEntry PLATINUM_FLUID_TANK = getCT(AllCTTypes.RECTANGLE, "fluid_tank");
    public static final CTSpriteShiftEntry PLATINUM_FLUID_TANK_TOP = getCT(AllCTTypes.RECTANGLE, "fluid_tank_top");
    public static final CTSpriteShiftEntry PLATINUM_FLUID_TANK_INNER = getCT(AllCTTypes.RECTANGLE, "fluid_tank_inner");
    public static final CTSpriteShiftEntry PLATINUM_FLUID_VESSEL = getCT(AllCTTypes.RECTANGLE, "fluid_tank");
    public static final CTSpriteShiftEntry PLATINUM_FLUID_VESSEL_TOP = getCT(AllCTTypes.RECTANGLE, "fluid_tank_top");
    public static final CTSpriteShiftEntry PLATINUM_FLUID_VESSEL_INNER = getCT(AllCTTypes.RECTANGLE, "fluid_tank_inner");

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return CTSpriteShifter.getCT(type,
            ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "block/" + blockTextureName),
            ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "block/" + blockTextureName + "_connected")
        );
    }
}
