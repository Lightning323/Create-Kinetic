package org.lightning323.createkinetic.items.shipTotem;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ShipTotemItem extends TotemItem {
    final boolean freezeShip;

    public ShipTotemItem(boolean freezeShip) {
        super((s, i) -> recoverShip(s, i, freezeShip));
        this.freezeShip = freezeShip;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        // Check for shift key
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createkinetic.shiptotem")
                    .withStyle(ChatFormatting.GRAY));

            if (this.freezeShip) {
                tooltip.add(Component.translatable("tooltip.createkinetic.shiptotem.freeze")
                        .withStyle(ChatFormatting.BLUE));
            }
        }
    }

    private static boolean recoverShip(ServerPlayer player, ItemStack i, boolean freezeShip) {
        return false;
    }
}
