/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.createmod.catnip.render.SpriteShifter
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.DyeColor
 */
package org.lightning323.createkinetic.registries;

import java.util.EnumMap;
import java.util.Map;

import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.lightning323.createkinetic.CreateKinetic;

public class TracksSpriteShifts {
    public static final SpriteShiftEntry BELT = TracksSpriteShifts.get("block/belt", "block/belt_scroll");
    private static final Map<DyeColor, SpriteShiftEntry> COLORED_BELTS = new EnumMap<DyeColor, SpriteShiftEntry>(DyeColor.class);

    public static SpriteShiftEntry belt(DyeColor color) {
        return color == null ? BELT : COLORED_BELTS.getOrDefault(color, BELT);
    }

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get((ResourceLocation) ResourceLocation.tryBuild((String) CreateKinetic.ID, (String) originalLocation), (ResourceLocation) ResourceLocation.tryBuild((String) CreateKinetic.ID, (String) targetLocation));
    }

    public static void init() {
    }

    static {
        for (DyeColor color : DyeColor.values()) {
//            System.out.println("DYE COLOR ======================== "+color);
            /**
             * DYE COLOR ======================== white
             * DYE COLOR ======================== orange
             * DYE COLOR ======================== magenta
             * DYE COLOR ======================== light_blue
             * DYE COLOR ======================== yellow
             * DYE COLOR ======================== lime
             * DYE COLOR ======================== pink
             * DYE COLOR ======================== gray
             * DYE COLOR ======================== light_gray
             * DYE COLOR ======================== cyan
             * DYE COLOR ======================== purple
             * DYE COLOR ======================== blue
             * DYE COLOR ======================== brown
             * DYE COLOR ======================== green
             * DYE COLOR ======================== red
             * DYE COLOR ======================== black
             * DYE COLOR ======================== maroon
             * DYE COLOR ======================== rose
             * DYE COLOR ======================== coral
             * DYE COLOR ======================== indigo
             * DYE COLOR ======================== navy
             * DYE COLOR ======================== slate
             * DYE COLOR ======================== olive
             * DYE COLOR ======================== amber
             * DYE COLOR ======================== beige
             * DYE COLOR ======================== teal
             * DYE COLOR ======================== mint
             * DYE COLOR ======================== aqua
             * DYE COLOR ======================== verdant
             * DYE COLOR ======================== forest
             * DYE COLOR ======================== ginger
             * DYE COLOR ======================== tan
             */
            //TODO: I should have unique colors for every dye depot color, but... I don't want to
            String colorName = color.getName();
            colorName = switch (colorName) {
                case "maroon" -> "red";
                case "olive" -> "green";
                case "beige","tan","amber" -> "yellow";
                case "teal", "aqua", "mint" -> "cyan";
                case "forest", "verdant" -> "green";
                case "ginger" -> "orange";
                case "coral" -> "pink";
                case "indigo", "navy" -> "blue";
                case "slate" -> "gray";
                case "rose" -> "magenta";
                default -> colorName;
            };
            COLORED_BELTS.put(color, TracksSpriteShifts.get("block/belt", "block/belt_" + colorName + "_scroll"));
        }
    }
}

