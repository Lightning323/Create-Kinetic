package org.lightning323.createkinetic.events;

import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.lightning323.createkinetic.config.PropulsionConfig;
import org.lightning323.createkinetic.registries.PropulsionBlocks;

public class ModSetupEvents {
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BoilerHeater.REGISTRY.register(PropulsionBlocks.SOLID_BURNER.get(), ModSetupEvents::propulsionBurnerHeat);
            BoilerHeater.REGISTRY.register(PropulsionBlocks.LIQUID_BURNER.get(), ModSetupEvents::propulsionBurnerHeat);
        });
    }

    private static float propulsionBurnerHeat(Level level, BlockPos pos, BlockState state) {
        if (!PropulsionConfig.BURNERS_HEAT_STEAM_ENGINES.get()) {
            return BoilerHeater.NO_HEAT;
        }
        HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
        if (value == HeatLevel.NONE) {
            return BoilerHeater.NO_HEAT;
        }
        if (value == HeatLevel.SEETHING) {
            return PropulsionConfig.BURNERS_SUPERHEAT_STEAM_ENGINES.get() ? 2 : 1;
        }
        if (value.isAtLeast(HeatLevel.FADING)) {
            return 1;
        }
        return BoilerHeater.PASSIVE_HEAT;
    }
}
