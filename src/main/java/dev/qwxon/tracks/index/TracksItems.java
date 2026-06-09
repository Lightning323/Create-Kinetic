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
package dev.qwxon.tracks.index;

import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.qwxon.tracks.content.items.SuspensionKeyItem;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import dev.simulated_team.simulated.registrate.simulated_tab.CreativeTabItemTransforms;
import net.minecraft.world.item.Item;
import org.lightning323.createkinetic.CreateKinetic;

public class TracksItems {
    private static final SimulatedRegistrate REGISTRATE = CreateKinetic.getRegistrate();
    public static final ItemEntry<Item> SMALL_SUSPENSION_TRACK = REGISTRATE.item("small_suspension_track", Item::new).register();
    public static final ItemEntry<Item> SUSPENSION_TRACK = ((ItemBuilder)REGISTRATE.item("suspension_track", Item::new).transform((NonNullFunction)CreativeTabItemTransforms.VisibilityType.INVISIBLE.applyItem())).register();
    public static final ItemEntry<Item> LARGE_SUSPENSION_TRACK = ((ItemBuilder)REGISTRATE.item("large_suspension_track", Item::new).transform((NonNullFunction)CreativeTabItemTransforms.VisibilityType.INVISIBLE.applyItem())).register();
    public static final ItemEntry<Item> SMALL_TRACK_DRIVE_WHEEL = REGISTRATE.item("small_track_drive_wheel", Item::new).register();
    public static final ItemEntry<Item> TRACK_DRIVE_WHEEL = ((ItemBuilder)REGISTRATE.item("track_drive_wheel", Item::new).transform((NonNullFunction)CreativeTabItemTransforms.VisibilityType.INVISIBLE.applyItem())).register();
    public static final ItemEntry<Item> LARGE_TRACK_DRIVE_WHEEL = ((ItemBuilder)REGISTRATE.item("large_track_drive_wheel", Item::new).transform((NonNullFunction)CreativeTabItemTransforms.VisibilityType.INVISIBLE.applyItem())).register();
    public static final ItemEntry<SuspensionKeyItem> SUSPENSION_KEY = REGISTRATE.item("suspension_key", SuspensionKeyItem::new).register();

    public static void init() {
    }
}

