package org.lightning323.create_kinetic;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.create_kinetic.registry.KineticBlockEntities;
import org.lightning323.create_kinetic.registry.KineticBlocks;
import org.lightning323.create_kinetic.registry.KineticSounds;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateKinetic.MODID)
public class CreateKinetic {
    public static final String MODID = "create_kinetic";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public CreateKinetic(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);


        BLOCKS.register(modEventBus);
        KineticSounds.SOUND_EVENTS.register(modEventBus);
        KineticBlocks.registerBlockItems(ITEMS);
        KineticBlocks.BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
//        NeoForge.EVENT_BUS.register(this); Cant run this unless we have methods with @SubscribeEvent
        KineticBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
}
