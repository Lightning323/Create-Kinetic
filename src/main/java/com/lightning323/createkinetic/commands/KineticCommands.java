package com.lightning323.createkinetic.commands;

import com.lightning323.createkinetic.items.ShipTotemItem;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.internal.world.VsiServerShipWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import com.lightning323.createkinetic.utils.VSUtils;
//import org.valkyrienskies.core.api.ships.*;
////import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
//import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.function.Consumer;

import static net.minecraft.commands.Commands.argument;

/**
 * Iteration over all ships
 * TODO: If ship number exceeds 5000, we can use  shipObjectWorld.getAllShips().getByChunkPos(x,y,dimension) to get 10x10 chunk area around the player
 * EVERY ship must have a unique slug, and so we really do have to iterate over EVERY one to prevent duplicates.
 * VS2 probably assumes you wont have over 5000 ships in a minecraft world anyway
 */
//https://github.com/ValkyrienSkies/Valkyrien-Skies-2/blob/42e49defd5f398f1b1e1a952d56a0a4407373e31/common/src/main/kotlin/org/valkyrienskies/mod/common/command/VSCommands.kt#L40
//https://github.com/ValkyrienSkies/Valkyrien-Skies-2/blob/25db12ab7eff4d2813b7d1d8b0553e6e7f2e0fc3/common/src/main/kotlin/org/valkyrienskies/mod/common/command/VSCommands.kt#L263
@Mod.EventBusSubscriber
public class KineticCommands {
    static final int RENAME_DISTANCE = 20;

    public static int executeParsedCommandOP(CommandSourceStack originalSource, String command, boolean redirectOutput) {
        MinecraftServer server = originalSource.getServer();
        var dispatcher = server.getCommands().getDispatcher();

        // Remove leading slash
        if (command.startsWith("/")) {
            command = command.substring(1);
        }


        try {
            CommandSourceStack serverSource;

            if (redirectOutput) {
                serverSource = new CommandSourceStack(
                        originalSource.getPlayer(), // entity
                        originalSource.getPlayer().position(), // position
                        originalSource.getPlayer().getRotationVector(), // rotation

                        server.getLevel(originalSource.getPlayer().level().dimension()).getServer()
                                .getLevel(originalSource.getPlayer().level().dimension()), // server level access

                        4, // permission level (OP)
                        originalSource.getPlayer().getName().getString(), // name
                        originalSource.getPlayer().getDisplayName(), // display name
                        server, // server
                        originalSource.getPlayer() // entity again
                ).withPermission(4)
                        .withSuppressedOutput(); // ensure messages show
            } else {
                serverSource = server.createCommandSourceStack()
                        .withPermission(4) // Full OP level
                        .withSuppressedOutput();
            }

            ParseResults<CommandSourceStack> parseResults = dispatcher.parse(command, serverSource);
            return dispatcher.execute(parseResults);
        } catch (CommandSyntaxException e) {
            originalSource.sendFailure(Component.literal("Error executing command: " + e.getMessage()));
            return 0;
        } catch (Exception e) {
            originalSource.sendFailure(Component.literal("Error executing command: " + e.getMessage()));
            return 0;
        }
    }

