package org.lightning323.createkinetic.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.lightning323.createkinetic.config.KineticConfig;
import org.lightning323.createkinetic.content.heat.burners.AbstractBurnerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BasinBlockEntity.class)
public class BasinBlockEntityMixin {
    @Inject(method = "getHeatLevelOf", at = @At("RETURN"), cancellable = true)
    private static void kinetic$checkCustomBurners(BlockState state, CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
        if (cir.getReturnValue() == BlazeBurnerBlock.HeatLevel.NONE 
                && state.getBlock() instanceof AbstractBurnerBlock
                && KineticConfig.BURNERS_POWER_HEATED_MIXERS.get()) {
            BlazeBurnerBlock.HeatLevel burnerHeat = state.getValue(AbstractBurnerBlock.HEAT);
            if (burnerHeat == BlazeBurnerBlock.HeatLevel.SEETHING
                    && !KineticConfig.BURNERS_SUPERHEAT_STEAM_ENGINES.get()) {
                cir.setReturnValue(BlazeBurnerBlock.HeatLevel.KINDLED);
                return;
            }
            cir.setReturnValue(burnerHeat);
        }
    }
}

