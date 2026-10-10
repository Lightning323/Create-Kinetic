package org.lightning323.createkinetic.content.thruster;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record FluidThrusterProperties(
    float thrustMultiplier,
    float consumptionMultiplier,
    ThrusterParticleType particleType,
    List<ResourceLocation> overrideTextures,
    Integer overrideColor,
    boolean useFluidColor
) {
    public static final FluidThrusterProperties DEFAULT = new FluidThrusterProperties(
        1.0f, 1.0f, ThrusterParticleType.PLUME, List.of(), null, false
    );

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(thrustMultiplier);
        buf.writeFloat(consumptionMultiplier);
        buf.writeEnum(particleType);
        buf.writeCollection(overrideTextures, FriendlyByteBuf::writeResourceLocation);
        buf.writeBoolean(overrideColor != null);
        if (overrideColor != null) {
            buf.writeInt(overrideColor);
        }
        buf.writeBoolean(useFluidColor);
    }

    public static FluidThrusterProperties decode(FriendlyByteBuf buf) {
        float thrust = buf.readFloat();
        float consumption = buf.readFloat();
        ThrusterParticleType particle = buf.readEnum(ThrusterParticleType.class);
        List<ResourceLocation> textures = buf.readCollection(java.util.ArrayList::new, FriendlyByteBuf::readResourceLocation);
        Integer color = buf.readBoolean() ? buf.readInt() : null;
        boolean useFluidColor = buf.readBoolean();
        return new FluidThrusterProperties(thrust, consumption, particle, textures, color, useFluidColor);
    }
}
