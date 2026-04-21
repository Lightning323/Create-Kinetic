package org.lightning323.createkinetic.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.particles.plume.PlumeParticle;
import org.lightning323.createkinetic.registries.KineticParticles;

@EventBusSubscriber(modid = CreateKinetic.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

//    @SubscribeEvent
//    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
//        event.registerSpriteSet(KineticParticles.PLUME.get(), new PlumeParticle.Provider::new);
//    }
}