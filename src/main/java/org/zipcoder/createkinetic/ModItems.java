package org.zipcoder.createkinetic;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static org.zipcoder.createkinetic.Createkinetic.MODID;

public class ModItems {
    // Create a Deferred Register to hold Items which will all be registered under the "createkinetic" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);


    public static final RegistryObject<Item> SHIP_TOTEM = ITEMS.register("ship_totem", () ->
            new Item(new Item.Properties().stacksTo(1)));

}
