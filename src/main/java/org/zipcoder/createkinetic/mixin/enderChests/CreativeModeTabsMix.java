package org.zipcoder.createkinetic.mixin.enderChests;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zipcoder.createkinetic.ModItems;

@Mixin(value = CreativeModeTabs.class)
public abstract class CreativeModeTabsMix {

    @Inject(method = {"method_51332","lambda$bootstrap$10"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab$Output;accept(Lnet/minecraft/world/level/ItemLike;)V",ordinal = 84, shift = At.Shift.AFTER))
    private static void injectedAfter(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output, CallbackInfo ci) {
        output.accept(ModItems.IRON_CHEST);
        output.accept(ModItems.COPPER_CHEST);
        output.accept(ModItems.GOLD_CHEST);
        output.accept(ModItems.LAPIS_CHEST);
        output.accept(ModItems.REDSTONE_CHEST);
        output.accept(ModItems.DIAMOND_CHEST);
        output.accept(ModItems.EMERALD_CHEST);
        output.accept(ModItems.NETHERITE_CHEST);
    }

}