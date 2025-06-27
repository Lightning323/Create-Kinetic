//package org.zipcoder.createkinetic;
//
//// Imports will go here (translated from Kotlin)
//// This is a very large class with nested builders; translation will focus on main structure
//
//import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.arguments.ArgumentType;
//import com.mojang.brigadier.builder.LiteralArgumentBuilder;
//import com.mojang.brigadier.builder.RequiredArgumentBuilder;
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.Commands;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.phys.Vec3;
//import org.joml.primitives.AABBd;
//import org.valkyrienskies.core.api.ships.LoadedShip;
//import org.valkyrienskies.core.api.ships.QueryableShipData;
//import org.valkyrienskies.core.api.ships.Ship;
//import org.valkyrienskies.core.api.world.ShipWorld;
//import org.valkyrienskies.mod.common.command.RelativeVector3Argument;
//import org.valkyrienskies.mod.common.command.ShipArgument;
//import org.valkyrienskies.mod.mixinducks.feature.command.VSCommandSource;
//import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.arguments.ArgumentType;
//import com.mojang.brigadier.arguments.BoolArgumentType;
//import com.mojang.brigadier.arguments.DoubleArgumentType;
//import com.mojang.brigadier.arguments.StringArgumentType;
//import com.mojang.brigadier.builder.LiteralArgumentBuilder;
//import com.mojang.brigadier.builder.RequiredArgumentBuilder;
//import com.mojang.brigadier.context.CommandContext;
//import net.minecraft.commands.CommandRuntimeException;
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.arguments.EntityArgument;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.phys.BlockHitResult;
//import org.joml.Vector3d;
//import org.valkyrienskies.core.api.ships.ServerShip;
//import org.valkyrienskies.core.api.world.ServerShipWorld;
//import org.valkyrienskies.core.api.world.ShipWorld;
//import org.valkyrienskies.core.apigame.ShipTeleportData;
//import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
//
//public class VSCommands {
//
//    private static final String DELETED_SHIPS_MESSAGE = "command.valkyrienskies.delete.success";
//    private static final String SET_SHIP_STATIC_SUCCESS_MESSAGE = "command.valkyrienskies.set_static.success";
//    private static final String TELEPORT_SHIP_SUCCESS_MESSAGE = "command.valkyrienskies.teleport.success";
//    private static final String GET_SHIP_SUCCESS_MESSAGE = "command.valkyrienskies.get_ship.success";
//    private static final String GET_SHIP_FAIL_MESSAGE = "command.valkyrienskies.get_ship.fail";
//    private static final String GET_SHIP_ONLY_USABLE_BY_ENTITIES_MESSAGE = "command.valkyrienskies.get_ship.only_usable_by_entities";
//    private static final String TELEPORTED_MULTIPLE_SHIPS_SUCCESS = "command.valkyrienskies.teleport.multiple_ship_success";
//    private static final String TELEPORT_FIRST_ARG_CAN_ONLY_INPUT_1_SHIP = "command.valkyrienskies.mc_teleport.can_only_teleport_to_one_ship";
//    private static final String SCALED_SHIPS_MESSAGE = "command.valkyrienskies.scale.success";
//    private static final int REQUIRED_PERMISSION = 2;
//
//    public static void bootstrap() {
//        ArgumentTypes.register("valkyrienskies:ship", ShipArgument.class, new ShipArgument.Serializer());
//        ArgumentTypes.register("valkyrienskies:relative_vector", RelativeVector3Argument.class, new EmptyArgumentSerializer<>(RelativeVector3Argument::new));
//    }
//
//    private static <T> RequiredArgumentBuilder<VSCommandSource, T> argument(String name, ArgumentType<T> type) {
//        return RequiredArgumentBuilder.argument(name, type);
//    }
//
//    private static LiteralArgumentBuilder<VSCommandSource> literal(String name) {
//        return LiteralArgumentBuilder.literal(name);
//    }
//
//    public static void registerServerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
////        CommandDispatcher<VSCommandSource> vsDispatcher = (CommandDispatcher<VSCommandSource>) dispatcher;
//        CommandDispatcher<VSCommandSource> vsDispatcher = new CommandDispatcher<>(); //It has to be registered by valkyrienskies
//        VSCommands.register(vsDispatcher);
//
//        vsDispatcher.register(literal("vs").then(literal("get-ship")
////                                .executes(ctx -> {
////                            try {
//////                                CommandContext<VSCommandSource> mcContext = context;
//////                                Entity sourceEntity = ctx.getSource().getPlayer();
////                                Entity sourceEntity = Command < S > ctx.getSource().getPlayer();
////                                if (sourceEntity != null) {
////                                    BlockHitResult rayTrace = (BlockHitResult) sourceEntity.pick(10.0, 1.0F, false);
////                                    ServerShip ship = sourceEntity.level().getShipManagingPos(rayTrace.getBlockPos());
////                                    if (ship != null) {
////                                        ((VSCommandSource) ctx.getSource()).sendVSMessage(
////                                                new TranslatableComponent(GET_SHIP_SUCCESS_MESSAGE, ship.getSlug())
////                                        );
////                                        return 1;
////                                    } else {
////                                        ((VSCommandSource) ctx.getSource()).sendVSMessage(
////                                                new TranslatableComponent(GET_SHIP_FAIL_MESSAGE)
////                                        );
////                                        return 0;
////                                    }
////                                } else {
////                                    ((VSCommandSource) ctx.getSource()).sendVSMessage(
////                                            new TranslatableComponent(GET_SHIP_ONLY_USABLE_BY_ENTITIES_MESSAGE)
////                                    );
////                                    return 0;
////                                }
////                            } catch (Exception e) {
////                                if (!(e instanceof CommandRuntimeException)) e.printStackTrace();
////                                throw e;
////                            }
////                        })
//                        )
//                        .then(
//                                literal("ship")
////                                        .then(argument("ship", ShipArgument.ships())
////                                                .then(literal("rename")
//////                                                        .then(argument("newName", StringArgumentType.string()).executes(context -> {
//////                                                            try {
//////                                                                ServerShip ship = (ServerShip) ShipArgument.getShip(context, "ship");
//////                                                                String newName = StringArgumentType.getString(context, "newName");
//////                                                                vsCore.renameShip(ship, newName);
//////                                                                return 1;
//////                                                            } catch (Exception e) {
//////                                                                if (!(e instanceof CommandRuntimeException))
//////                                                                    e.printStackTrace();
//////                                                                throw e;
//////                                                            }
//////                                                        }))
////                                                )
////                                        )
//                        )
//                        .then(
//                                literal("teleport")
////                                        .then(argument("ships", ShipArgument.selectorOnly())
////                                                .then(argument("entity", EntityArgument.entity()).executes(context -> {
////                                                    try {
////                                                        ServerShipWorld world = (ServerShipWorld) ((VSCommandSource) context.getSource()).getShipWorld();
////                                                        Entity entity = EntityArgument.getEntity(context, "entity");
////                                                        Vector3d pos = new Vector3d(entity.getX(), entity.getY(), entity.getZ());
////                                                        for (ServerShip ship : ShipArgument.getShips(context, "ships")) {
////                                                            ShipTeleportData data = new ShipTeleportDataImpl(pos, ((CommandSourceStack) context.getSource()).level().dimensionId());
////                                                            vsCore.teleportShip(world, ship, data);
////                                                        }
////                                                        ((VSCommandSource) context.getSource()).sendVSMessage(
////                                                                new TranslatableComponent(TELEPORTED_MULTIPLE_SHIPS_SUCCESS, 1)
////                                                        );
////                                                        return 1;
////                                                    } catch (Exception e) {
////                                                        if (!(e instanceof CommandRuntimeException))
////                                                            e.printStackTrace();
////                                                        throw e;
////                                                    }
////                                                }))
////                                        )
//                        )
//        );
//    }
//
//    public static void registerClientCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
//        // TODO implement client commands
//    }
//
//    public static ShipWorld getShipWorld(CommandSourceStack source) {
//        return ((VSCommandSource) source).getShipWorld();
//    }
//}
