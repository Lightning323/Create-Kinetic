package org.lightning323.createkinetic.registries;

import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.items.shipTotem.ShipTotemItem;

import java.util.List;

public class KineticItems {
    // Create the register
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, CreateKinetic.MOD_ID);

    // Define your items
    public static final DeferredHolder<Item, ShipTotemItem> SHIP_TOTEM = ITEMS.register("ship_totem",
            () -> new ShipTotemItem(false));

    public static final DeferredHolder<Item, ShipTotemItem> FREEZE_SHIP_TOTEM = ITEMS.register("freeze_ship_totem",
            () -> new ShipTotemItem(true));

    public static final DeferredHolder<Item, BlockItem> THRUSTER_ITEM = KineticItems.ITEMS.register("thruster",
            () -> new BlockItem(KineticBlocks.THRUSTER.get(), new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                    KineticItems.shiftForTooltip(tooltip,
                            Component.translatable("tooltip.createkinetic.thruster").withStyle(ChatFormatting.GRAY));
                }
            }
    );

    public static void register(net.neoforged.bus.api.IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static void shiftForTooltip(List<Component> tooltip, Component... addedTooltip) {
        if (Screen.hasShiftDown()) {
            // Detailed description
            tooltip.addAll(List.of(addedTooltip));
        } else {
            tooltip.add(TooltipHelper.holdShift(FontHelper.Palette.STANDARD_CREATE, false));
        }
    }
}