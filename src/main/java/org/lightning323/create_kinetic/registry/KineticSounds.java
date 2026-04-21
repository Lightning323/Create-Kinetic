package org.lightning323.create_kinetic.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

import static org.lightning323.create_kinetic.CreateKinetic.MODID;

public class KineticSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, MODID);

//    public static final Supplier<SoundEvent> THRUSTER =
//            register("thruster");
//
//    private static Supplier<SoundEvent> register(String name) {
//        return SOUND_EVENTS.register(name,
//                () -> SoundEvent.createVariableRangeEvent(
//                        ResourceLocation.fromNamespaceAndPath(MODID, name)
//                )
//        );
//    }
}