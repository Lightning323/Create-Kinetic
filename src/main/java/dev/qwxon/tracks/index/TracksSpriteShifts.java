/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.createmod.catnip.render.SpriteShifter
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.DyeColor
 */
package dev.qwxon.tracks.index;

import java.util.EnumMap;
import java.util.Map;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class TracksSpriteShifts {
    public static final SpriteShiftEntry BELT = TracksSpriteShifts.get("block/belt", "block/belt_scroll");
    private static final Map<DyeColor, SpriteShiftEntry> COLORED_BELTS = new EnumMap<DyeColor, SpriteShiftEntry>(DyeColor.class);

    public static SpriteShiftEntry belt(DyeColor color) {
        return color == null ? BELT : COLORED_BELTS.getOrDefault(color, BELT);
    }

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get((ResourceLocation)ResourceLocation.tryBuild((String)"tracks", (String)originalLocation), (ResourceLocation)ResourceLocation.tryBuild((String)"tracks", (String)targetLocation));
    }

    private static String textureName(DyeColor color) {
        return color == DyeColor.LIME ? "light_green" : color.getName();
    }

    public static void init() {
    }

    static {
        for (DyeColor color : DyeColor.values()) {
            String colorName = TracksSpriteShifts.textureName(color);
            COLORED_BELTS.put(color, TracksSpriteShifts.get("block/belt", "block/belt_" + colorName + "_scroll"));
        }
    }
}

