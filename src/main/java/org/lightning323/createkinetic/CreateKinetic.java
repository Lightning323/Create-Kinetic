package org.lightning323.createkinetic;

import com.mojang.logging.LogUtils;
import com.simibubi.create.*;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.compat.curios.Curios;
import com.simibubi.create.compat.inventorySorter.InventorySorterCompat;
import com.simibubi.create.content.decoration.palettes.AllPaletteBlocks;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.content.logistics.packagePort.AllPackagePortTargetTypes;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.recipe.AllIngredients;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.data.CreateDatagen;
import com.simibubi.create.infrastructure.worldgen.AllFeatures;
import com.simibubi.create.infrastructure.worldgen.AllPlacementModifiers;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.lightning323.createkinetic.registries.*;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateKinetic.MOD_ID)
public class CreateKinetic {
    public static final String MOD_ID = "createkinetic";
    private static final Logger LOGGER = LogUtils.getLogger();


    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    public CreateKinetic(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onRegister);

        onCtor(modEventBus, modContainer);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }


    public static void onCtor(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        KineticBlocks.register();
        KineticBlockEntitiyTypes.register();
        KineticItems.register(modEventBus);
        KineticCreativeTabs.register(modEventBus);
////        KineticMenus.register();
////        KineticPackets.registerPackets();
    }

    public void onRegister(final RegisterEvent event) {
//        KineticPartialModels.init();
//        KineticContraptions.init();
//        KineticSpriteShifts.init();
//        KineticParticles.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
}
