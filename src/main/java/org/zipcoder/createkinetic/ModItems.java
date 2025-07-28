package org.zipcoder.createkinetic;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.zipcoder.createkinetic.items.TotemItem;

import java.util.function.Supplier;

import static org.zipcoder.createkinetic.Createkinetic.MODID;
import static org.zipcoder.createkinetic.ModCommands.executeParsedCommandOP;

public class ModItems {
    // Create a Deferred Register to hold Items which will all be registered under the "createkinetic" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);


    //Ender chests
    public static Item IRON_CHEST, COPPER_CHEST, GOLD_CHEST, LAPIS_CHEST, REDSTONE_CHEST, EMERALD_CHEST,DIAMOND_CHEST,  NETHERITE_CHEST;

    public static final RegistryObject<Item> SHIP_TOTEM = ITEMS.register("ship_totem",
            () -> new TotemItem((s, i) -> recoverShip(s, i, false))
    );

    public static final RegistryObject<Item> FREEZE_SHIP_TOTEM = ITEMS.register("freeze_ship_totem",
            () -> new TotemItem((s, i) -> recoverShip(s, i, true))
    );

    private static boolean recoverShip(ServerPlayer player, ItemStack i, boolean freezeShip) {
        String shipSlug = i.getHoverName().getString();
        if (shipSlug.isBlank() || !i.hasCustomHoverName()) {
            player.sendSystemMessage(Component.literal("Please rename this totem to the slug of the ship you want to recover."));
            return false;
        }

        CommandSourceStack source = player.createCommandSourceStack();
        int exit = ModCommands.recoverShip(player, shipSlug);
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
                                                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))
                                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy command")))
                                                )
                                )
                                .append(Component.literal(" to unfreeze."))
                );
            }
            return true;
        }
    }

}
