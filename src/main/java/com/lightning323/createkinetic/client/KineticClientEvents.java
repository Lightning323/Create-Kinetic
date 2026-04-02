package com.lightning323.createkinetic.client;

import com.lightning323.createkinetic.blocks.sail.SailClothBlock;
import com.lightning323.createkinetic.registries.KineticBlocks;
import com.lightning323.createkinetic.registries.KineticKeybindings;
import com.lightning323.createkinetic.registries.KineticParticles;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.lightning323.createkinetic.CreateKinetic.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KineticClientEvents {



    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // In your ClientModEvents or Client Setup class
//        ItemBlockRenderTypes.setRenderLayer(KineticBlocks.RUDDER.get(), RenderType.cutout());
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KineticKeybindings.CONTROL_UP);
        event.register(KineticKeybindings.CONTROL_DOWN);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tintIndex) -> {
                    if (tintIndex == 0 && state.hasProperty(SailClothBlock.COLOR)) {
                        return state.getValue(SailClothBlock.COLOR).getFireworkColor();
                    }
                    return 0xFFFFFF;
                }, KineticBlocks.SAIL_CLOTH.get(), KineticBlocks.SAIL_MAGNET.get(),
                KineticBlocks.SAIL_WEIGHT.get());
    }

    @SubscribeEvent
    public static void onParticleFactoryRegistration(RegisterParticleProvidersEvent event) {
        KineticParticles.register(event);
    }

}