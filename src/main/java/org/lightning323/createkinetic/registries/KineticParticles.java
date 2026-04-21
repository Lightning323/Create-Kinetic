package org.lightning323.createkinetic.registries;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.createkinetic.CreateKinetic;

import java.util.function.Supplier;

public class KineticParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, CreateKinetic.MOD_ID);

    public static final Supplier<SimpleParticleType> PLUME = PARTICLE_TYPES.register("plume_particle", () -> new SimpleParticleType(true));

    public static void register(IEventBus modEventBus) {
        PARTICLE_TYPES.register(modEventBus);
    }

}