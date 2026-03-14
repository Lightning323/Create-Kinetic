package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.CreateKinetic;
import com.lightning323.createkinetic.client.WindParticle;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

public class KineticParticles {
    public static final RegistryEntry<SimpleParticleType> WIND = CreateKinetic.REGISTRATE
            .simple("wind", Registries.PARTICLE_TYPE, () -> new SimpleParticleType(false));

    public static void init(){}

    public static void register(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(KineticParticles.WIND.get(), WindParticle.Factory::new);
    }

}
