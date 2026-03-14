package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.items.ShipTotemItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import static com.lightning323.createkinetic.CreateKinetic.REGISTRATE;

public class KineticItems {

    public static final ItemEntry<ShipTotemItem> SHIP_TOTEM = REGISTRATE.item("ship_totem",
                    p -> new ShipTotemItem(false))
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .register();

    public static final ItemEntry<ShipTotemItem> FREEZE_SHIP_TOTEM = REGISTRATE.item("freeze_ship_totem",
                    p -> new ShipTotemItem(true))
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .register();

    public static final ItemEntry<Item> STEERING_MECHANISM = REGISTRATE
            .item("steering_mechanism", Item::new)
            .register();

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_STEERING_MECHANISM = REGISTRATE
            .item("incomplete_steering_mechanism", SequencedAssemblyItem::new)
            .model((c, p) -> p.withExistingParent(c.getName(), "item/generated")
                    .texture("layer0", p.modLoc("item/" + c.getName())))
            .register();

    public static void register() {

    }
}
