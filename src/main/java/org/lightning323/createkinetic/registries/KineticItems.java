/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.ItemBuilder
 *  com.tterrag.registrate.util.entry.ItemEntry
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  dev.simulated_team.simulated.registrate.SimulatedRegistrate
 *  dev.simulated_team.simulated.registrate.simulated_tab.CreativeTabItemTransforms$VisibilityType
 *  net.minecraft.world.item.Item
 */
package org.lightning323.createkinetic.registries;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.KineticRegistrate;
import org.lightning323.createkinetic.utility.BurnableItem;

public class KineticItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateKinetic.ID);
    private static final KineticRegistrate REGISTRATE = CreateKinetic.getRegistrate();

    public static final ItemEntry<Item> SUSPENSION_TRACK =
            REGISTRATE.item("small_suspension_track", Item::new)
                    .model((ctx, prov) -> {})
                    .register();

    public static final ItemEntry<Item> TRACK_DRIVE_WHEEL =
            REGISTRATE.item("small_track_drive_wheel", Item::new)
                    .model((ctx, prov) -> {})
                    .register();

    public static final DeferredHolder<Item, BurnableItem> PINE_RESIN =
            ITEMS.register("pine_resin", () ->
                    new BurnableItem(new Item.Properties(), 1200));

    public static final DeferredHolder<Item, Item> TURPENTINE_BUCKET =
            ITEMS.register("turpentine_bucket", () ->
                    new net.minecraft.world.item.BucketItem(KineticFluids.TURPENTINE.get(),
                            new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET)
                                    .stacksTo(1)));

    public static final DeferredHolder<Item, Item> OXIDIZER_BUCKET = ITEMS.register("oxidizer_bucket", () ->
            new net.minecraft.world.item.BucketItem(KineticFluids.OXIDIZER.get(),
                    new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET)
                            .stacksTo(1)));

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}

