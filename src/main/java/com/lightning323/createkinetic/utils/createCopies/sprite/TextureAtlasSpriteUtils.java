package com.lightning323.createkinetic.utils.createCopies.sprite;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class TextureAtlasSpriteUtils {
    public static TextureAtlasSprite makeTextureAtlasSprite(
            ResourceLocation atlasLocation,
            SpriteContents contents,
            int atlasWidth,
            int atlasHeight,
            int x,
            int y
    ) {
        return new TextureAtlasSprite(atlasLocation, contents, atlasWidth, atlasHeight, x, y);
    }
}
