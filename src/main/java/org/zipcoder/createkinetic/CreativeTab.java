package org.zipcoder.createkinetic;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static org.zipcoder.createkinetic.Createkinetic.MODID;
import static org.zipcoder.createkinetic.ModItems.SHIP_TOTEM;

public class CreativeTab {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "createkinetic" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a creative tab with the id "createkinetic:example_tab" for the example item, that is placed after the combat tab
    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("createkinetic_tab",
            () -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT)
                    .title(Component.translatable("itemGroup." + MODID + ".tab"))
                    .icon(() -> SHIP_TOTEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(SHIP_TOTEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                    }).build());

    //For vanilla tabs
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            //event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }
}
