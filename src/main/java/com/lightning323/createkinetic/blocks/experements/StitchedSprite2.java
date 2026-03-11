package com.lightning323.createkinetic.blocks.experements;

import net.createmod.catnip.render.StitchedSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class StitchedSprite2 extends StitchedSprite {

	public StitchedSprite2(ResourceLocation atlas, ResourceLocation location) {
		super(atlas, location);
	}

	@Override
	protected void loadSprite(TextureAtlas atlas) {
//		sprite = TextureAtlasSpriteUtils.makeTextureAtlasSprite(atlas,new SpriteContents())
	}

}