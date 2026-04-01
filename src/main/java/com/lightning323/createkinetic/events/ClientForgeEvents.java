package com.lightning323.createkinetic.events;

import com.lightning323.createkinetic.registries.KineticKeybindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.lightning323.createkinetic.CreateKinetic.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

    public static float control_ElevatorImpulse;
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) { // Only check once per tick
            if (KineticKeybindings.CONTROL_UP.isDown()) {
                control_ElevatorImpulse = 1;
            } else if (KineticKeybindings.CONTROL_DOWN.isDown()) {
                control_ElevatorImpulse = -1;
            } else {
                control_ElevatorImpulse = 0;
            }
        }
    }
}