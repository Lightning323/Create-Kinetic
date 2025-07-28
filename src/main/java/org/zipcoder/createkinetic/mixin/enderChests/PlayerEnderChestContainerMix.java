package org.zipcoder.createkinetic.mixin.enderChests;

import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = PlayerEnderChestContainer.class)
public abstract class PlayerEnderChestContainerMix {

    private static final int MAX_SIZE = 9 * 100;

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 27))
    private static int modifyConst(int value) {
        return MAX_SIZE;
    }
}