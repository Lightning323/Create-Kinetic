package org.lightning323.createkinetic.particles;

import com.simibubi.create.foundation.particle.ICustomParticleData;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.particles.ion.IonParticleData;
import org.lightning323.createkinetic.particles.plasma.PlasmaParticleData;
import org.lightning323.createkinetic.particles.plume.PlumeParticleData;

import java.util.function.Supplier;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = CreateKinetic.ID, bus = Bus.MOD, value = Dist.CLIENT)
public enum ParticleTypes {
    PLUME(PlumeParticleData::new),
    PLASMA(PlasmaParticleData::new),
    ION(IonParticleData::new);

    private final ParticleEntry<?> entry;

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        ParticleTypes.registerFactories(event);
    }

    <D extends ParticleOptions> ParticleTypes(Supplier<? extends ICustomParticleData<D>> typeFactory) {
        String name = CreateLang.asId(name());
        entry = new ParticleEntry<>(name, typeFactory);
    }

    public static ParticleType<?> getPlumeType() { return PLUME.get(); }
    public static ParticleType<?> getPlasmaType() { return PLASMA.get(); }
    public static ParticleType<?> getIonType() { return ION.get(); }

    public static void register(IEventBus modEventBus){
        ParticleEntry.REGISTER.register(modEventBus);
    }

    public static void registerFactories(RegisterParticleProvidersEvent event) {
        for (ParticleTypes particle : values()) 
            particle.entry.registerFactory(event);
    }

    public ParticleType<?> get() {
        return entry.object.get();
    }

    public String parameter() {
        return entry.name;
    }

    private static class ParticleEntry<D extends ParticleOptions> {
        private static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, CreateKinetic.ID);
        private final String name;
        private final Supplier<? extends ICustomParticleData<D>> typeFactory;
        private final DeferredHolder<ParticleType<?>, ParticleType<D>> object;

        public ParticleEntry(String name, Supplier<? extends ICustomParticleData<D>> typeFactory) {
            this.name = name; this.typeFactory = typeFactory;
            object = REGISTER.register(name, () -> this.typeFactory.get().createType());
        }

        public void registerFactory(RegisterParticleProvidersEvent event){
            typeFactory.get().register(object.get(), event);
        }
    }
}