    public static int executeParsedCommand(CommandSourceStack source, String command) {
        // Use the server's command dispatcher
        MinecraftServer server = source.getServer();
        var dispatcher = server.getCommands().getDispatcher();

        // Parse the command string (if a leading slash exists, remove it)
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        ParseResults<CommandSourceStack> parseResults = dispatcher.parse(command, source);
        try {
            // Execute the parsed command and return the result
            return dispatcher.execute(parseResults);
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("Error executing command: " + e.getMessage()));
            return 0;
        }
    }


    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        /**
         * OPERATOR COMMANDS
         */
        dispatcher.register(Commands.literal("vs")
                .requires(source -> source.hasPermission(2)) // Only players with permission level 2 or higher see this command
                .then(Commands.literal("deletemassless").requires(source -> source.hasPermission(2)).executes(context -> {
                    VSUtils.deleteMasslessShips(context);
                    return Command.SINGLE_SUCCESS;
                })).then(Commands.literal("total")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                    VSUtils.listTotalShips(context,false);
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("list")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            VSUtils.listTotalShips(context,true);
                            return Command.SINGLE_SUCCESS;
                        })));

        /**
         * NON-OPERATOR COMMANDS
         */


        dispatcher.register(Commands.literal("ship")
                .then(Commands.literal("totem")
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("name", StringArgumentType.word())
                                .suggests(VSUtils.shipSlugSuggestions(true, 50))//ShipArgument.Companion.ships()
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    String newName = StringArgumentType.getString(ctx, "name");
                                    return renameShipTotem(player, (message) -> {
                                        ctx.getSource().sendSystemMessage(message);
                                    }, newName);
                                })))
                .then(Commands.literal("this")
                        .executes(ctx -> {
                            VsiServerShipWorld shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(ctx.getSource().getServer());
                            Ship ship = VSUtils.getShipNearPlayer(shipObjectWorld, ctx.getSource().getPlayerOrException());
                            System.out.println("ship: " + ship);
                            if (ship == null) {
                                ctx.getSource().sendFailure(Component.literal("No ship found"));
                                return 0;
                            } else {
                                ctx.getSource().sendSystemMessage(
                                        Component.literal("Found ship: \"" + ship.getSlug() + "\"")
                                                .withStyle(style -> style
                                                        .withColor(ChatFormatting.AQUA)
                                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ship.getSlug() == null ? "" : ship.getSlug()))
                                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy ship name")))
                                                )
                                );
                                return Command.SINGLE_SUCCESS;
                            }
                        })
                )
                .then(Commands.literal("rename")
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("old", StringArgumentType.word())
                                .suggests(VSUtils.shipSlugSuggestions(false, RENAME_DISTANCE))//ShipArgument.Companion.ships()
                                .then(argument("new", StringArgumentType.word()) // /neutron rename <old> <new>
                                        .executes(ctx -> {
                                            String oldSlug = StringArgumentType.getString(ctx, "old");
                                            String newSlug = StringArgumentType.getString(ctx, "new");

                                            ServerShip foundShip = null;
                                            Vec3 playerPos = ctx.getSource().getPlayer().getEyePosition();
                                            VsiServerShipWorld shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(ctx.getSource().getServer());

                                            for (ServerShip ship : shipObjectWorld.getAllShips()) {
                                                if (ship != null && ship.getTransform().getPositionInWorld().distance(playerPos.x, playerPos.y, playerPos.z) < RENAME_DISTANCE && ship.getSlug() != null) {
                                                    if (ship.getSlug().equals(oldSlug)) {
                                                        foundShip = ship;
                                                    } else if (ship.getSlug().equalsIgnoreCase(newSlug)) {
                                                        ctx.getSource().sendSystemMessage(
                                                                Component.literal("That ship name already exists!"));
                                                        return 0;
                                                    }
                                                }
                                            }
                                            if (foundShip == null) {
                                                ctx.getSource().sendFailure(Component.literal("No ship found nearby"));
                                                return 0;
                                            }
                                            foundShip.setSlug(newSlug);

                                            if (foundShip.getSlug().equals(newSlug)) {
                                                ctx.getSource().sendSystemMessage(
                                                        Component.literal("Renamed ship: \"" + oldSlug + "\" to \"" + newSlug + "\""));
                                                return 1;
                                            } else {
                                                ctx.getSource().sendSystemMessage(
                                                        Component.literal("Failed to rename ship: \"" + oldSlug + "\" to \"" + newSlug + "\""));
                                                return 0;
                                            }
                                        })))
                )
                .then(Commands.literal("recover")
                        .requires(source -> source.hasPermission(2))
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("ship", StringArgumentType.word())
                                .suggests(VSUtils.shipSlugSuggestions(true, -1))
                                .executes(ctx -> {
                                    String shipSlug = StringArgumentType.getString(ctx, "ship");
                                    return VSUtils.recoverShip(ctx.getSource().getServer(), ctx.getSource().getPlayer(), shipSlug);
                                }))
                )
                .then(Commands.literal("freeze")
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("ship", StringArgumentType.word())
                                .suggests(VSUtils.shipSlugSuggestions(false, 50))//ShipArgument.Companion.ships()
                                .executes(ctx -> {
                                    String shipSlug = StringArgumentType.getString(ctx, "ship");
                                    return setShipStatic(ctx.getSource(), shipSlug, true) ? Command.SINGLE_SUCCESS : 1;
                                }))
                )
                .then(Commands.literal("unfreeze")
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("ship", StringArgumentType.word())
                                .suggests(VSUtils.shipSlugSuggestions(false, 50))//ShipArgument.Companion.ships()
                                .executes(ctx -> {
                                    String shipSlug = StringArgumentType.getString(ctx, "ship");
                                    return setShipStatic(ctx.getSource(), shipSlug, false) ? Command.SINGLE_SUCCESS : 1;
                                }))
                )
        );
    }

    private static boolean setShipStatic(ServerLevel level, LoadedServerShip ship, boolean isStatic) {
        if (ship != null) {
            KineticShipControl control = KineticShipControl.getOrAddController(level, ship);
            if (control != null) {
                control.setStatic(isStatic);
                return true;
            }
        }
        return false;
    }

    public static boolean setShipStatic(CommandSourceStack source, String shipSlug, boolean isStatic) {
        Level level = source.getPlayer().level();
        if (level instanceof ServerLevel serverLevel) {
            LoadedServerShip ship = (LoadedServerShip) VSUtils.getShipBySlug(serverLevel, shipSlug);
            return setShipStatic(source, ship, isStatic);
        }
        return false;
    }

    public static boolean setShipStatic(CommandSourceStack source, Ship ship2, boolean isStatic) {
        Level level = source.getPlayer().level();
        if (level instanceof ServerLevel serverLevel) {
            LoadedServerShip ship = (LoadedServerShip) ship2;
            if (setShipStatic(serverLevel, ship, isStatic)) return true;
        }
        return executeParsedCommandOP(source, "vs set-static " + ship2.getSlug() + " true", false) == Command.SINGLE_SUCCESS;
    }

    public static int renameShipTotem(ServerPlayer player, Consumer<MutableComponent> messages, Ship ship) {
        ItemStack stack = player.getMainHandItem();
        ItemStack offhandStack = player.getOffhandItem();
        if (!(stack.getItem() instanceof ShipTotemItem) && !(offhandStack.getItem() instanceof ShipTotemItem)) {
            messages.accept(Component.literal("You must be holding a Ship Totem!"));
            return 0;
        }

        if (ship == null || ship.getSlug() == null) {
            messages.accept(Component.literal("No ship with that name exists!"));
            return 0;
        }

        stack.setHoverName(Component.literal(ship.getSlug()));
        messages.accept(Component.literal("Totem renamed to: " + ship.getSlug()));
        return 1;
    }

    public static int renameShipTotem(ServerPlayer player, Consumer<MutableComponent> messages, String newName) {
        ItemStack stack = player.getMainHandItem();
        ItemStack offhandStack = player.getOffhandItem();
        if (!(stack.getItem() instanceof ShipTotemItem) && !(offhandStack.getItem() instanceof ShipTotemItem)) {
            messages.accept(Component.literal("You must be holding a Ship Totem!"));
            return 0;
        }

        //Ensure the name actually exists
        VsiServerShipWorld shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(player.getServer());
        boolean found = shipObjectWorld.getAllShips().stream().anyMatch(ship -> ship != null && ship.getSlug() != null && newName.equals(ship.getSlug()));
        if (!found) {
            messages.accept(Component.literal("No ship with that name exists!"));
            return 0;
        }

        stack.setHoverName(Component.literal(newName));
        messages.accept(Component.literal("Totem renamed to: " + newName));
        return 1;
    }


    int getThisShipCommand(CommandContext<CommandSourceStack> ctx) {
        try {
            Entity sourceEntity = ctx.getSource().getPlayer();
            VsiServerShipWorld shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(ctx.getSource().getServer());

            if (sourceEntity != null) {
                Ship pickedShip = VSUtils.getShipNearPlayer(shipObjectWorld, sourceEntity);
                if (pickedShip != null) {
                    ctx.getSource().sendSystemMessage(Component.literal("Found ship: " + pickedShip.getSlug()));
                    return 1;
                } else {
//                                  ((VSCommandSource) ctx.getSource()).sendVSMessage( new TranslatableComponent(GET_SHIP_FAIL_MESSAGE));
                    ctx.getSource().sendSystemMessage(Component.literal("No ship found."));
                    return 0;
                }
            } else {
//                              ((VSCommandSource) ctx.getSource()).sendVSMessage( new TranslatableComponent(GET_SHIP_ONLY_USABLE_BY_ENTITIES_MESSAGE));
                ctx.getSource().sendSystemMessage(Component.literal("This command can only be used by entities."));
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }


}
