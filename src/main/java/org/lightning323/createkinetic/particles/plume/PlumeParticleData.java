package org.lightning323.createkinetic.particles.plume;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nonnull;

public class PlumeParticleData implements ParticleOptions, ICustomParticleDataWithSprite<PlumeParticleData> {

    // 1. MapCodec replaces the old Deserializer for data/commands
    public static MapCodec<PlumeParticleData> codec(ParticleType<PlumeParticleData> type) {
        return MapCodec.unit(() -> new PlumeParticleData(type));
    }

    // 2. StreamCodec replaces the old writeToNetwork/fromNetwork logic
    public static StreamCodec<RegistryFriendlyByteBuf, PlumeParticleData> streamCodec(ParticleType<PlumeParticleData> type) {
        return StreamCodec.unit(new PlumeParticleData(type));
    }

    private final ParticleType<PlumeParticleData> type;

    // Default constructor for initial registration references if needed
    public PlumeParticleData() {
        this.type = null;
    }

    public PlumeParticleData(ParticleType<PlumeParticleData> type) {
        this.type = type;
    }

    @Override
    @Nonnull
    public ParticleType<PlumeParticleData> getType() {
        return this.type;
    }

    @Override
    public SpriteParticleRegistration<PlumeParticleData> getMetaFactory() {
        return PlumeParticle.Factory::new;
    }

    @Override
    public MapCodec<PlumeParticleData> getCodec(ParticleType<PlumeParticleData> type) {
        return MapCodec.unit(() -> new PlumeParticleData(type));
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, PlumeParticleData> getStreamCodec() {
        return StreamCodec.unit(new PlumeParticleData(type));
    }
}