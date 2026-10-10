package org.lightning323.createkinetic.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.createkinetic.CreateKinetic;

public class PropulsionSoundEvents {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
        DeferredRegister.create(Registries.SOUND_EVENT, CreateKinetic.ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> THRUSTER_LOOP = SOUND_EVENTS.register(
        "thruster_loop",
        () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "thruster_loop"))
    );

    public static void register(IEventBus modBus) {
        SOUND_EVENTS.register(modBus);
    }
}
