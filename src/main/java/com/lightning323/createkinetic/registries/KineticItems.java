package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.items.ShipTotemItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

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

    public static void shiftForTooltip(List<Component> tooltip, Component... addedTooltip) {
        if (Screen.hasShiftDown()) {
            // Detailed description
            tooltip.addAll(List.of(addedTooltip));
        } else {
            tooltip.add(TooltipHelper.holdShift(FontHelper.Palette.STANDARD_CREATE, false));
        }
    }

    public static void register() {

    }
}
