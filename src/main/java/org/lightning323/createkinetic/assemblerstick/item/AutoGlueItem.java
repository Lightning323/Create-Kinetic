package org.lightning323.createkinetic.assemblerstick.item;

import net.minecraft.world.item.ItemStack;

public class AutoGlueItem extends CreateStyleTooltipItem {
    public AutoGlueItem(final Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(final ItemStack stack) {
        return true;
    }
}
