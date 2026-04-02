package com.lightning323.createkinetic.items.shipTotem;

import com.lightning323.createkinetic.CreateKinetic;
import com.lightning323.createkinetic.commands.KineticCommands;
import com.lightning323.createkinetic.utils.VSUtils;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.internal.world.VsiServerShipWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;

public class ShipTotemItem extends TotemItem {
    final boolean freezeShip;

    public ShipTotemItem(boolean freezeShip) {

        super((s, i) -> recoverShip(s, i, freezeShip));
        this.freezeShip = freezeShip;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createkinetic.shiptotem")
                    .withStyle(ChatFormatting.GRAY));
            if (this.freezeShip) {
                tooltip.add(Component.translatable("tooltip.createkinetic.shiptotem.freeze")
                        .withStyle(ChatFormatting.BLUE));
            }
        } else {
            tooltip.add(TooltipHelper.holdShift(FontHelper.Palette.STANDARD_CREATE, false));
        }
    }

    private static boolean recoverShip(ServerPlayer player, ItemStack i, boolean freezeShip) {

        String shipSlug = i.getHoverName().getString();
        if (shipSlug.isBlank() || !i.hasCustomHoverName()) {
            VsiServerShipWorld shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(player.getServer());
            Ship ship = VSUtils.getShipNearPlayer(shipObjectWorld, player);
            CreateKinetic.LOGGER.debug("Found ship: {}", ship);
            if (ship == null
                    || KineticCommands.renameShipTotem(player, player::sendSystemMessage, ship) != 1) {
                player.sendSystemMessage(Component.literal("You cant use it yet! Click the totem on an existing ship to rename it, or Rename the totem with ").append(
                        Component.literal("/ship totem <shipName>")
                                .withStyle(style -> style
                                        .withColor(ChatFormatting.AQUA) // color it differently
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ship totem "))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open command")))
                                )
                ));
            }
            return false;
        }

        CommandSourceStack source = player.createCommandSourceStack();
        Level level = player.level();
        int exit = VSUtils.recoverShip(source.getServer(), player, shipSlug);
        if (exit == 0) {
            player.sendSystemMessage(Component.literal("Teleport failed!"));
            return false;
        } else {
            player.sendSystemMessage(Component.literal("Teleport successful!"));

            if (freezeShip) {
                KineticCommands.setShipStatic(source, shipSlug, true);

                String command = "/ship unfreeze " + shipSlug;
                player.sendSystemMessage(
                        Component.literal("Ship has been frozen. Use ")
                                .append(
                                        Component.literal(command)
                                                .withStyle(style -> style
                                                        .withColor(ChatFormatting.AQUA) // color it differently
                                                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to run command")))
                                                )
                                )
                                .append(Component.literal(" to unfreeze."))
                );
            }
            return true;
        }
    }
}
