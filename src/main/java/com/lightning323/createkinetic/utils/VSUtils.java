package com.lightning323.createkinetic.utils;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.internal.world.VsiServerShipWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.lightning323.createkinetic.commands.KineticCommands.executeParsedCommandOP;

public class VSUtils {

    //TODO: any instance of distanceToSqr should be changed in your code, otherwise it may not work on VS ships
    //From org.valkyrienskies.mod.mixin.mod_compat.create.client;
    public static double vsDistanceToSqr(Vec3 position1, Vec3 position2) {
        Vec3 shipPos1 = VSGameUtilsKt.toShipRenderCoordinates(Minecraft.getInstance().level, position2, position1);
        return shipPos1.distanceToSqr(position2);
    }

    public static int recoverShip(MinecraftServer server, ServerPlayer player, String shipSlug) {
        CommandSourceStack source = player.createCommandSourceStack();
        int teleportx = (int) player.getEyePosition().x;
        int teleporty = (int) player.getEyePosition().y;
        int teleportz = (int) player.getEyePosition().z;
        return executeParsedCommandOP(source, "vs teleport " + shipSlug + " "
                + teleportx + " " + teleporty + " " + teleportz, false);
    }


    public static SuggestionProvider<CommandSourceStack> shipSlugSuggestions(boolean allDimensions, int maxDistance) {
        return (CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) -> {
            List<String> suggestions = new ArrayList<>();
            try {
                ServerPlayer player = context.getSource().getPlayer();
                VsiServerShipWorld shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(context.getSource().getServer());

                ResourceKey<Level> dimension = player.level().dimension();
                String playerDimension = dimension.location().toString();
                Vec3 playerPos = player.getEyePosition();

                for (ServerShip ship : shipObjectWorld.getLoadedShips()) {
                    if (maxDistance > 0 && ship.getTransform().getPositionInWorld().distance(playerPos.x, playerPos.y, playerPos.z) < maxDistance) {
                        if (allDimensions || ship.getChunkClaimDimension().endsWith(playerDimension)) {
                            suggestions.add(ship.getSlug());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return SharedSuggestionProvider.suggest(suggestions, builder);
        };
    }

    public static ServerShip getShipBySlug(ServerLevel server, String slug) {
        VsiServerShipWorld shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(server);
        for (ServerShip ship : shipObjectWorld.getLoadedShips()) {
            if (ship.getSlug() != null && ship.getSlug().equals(slug)) {
                return ship;
            }
        }
        return null;
    }

    public static Ship getShipNearPlayer(VsiServerShipWorld shipWorld, Entity sourceEntity) {
//        BlockHitResult rayTrace = (BlockHitResult) sourceEntity.pick(RAYTRACE_RANGE, 1.0F, false);
        Vec3 eyePos = sourceEntity.getEyePosition(1.0F); // Origin (ox, oy, oz)
        Vec3 viewVec = sourceEntity.getViewVector(1.0F); // Direction (dx, dy, dz)

        //Get the one closest to the player
        ResourceKey<Level> dimension = sourceEntity.level().dimension();
        Ship closestShip = null;
        double closestDistance = 5;

        for (LoadedServerShip ship : shipWorld.getLoadedShips()) {
            if (ship != null && ship.getShipAABB() != null &&
                    ship.getChunkClaimDimension().endsWith(dimension.location().toString()) //if we are in the same dimension
            ) {
                double distance = ship.getTransform().getPositionInWorld().distance(eyePos.x, eyePos.y, eyePos.z);

                if (distance < 20) {
                    Vector3d rayOrigin = ship.getWorldToShip().transformPosition(new Vector3d(eyePos.x, eyePos.y, eyePos.z));
                    Vector3d rayDir = ship.getWorldToShip().transformDirection(new Vector3d(viewVec.x, viewVec.y, viewVec.z));
                    boolean intersect = ship.getShipAABB().intersectsRay((float) rayOrigin.x, (float) rayOrigin.y, (float) rayOrigin.z, (float) rayDir.x, (float) rayDir.y, (float) rayDir.z);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestShip = ship;
                    }
//                    System.out.println("ship " + ship.getSlug() + ", aabb: " + ship.getShipAABB().toString() + ", intersects: " + intersect + ", distance: " + distance);
                    if (intersect) {
//                        System.out.println("Found ship: " + ship.getSlug());
                        return ship;
                    }
                }
            }
        }
        return closestShip;
    }

    final static int DELETE_MASSLESS_THRESHOLD = 99;
    final static int PROXIMITY_RADIUS = 20;

    public static int deleteMasslessShips(CommandContext<CommandSourceStack> cc) {
        VsiServerShipWorld shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(cc.getSource().getServer());

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

    public static void listTotalShips(CommandContext<CommandSourceStack> cc, boolean count) {
        VsiServerShipWorld shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(cc.getSource().getServer());
        cc.getSource().sendSystemMessage(Component.literal("There are " + shipObjectWorld.getAllShips().size() + " total ships in the world."));


        /**
         * Player information
         */
        ServerPlayer player = cc.getSource().getPlayer();
        ResourceKey<Level> dimension = player.level().dimension();
        String playerDimension = dimension.location().toString();
        int playerChunkX = player.blockPosition().getX() >> 4;
        int playerChunkZ = player.blockPosition().getZ() >> 4;
        System.out.println("Player Chunk X: " + playerChunkX + " Chunk Z: " + playerChunkZ);
        System.out.println("Player Dimension: " + playerDimension);

        /**
         * Individual ship counters
         */
        AtomicInteger masslessShips = new AtomicInteger(0);
        AtomicInteger shipsInThisDimension = new AtomicInteger(0);
        AtomicInteger shipsWithinProximity = new AtomicInteger(0);


        for (ServerShip ship : shipObjectWorld.getAllShips()) {
            double mass = 0;

            if (ship != null) {
//                System.out.println("Ship " + ship.getSlug() + " is in dimension " + ship.getChunkClaimDimension());
                mass = ship.getInertiaData().getMass();
                if (mass < DELETE_MASSLESS_THRESHOLD) {
                    masslessShips.incrementAndGet();
                }
                if (ship.getChunkClaimDimension().endsWith(playerDimension)) {//Ship chunk claim dimension looks like this  minecraft:dimension:minecraft:overworld
                    shipsInThisDimension.incrementAndGet();
                    if (Math.abs(ship.getChunkClaim().getXMiddle() - playerChunkX) < PROXIMITY_RADIUS
                            && Math.abs(ship.getChunkClaim().getZMiddle() - playerChunkZ) < PROXIMITY_RADIUS) {
                        shipsWithinProximity.incrementAndGet(); //shipsWithinProximity
                    }
                }
            }

            if (count) {
                if (ship == null) {
                    cc.getSource().sendSystemMessage(Component.literal("Null ship found."));
                    continue;
                }
                String slug = ship.getSlug();
                Vector3dc positionInWorld = ship.getTransform().getPositionInWorld();
                // Create the full string first for the clipboard
                String clipboardContent = String.format("%s\t Dim=%s\t X=%.2f\t Y=%.2f\t Z=%.2f\t Mass=%.2f",
                        slug, ship.getChunkClaimDimension(), positionInWorld.x(), positionInWorld.y(), positionInWorld.z(), mass);

                cc.getSource().sendSystemMessage(
                        Component.literal(slug)
                                .withStyle(ChatFormatting.WHITE)
                                // Add the click event to the base component or a wrapper
                                .withStyle(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, clipboardContent))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy"))))
                                .append(Component.literal(" Dim=")
                                        .append(Component.literal(ship.getChunkClaimDimension())
                                                .withStyle(ChatFormatting.BLUE)))
                                .append(Component.literal(" X=")
                                        .append(Component.literal(String.format("%.2f", positionInWorld.x()))
                                                .withStyle(ChatFormatting.GREEN)))
                                .append(Component.literal(" Y=")
                                        .append(Component.literal(String.format("%.2f", positionInWorld.y()))
                                                .withStyle(ChatFormatting.AQUA)))
                                .append(Component.literal(" Z=")
                                        .append(Component.literal(String.format("%.2f", positionInWorld.z()))
                                                .withStyle(ChatFormatting.RED)))
                                .append(Component.literal(" Mass=")
                                        .append(Component.literal(String.format("%.2f", mass))
                                                .withStyle(ChatFormatting.YELLOW)))
                );
            }
        }
        cc.getSource().sendSystemMessage(Component.literal("(" + masslessShips.get() + " massless ships)"));
        cc.getSource().sendSystemMessage(Component.literal("(" + shipsInThisDimension.get() + " ships in this dimension)"));
//        cc.getSource().sendSystemMessage(Component.literal("(" + shipsWithinProximity.get() + " ships within " + PROXIMITY_RADIUS + " chunk proximity)"));
    }


}
