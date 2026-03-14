package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.items.ShipTotemItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Rarity;

import static com.lightning323.createkinetic.Createkinetic.REGISTRATE;

public class KineticItems {

    public static final ItemEntry<ShipTotemItem> SHIP_TOTEM = REGISTRATE.item("ship_totem",
                    p -> new ShipTotemItem(false))
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .register();

    public static final ItemEntry<ShipTotemItem> FREEZE_SHIP_TOTEM = REGISTRATE.item("freeze_ship_totem",
                    p -> new ShipTotemItem(true))
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .register();

    public static void register() {

    }
}
