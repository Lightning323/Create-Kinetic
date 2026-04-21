package org.lightning323.createkinetic.registries;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.createkinetic.CreateKinetic;

import static org.lightning323.createkinetic.CreateKinetic.REGISTRATE;


public class KineticCreativeTabs {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "createkinetic" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateKinetic.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BASE_CREATIVE_TAB = CREATIVE_MODE_TABS.register("createkinetic_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .title(Component.translatable("itemGroup." + CreateKinetic.MOD_ID + ".tab"))
                    .icon(() -> KineticItems.SHIP_TOTEM.get().getDefaultInstance())
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(KineticItems.SHIP_TOTEM.get());
                        output.accept(KineticItems.FREEZE_SHIP_TOTEM.get());
                        output.accept(KineticItems.THRUSTER_ITEM.get());
                    })
                    .build());


    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
