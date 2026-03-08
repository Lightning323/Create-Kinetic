package com.lightning323.createkinetic;

import com.lightning323.createkinetic.datagen.ModBlockStateProvider;
import com.lightning323.createkinetic.registries.BlockRegistry;
import com.lightning323.createkinetic.registries.CreativeTabRegistry;
import com.lightning323.createkinetic.registries.ItemRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
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
import org.slf4j.Logger;
import com.lightning323.createkinetic.network.NetworkHandler;

import static com.lightning323.createkinetic.registries.CreativeTabRegistry.CREATIVE_MODE_TABS;
import static com.lightning323.createkinetic.registries.BlockRegistry.BLOCKS;
import static com.lightning323.createkinetic.registries.ItemRegistry.ITEMS;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Createkinetic.MOD_ID)
public class Createkinetic {
    public static final String MOD_ID = "createkinetic";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Createkinetic() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(CreativeTabRegistry::addCreative);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, KineticConfig.SPEC);

        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    //Datagen event
    public void gatherData(GatherDataEvent event) {
        LOGGER.info("Datagen started!");
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
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
