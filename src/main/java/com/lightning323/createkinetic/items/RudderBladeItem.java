package com.lightning323.createkinetic.items;

import com.lightning323.createkinetic.registries.KineticItems;
import net.minecraft.world.item.Item;


public class RudderBladeItem extends Item {

    public final BladeProperties bladeProperties;
    public final BladeType type;

    public RudderBladeItem(Item.Properties rarity, BladeType type, BladeProperties properties) {
        super(rarity);
        this.bladeProperties = properties;
        this.type = type;
    }


    public static class BladeProperties {
        public BladeProperties(float forceMultiplier) {
            this.forceMultiplier = forceMultiplier;
        }
        public final float forceMultiplier;

    }

    public static enum BladeType {
        COPPER, IRON;


        public static int toInt(RudderBladeItem item) {
            return item.type.ordinal();
        }

        public static RudderBladeItem toItem(int id) {
            BladeType type = BladeType.values()[id];
            switch (type) {
                case IRON -> {
                    return KineticItems.IRON_RUDDER_BLADE.get();
                }
                case COPPER -> {
                    return KineticItems.COPPER_RUDDER_BLADE.get();
                }
            }
            return null;
        }

    }
}
