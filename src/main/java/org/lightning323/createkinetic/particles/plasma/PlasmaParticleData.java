package org.lightning323.createkinetic.particles.plasma;

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

public class PlasmaParticleData implements ParticleOptions, ICustomParticleDataWithSprite<PlasmaParticleData> {
    private final List<ResourceLocation> overrideTextures;
    private final Integer overrideColor;

    public PlasmaParticleData() {
        this(List.of(), null);
    }

    public PlasmaParticleData(List<ResourceLocation> overrideTextures, Integer overrideColor) {
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
        return ParticleTypes.getPlasmaType();
    }

    public MapCodec<PlasmaParticleData> getCodec(ParticleType<PlasmaParticleData> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.listOf().optionalFieldOf("override_textures", List.of()).forGetter(PlasmaParticleData::overrideTextures),
            Codec.INT.optionalFieldOf("override_color").forGetter(data -> java.util.Optional.ofNullable(data.overrideColor))
        ).apply(instance, (textures, color) -> new PlasmaParticleData(textures, color.orElse(null))));
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, PlasmaParticleData> getStreamCodec() {
        return StreamCodec.of((buf, data) -> {
            buf.writeCollection(data.overrideTextures, (b, rl) -> b.writeResourceLocation(rl));
            buf.writeBoolean(data.overrideColor != null);
            if (data.overrideColor != null) {
                buf.writeInt(data.overrideColor);
            }
        }, buf -> {
            List<ResourceLocation> textures = buf.readCollection(ArrayList::new, b -> b.readResourceLocation());
            Integer color = buf.readBoolean() ? buf.readInt() : null;
            return new PlasmaParticleData(textures, color);
        });
    }

    @Override
    public SpriteParticleRegistration<PlasmaParticleData> getMetaFactory() {
        return PlasmaParticle.Factory::new;
    }
}
