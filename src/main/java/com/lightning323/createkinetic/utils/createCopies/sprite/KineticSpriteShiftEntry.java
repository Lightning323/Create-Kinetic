package com.lightning323.createkinetic.utils.createCopies.sprite;

import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.resources.ResourceLocation;

public class KineticSpriteShiftEntry extends SpriteShiftEntry {

	public void set(ResourceLocation originalLocation, ResourceLocation targetLocation) {
		original = new KineticStitchedSprite(originalLocation);
		target = new KineticStitchedSprite(targetLocation);
	}
}