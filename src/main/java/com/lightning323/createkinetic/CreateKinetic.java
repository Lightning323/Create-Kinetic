package com.lightning323.createkinetic;

import com.lightning323.createkinetic.registries.*;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.mod.api.ValkyrienSkies;

import static com.lightning323.createkinetic.registries.KineticCreativeTabs.CREATIVE_MODE_TABS;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateKinetic.MOD_ID)
public class CreateKinetic {
    public static final String MOD_ID = "createkinetic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    public CreateKinetic() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(KineticCreativeTabs::addCreative);
        modEventBus.addListener(this::onRegister);

        KineticBlocks.register();
        KineticItems.register();
        KineticMenus.register();
        KineticBlockEntities.register();
        KineticPackets.registerPackets();

        //Register ship control
        ValkyrienSkies.api().registerAttachment(ValkyrienSkies.api()
                .newAttachmentRegistrationBuilder(KineticShipControl.class).build()
        );
        ValkyrienSkies.api().getShipLoadEvent().on(ship -> {
            KineticShipControl.getOrAddController(null, ship.getShip());
        });

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, KineticConfig.SPEC);
        REGISTRATE.registerEventListeners(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        //TODO: Come up with a better way to handle translation keys
        REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            KineticEnglishTranslationProvider.addTranslations(provider);
        });
        MinecraftForge.EVENT_BUS.register(this);
    }

//    public static final KRedstoneLinkNetworkHandler REDSTONE_LINK_NETWORK_HANDLER = new KRedstoneLinkNetworkHandler();

    public void onRegister(final RegisterEvent event) {
        KineticPartialModels.init();
        KineticContraptions.init();
        KineticSpriteShifts.init();
        KineticParticles.init();
    }

    //Datagen event
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
    }


    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
