package com.lightning323.createkinetic.sprite;


import java.util.HashMap;
import java.util.Map;

import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

//From create SpriteShifter
public class KineticSpriteShifter {

    private static final Map<String, KineticSpriteShiftEntry> ENTRY_CACHE = new HashMap<>();

    public static KineticSpriteShiftEntry get(ResourceLocation originalLocation, ResourceLocation targetLocation) {
        String key = originalLocation + "->" + targetLocation;
        if (ENTRY_CACHE.containsKey(key))
            return ENTRY_CACHE.get(key);

        KineticSpriteShiftEntry entry = new KineticSpriteShiftEntry();
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> {
            entry.set(originalLocation, targetLocation);
        });


        ENTRY_CACHE.put(key, entry);
        return entry;
    }
}