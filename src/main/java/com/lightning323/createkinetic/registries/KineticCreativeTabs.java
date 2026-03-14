package com.lightning323.createkinetic.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.lightning323.createkinetic.CreateKinetic.MOD_ID;
import static com.lightning323.createkinetic.CreateKinetic.REGISTRATE;

public class KineticCreativeTabs {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "createkinetic" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("createkinetic_tab",
            () -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT)
                    .title(Component.translatable("itemGroup." + MOD_ID + ".tab"))
                    .icon(() -> KineticItems.SHIP_TOTEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        //Registrate
                        REGISTRATE.getAll(Registries.ITEM)
                                .forEach(item -> output.accept(item.get()));
                    }).build());

    //For vanilla tabs
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
//            event.accept(SHIP_TOTEM.get());
//            event.accept(FREEZE_SHIP_TOTEM.get());
        }
    }
}
