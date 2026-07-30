package org.lightning323.createkinetic.mixin;

import dev.simulated_team.simulated.registrate.simulated_tab.SimulatedCreativeTab;
import net.minecraft.world.item.ItemStack;
import org.lightning323.createkinetic.registries.KineticCreativeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(SimulatedCreativeTab.class)
public class SimulatedCreativeTabMixin {

    @Inject(
        method = "processItems",
        at = @At("HEAD")
    )
    private static void onProcessItems(
            Consumer<ItemStack> displayItems,
            Consumer<ItemStack> searchItems,
            CallbackInfo ci
    ) {
        //We put this here to make SURE our items are always added last
        KineticCreativeTabs.registerAeronauticsSections();
    }
}