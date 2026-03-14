package com.lightning323.createkinetic.sprite;

import com.lightning323.createkinetic.CreateKinetic;
import net.createmod.catnip.render.StitchedSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class KineticStitchedSprite extends StitchedSprite {

    public KineticStitchedSprite(ResourceLocation atlas, ResourceLocation location) {
        super(atlas, location);
    }

    public KineticStitchedSprite(ResourceLocation location) {
        this(InventoryMenu.BLOCK_ATLAS, location);
    }

    protected void loadSprite(TextureAtlas atlas) {
        sprite = atlas.getSprite(location);
        CreateKinetic.LOGGER.debug("Created spriteShiftEntry for {} at X={}; y={}", location, sprite.getX(), sprite.getY());
    }

}
