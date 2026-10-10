package org.lightning323.createkinetic.particles.plume;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.lightning323.createkinetic.particles.ParticleTypes;

import java.util.ArrayList;
import java.util.List;

public class PlumeParticleData implements ParticleOptions, ICustomParticleDataWithSprite<PlumeParticleData> {
    private final List<ResourceLocation> overrideTextures;
    private final Integer overrideColor;

    public PlumeParticleData() {
        this(List.of(), null);
    }

    public PlumeParticleData(List<ResourceLocation> overrideTextures, Integer overrideColor) {
        this.overrideTextures = overrideTextures == null ? List.of() : List.copyOf(overrideTextures);
        this.overrideColor = overrideColor;
    }

    public List<ResourceLocation> overrideTextures() {
        return overrideTextures;
    }

    public Integer overrideColor() {
        return overrideColor;
    }

    @Override
    public ParticleType<?> getType(){
        return ParticleTypes.getPlumeType();
    }

    public MapCodec<PlumeParticleData> getCodec(ParticleType<PlumeParticleData> type) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.listOf().optionalFieldOf("override_textures", List.of()).forGetter(PlumeParticleData::overrideTextures),
            Codec.INT.optionalFieldOf("override_color").forGetter(data -> java.util.Optional.ofNullable(data.overrideColor))
        ).apply(instance, (textures, color) -> new PlumeParticleData(textures, color.orElse(null))));
	}

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, PlumeParticleData> getStreamCodec() {
        return StreamCodec.of((buf, data) -> {
            buf.writeCollection(data.overrideTextures, (b, rl) -> b.writeResourceLocation(rl));
            buf.writeBoolean(data.overrideColor != null);
            if (data.overrideColor != null) {
                buf.writeInt(data.overrideColor);
            }
        }, buf -> {
            List<ResourceLocation> textures = buf.readCollection(ArrayList::new, b -> b.readResourceLocation());
            Integer color = buf.readBoolean() ? buf.readInt() : null;
            return new PlumeParticleData(textures, color);
        });
    }

    @Override
	public SpriteParticleRegistration<PlumeParticleData> getMetaFactory() {
        return PlumeParticle.Factory::new;
	}
}
