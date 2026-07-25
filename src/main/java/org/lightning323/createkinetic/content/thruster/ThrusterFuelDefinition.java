package org.lightning323.createkinetic.content.thruster;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.Optional;

public record ThrusterFuelDefinition (
    ResourceLocation fluidId,
    float thrustMultiplier,
    float consumptionMultiplier,
    ThrusterParticleType particle,
    List<ResourceLocation> overrideTextures,
    Optional<Integer> overrideColor,
    boolean useFluidColor,
    Optional<String> requiredMod
) {
    public static final Codec<ThrusterFuelDefinition> CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            ResourceLocation.CODEC.fieldOf("fluid").forGetter(ThrusterFuelDefinition::fluidId),
            Codec.FLOAT.fieldOf("thrust_multiplier").forGetter(ThrusterFuelDefinition::thrustMultiplier),
            Codec.FLOAT.fieldOf("consumption_multiplier").forGetter(ThrusterFuelDefinition::consumptionMultiplier),
            ThrusterParticleType.CODEC.optionalFieldOf("particle", ThrusterParticleType.PLUME).forGetter(ThrusterFuelDefinition::particle),
            ResourceLocation.CODEC.listOf().optionalFieldOf("override_textures", List.of()).forGetter(ThrusterFuelDefinition::overrideTextures),
            Codec.INT.optionalFieldOf("override_color").forGetter(ThrusterFuelDefinition::overrideColor),
            Codec.BOOL.optionalFieldOf("use_fluid_color", false).forGetter(ThrusterFuelDefinition::useFluidColor),
            Codec.STRING.optionalFieldOf("required_mod").forGetter(ThrusterFuelDefinition::requiredMod)
        ).apply(instance, ThrusterFuelDefinition::new));

    
    public Fluid getFluid() {
        Fluid fluid = BuiltInRegistries.FLUID.get(this.fluidId);
        return fluid == null ? Fluids.EMPTY : fluid;
    }
}
