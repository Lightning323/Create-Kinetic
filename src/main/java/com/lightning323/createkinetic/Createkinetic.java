package com.lightning323.createkinetic;

import com.lightning323.createkinetic.registries.*;
import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import com.lightning323.createkinetic.network.NetworkHandler;

import static com.lightning323.createkinetic.registries.KineticCreativeTabs.CREATIVE_MODE_TABS;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Createkinetic.MOD_ID)
public class Createkinetic {
    public static final String MOD_ID = "createkinetic";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    public Createkinetic() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(KineticCreativeTabs::addCreative);
        modEventBus.addListener(this::onRegister);

        KineticItems.register();
        KineticBlockEntities.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, KineticConfig.SPEC);
        REGISTRATE.registerEventListeners(modEventBus);


        CREATIVE_MODE_TABS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onRegister(final RegisterEvent event){
        KineticPartialModels.init();
        KineticContraptions.init();
    }

    //Datagen event
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.registerMessages();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
