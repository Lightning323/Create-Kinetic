package org.zipcoder.createkinetic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.world.ShipWorld;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.mixinducks.feature.command.VSCommandSource;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.zipcoder.createkinetic.utils.CommandUtils.executeParsedCommand;

//https://github.com/ValkyrienSkies/Valkyrien-Skies-2/blob/42e49defd5f398f1b1e1a952d56a0a4407373e31/common/src/main/kotlin/org/valkyrienskies/mod/common/command/VSCommands.kt#L40
//https://github.com/ValkyrienSkies/Valkyrien-Skies-2/blob/25db12ab7eff4d2813b7d1d8b0553e6e7f2e0fc3/common/src/main/kotlin/org/valkyrienskies/mod/common/command/VSCommands.kt#L263
@Mod.EventBusSubscriber
public class VMCommands {

    final static int DELETE_MASSLESS_THRESHOLD = 99;

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
        //CommandDispatcher<VSCommandSource> vsDispatcher = event.getDispatcher(); //TODO: is vsCommandSource needed?

        dispatcher.register(Commands.literal("ships")  // Only players with permission level 2 or higher see this command
                .then(Commands.literal("deletemassless").requires(source -> source.hasPermission(2)).executes(context -> {
                    vsDeleteMasslessShips(context);
                    return Command.SINGLE_SUCCESS;
                })).then(Commands.literal("total").requires(source -> source.hasPermission(2)).executes(context -> {
                    vsCountShips(context);
                    return Command.SINGLE_SUCCESS;
                })));

        dispatcher.register(Commands.literal("ships")
                        .then(Commands.literal("this").executes(ctx -> {
//                            String ship = vsGetCurrentShip(context);
//                            if (ship == null) {
//                                context.getSource().sendSystemMessage(Component.literal("No ship found."));
//                            } else {
//                                context.getSource().sendSystemMessage(
//                                        Component.literal("Found ship: ")
//                                                .append(Component.literal(ship).withStyle(style -> style
//                                                        .withColor(ChatFormatting.AQUA)
//                                                        .withUnderlined(true)
//                                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ship))
//                                                )));
//                            }

                            return executeParsedCommand(ctx.getSource(), "vs get-ship");
                        }))
                        .then(Commands.literal("rename")
                                .then(Commands.argument("old", StringArgumentType.word())//EntityArgument.entity()
                                        .then(Commands.argument("new", StringArgumentType.word()) // /neutron rename <old> <new>
                                                .executes(ctx -> {
//                                            E old = EntityArgument.getEntity(ctx, "old");
                                                    String oldSlug = StringArgumentType.getString(ctx, "old");
                                                    String newSlug = StringArgumentType.getString(ctx, "new");
                                                    return executeParsedCommand(ctx.getSource(), "vs ship " + oldSlug + " rename " + newSlug);
                                                }))))

        );
    }

    private static void vsCountShips(CommandContext<CommandSourceStack> cc) {
        ServerShipWorldCore shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(cc.getSource().getServer());
        cc.getSource().sendSystemMessage(Component.literal("There are " + shipObjectWorld.getAllShips().size() + " total ships in the world."));

        AtomicInteger masslessShips = new AtomicInteger(0);
        shipObjectWorld.getAllShips().stream().filter((s) -> {
            //Delete ships that have a low / nonexistent mass
            return s.getInertiaData().getMass() < DELETE_MASSLESS_THRESHOLD;
        }).forEach((ship) -> {
            masslessShips.incrementAndGet();
        });
        cc.getSource().sendSystemMessage(Component.literal("(There are " + masslessShips.get() + " massless ships)"));
    }

    private static String vsGetCurrentShip(CommandContext<CommandSourceStack> cc) {
        VSCommandSource vsContext = (VSCommandSource) cc;
        ShipWorld shipWorld = vsContext.getShipWorld();

        ChunkPos chunkPos = cc.getSource().getPlayer().chunkPosition();
        LoadedShip byChunkPos = shipWorld.getLoadedShips().getByChunkPos(chunkPos.x, chunkPos.z, "");
        if (byChunkPos == null) return null;
        else return byChunkPos.getSlug();
    }
//
//    private static String getShipBySlug(CommandContext<CommandSourceStack> cc) {
//        VSCommandSource vsContext = (VSCommandSource) cc;
//        ShipWorld shipWorld = vsContext.getShipWorld();
//
//        vsContext.getShipWorld().getAllShips().getById()
//        String slug = cc.getArgument("slug", String.class);
//
//        ChunkPos chunkPos = cc.getSource().getPlayer().chunkPosition();
//        LoadedShip byChunkPos = shipWorld.getLoadedShips().getByChunkPos(chunkPos.x, chunkPos.z, "");
//
//        if (byChunkPos == null) return null;
//        else return byChunkPos.getSlug();
//    }

//    private static int vsListAllShips(CommandContext<CommandSourceStack> cc) {
//        VSCommandSource vsContext = (VSCommandSource) cc;
//        ShipWorld shipWorld = vsContext.getShipWorld();
//
//        cc.getSource().sendSystemMessage(
//                Component.literal(shipWorld.getAllShips().size() + " Total ships")
//        );
//
//
//        StringBuilder stringBuilder = new StringBuilder();
//        shipWorld.getAllShips().forEach((s) -> {
//            stringBuilder.append("Ship  id=").append(s.getId()).append("  \"").append(s.getSlug()).append("\"").append("\n");
//        });
//
//        cc.getSource().sendSystemMessage(
//                Component.literal(stringBuilder.toString())
//        );
//        return 0;
//    }

    private static int vsDeleteMasslessShips(CommandContext<CommandSourceStack> cc) {
        ServerShipWorldCore shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(cc.getSource().getServer());

        ArrayList<ServerShip> shipsToDelete = new ArrayList<>();

        shipObjectWorld.getAllShips().stream().filter((s) -> {
            //Delete ships that have a low / nonexistent mass
            return s.getInertiaData().getMass() < DELETE_MASSLESS_THRESHOLD;
        }).forEach((ship) -> {
            System.out.println("Listing Ship: " + ship.toString());
            shipsToDelete.add(ship);
        });

        cc.getSource().sendSystemMessage(Component.literal("Listed " + shipsToDelete.size() + " massless ships."));

        shipsToDelete.forEach(shipObjectWorld::deleteShip);

        cc.getSource().sendSystemMessage(Component.literal("Deleted massless ships."));

        return 0;
    }
}
