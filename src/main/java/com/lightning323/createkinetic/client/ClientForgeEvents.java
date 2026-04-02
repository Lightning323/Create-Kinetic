package com.lightning323.createkinetic.client;

import com.lightning323.createkinetic.blocks.shipHelm.HelmControlData;
import com.lightning323.createkinetic.blocks.shipHelm.HelmControlPacket;
import com.lightning323.createkinetic.registries.KineticKeybindings;
import com.lightning323.createkinetic.registries.KineticPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.lightning323.createkinetic.CreateKinetic.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

    static final HelmControlData lastControlData = new HelmControlData();
    static final HelmControlData controlData = new HelmControlData();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            var mc = Minecraft.getInstance();
            if (mc.player == null) return;

            if (KineticClientData.getControllingBlockEntity() != null) {
                // Access the player's movement input
                Input input = mc.player.input;

                float elevatorImpulse = 0;
                if (KineticKeybindings.CONTROL_UP.isDown()) {
                    elevatorImpulse = 1;
                } else if (KineticKeybindings.CONTROL_DOWN.isDown()) {
                    elevatorImpulse = -1;
                }

                controlData.forwardImpulse = input.forwardImpulse;
                controlData.leftImpulse = input.leftImpulse;
                controlData.upImpulse = elevatorImpulse;
                controlData.sprintOn = mc.player.isSprinting();

                if (!lastControlData.equals(controlData)) {
                    lastControlData.set(controlData);
                    KineticPackets.HELM_CONTROL.sendToServer(
                            new HelmControlPacket(KineticClientData.getControllingBlockPos(), controlData));
                }
            }
        }
    }
}