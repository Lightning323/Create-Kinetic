package org.lightning323.createkinetic.registries;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.utility.BurnableItem;

//import dev.propulsionteam.propulsionsimulated.content.cable.CableSpoolItem;

public class PropulsionItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateKinetic.ID);

    public static final DeferredHolder<Item, BurnableItem> PINE_RESIN =
        ITEMS.register("pine_resin", () -> new BurnableItem(new Item.Properties(), 1200));
    public static final DeferredHolder<Item, Item> TURPENTINE_BUCKET =
        ITEMS.register("turpentine_bucket", () -> new net.minecraft.world.item.BucketItem(PropulsionFluids.TURPENTINE.get(), new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1)));

    public static final DeferredHolder<Item, Item> OXIDIZER_BUCKET = ITEMS.register("oxidizer_bucket", () -> new net.minecraft.world.item.BucketItem(PropulsionFluids.OXIDIZER.get(), new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1)));

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
