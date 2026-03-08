package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.items.ShipTotemItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.lightning323.createkinetic.Createkinetic.MOD_ID;

public class ItemRegistry {
    // Create a Deferred Register to hold Items which will all be registered under the "createkinetic" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static void register(IEventBus modEventBus){
        ITEMS.register(modEventBus);
    }

    public static final RegistryObject<Item> SHIP_TOTEM = ITEMS.register("ship_totem",
            () -> new ShipTotemItem(false));

    public static final RegistryObject<Item> FREEZE_SHIP_TOTEM = ITEMS.register("freeze_ship_totem",
            () -> new ShipTotemItem(true));
}
