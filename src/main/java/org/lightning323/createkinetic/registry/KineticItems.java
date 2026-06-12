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
package org.lightning323.createkinetic.registry;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.KineticRegistrate;
import org.lightning323.createkinetic.content.items.SuspensionKeyItem;

public class KineticItems {
    private static final KineticRegistrate REGISTRATE = CreateKinetic.getRegistrate();

    public static final ItemEntry<Item> SMALL_SUSPENSION_TRACK = REGISTRATE.item("small_suspension_track", Item::new).register();
    public static final ItemEntry<Item> SMALL_TRACK_DRIVE_WHEEL = REGISTRATE.item("small_track_drive_wheel", Item::new).register();
    public static final ItemEntry<SuspensionKeyItem> SUSPENSION_KEY = REGISTRATE.item("suspension_key", SuspensionKeyItem::new).register();


    public static void init() {
    }
}

