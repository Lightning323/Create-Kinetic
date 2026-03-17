package com.lightning323.createkinetic.client;

import com.lightning323.createkinetic.blocks.redstone.smartLink.KLinkRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.lightning323.createkinetic.CreateKinetic.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT) // Defaults to Bus.FORGE
public class ForgeBusClientEvents {
    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        // Only run on one phase (usually END) to avoid double-ticking
        if (event.phase == TickEvent.Phase.END && isGameActive()) {
            KLinkRenderer.tick();
        }
    }
}