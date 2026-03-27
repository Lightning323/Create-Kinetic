package com.lightning323.createkinetic.mixin;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "com.simibubi.create.infrastructure.config.CStress", remap = false)
public interface CStressAccessor {

    // You must explicitly point to the field name because it's all caps
    @Accessor("DEFAULT_CAPACITIES")
    static Object2DoubleMap<ResourceLocation> getCapacities() {
        throw new UnsupportedOperationException();
    }

    @Accessor("DEFAULT_IMPACTS")
    static Object2DoubleMap<ResourceLocation> getImpacts() {
        throw new UnsupportedOperationException();
    }
}