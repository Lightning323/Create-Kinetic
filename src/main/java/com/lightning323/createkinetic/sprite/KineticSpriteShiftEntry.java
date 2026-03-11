package com.lightning323.createkinetic.sprite;

import java.util.Objects;

import javax.annotation.Nullable;

import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class KineticSpriteShiftEntry extends SpriteShiftEntry {

	public void set(ResourceLocation originalLocation, ResourceLocation targetLocation) {
		original = new KineticStitchedSprite(originalLocation);
		target = new KineticStitchedSprite(targetLocation);
	}
}