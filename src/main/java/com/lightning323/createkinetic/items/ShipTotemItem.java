package com.lightning323.createkinetic.items;

import com.lightning323.createkinetic.commands.ModCommands;
import com.lightning323.createkinetic.utils.VSUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.internal.world.VsiServerShipWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import static com.lightning323.createkinetic.commands.ModCommands.executeParsedCommandOP;

public class ShipTotemItem extends TotemItem {

    public ShipTotemItem(boolean freezeShip) {
        super((s, i) -> recoverShip(s, i, freezeShip));
    }

    private static boolean recoverShip(ServerPlayer player, ItemStack i, boolean freezeShip) {
        String shipSlug = i.getHoverName().getString();
        if (shipSlug.isBlank() || !i.hasCustomHoverName()) {
            VsiServerShipWorld shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(player.getServer());
            Ship raycastShip = VSUtils.getShipNearPlayer(shipObjectWorld, player);
            if (raycastShip == null || ModCommands.renameShipTotem(player, player::sendSystemMessage, raycastShip.getSlug()) != 1) {
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
        int exit = VSUtils.recoverShip(source.getServer(), player, shipSlug);
        if (exit == 0) {
            player.sendSystemMessage(Component.literal("Teleport failed!"));
            return false;
        } else {
            player.sendSystemMessage(Component.literal("Teleport successful!"));

            if (freezeShip) {
                executeParsedCommandOP(source, "vs set-static " + shipSlug + " true", false);
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
