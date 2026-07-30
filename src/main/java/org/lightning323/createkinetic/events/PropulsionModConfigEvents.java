package org.lightning323.createkinetic.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.config.KineticConfig;
import org.lightning323.createkinetic.content.thruster.SolidThrusterFuelManager;
import org.lightning323.createkinetic.content.thruster.ThrusterFuelManager;

@EventBusSubscriber(modid = CreateKinetic.ID, bus = EventBusSubscriber.Bus.MOD)
public final class PropulsionModConfigEvents {

    private PropulsionModConfigEvents() {}

    @SubscribeEvent
    public static void onCommonConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() != KineticConfig.COMMON_SPEC) {
            return;
        }
        ThrusterFuelManager.rebuildThrusterFuelsAfterCommonConfigReload();
        SolidThrusterFuelManager.rebuildAfterCommonConfigReload();
    }
}
