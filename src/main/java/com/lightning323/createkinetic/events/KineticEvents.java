package com.lightning323.createkinetic.events;

import com.lightning323.createkinetic.KineticConfig;
import com.lightning323.createkinetic.registries.KineticParticles;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.lightning323.createkinetic.ship.ShipUtils;
import com.lightning323.createkinetic.ship.WindManager;
import com.simibubi.create.content.contraptions.bearing.SailBlock;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

import static com.lightning323.createkinetic.CreateKinetic.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KineticEvents {

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        BlockState state = event.getPlacedBlock();
        if (event.getLevel().isClientSide()) return;

        if (state.getBlock() instanceof com.simibubi.create.content.contraptions.bearing.SailBlock) {
            Direction value = state.getValue(BlockStateProperties.FACING);
            ShipUtils.addSail(event.getEntity().level(), event.getPos(), value.getAxis());
        }
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        if (event.getLevel().isClientSide()) return;

        if (state.getBlock() instanceof com.simibubi.create.content.contraptions.bearing.SailBlock) {
            Direction value = state.getValue(BlockStateProperties.FACING);
            ShipUtils.removeSail(event.getPlayer().level(), event.getPos(), value.getAxis());
        }
    }

    private static int tickCount = 0;
    private static final int tickInterval = 100;

    //TODO: Optimize this (Remove tick logic
//    @SubscribeEvent
//    public static void onWorldTick(TickEvent.LevelTickEvent event) {
//        //Server side tick
//        if (event.phase == TickEvent.Phase.START && !event.level.isClientSide) {// Phase.START is equivalent to "PRE"
//            if (tickCount == tickInterval) {//We only do this every N ticks
//                tickCount = 0;
//                if (event.level instanceof ServerLevel serverLevel) {
//                    kineticWorldTick(serverLevel);
//                }
//            } else {
//                tickCount++;
//            }
//        }
//    }

    private static void kineticWorldTick(ServerLevel world) {
//        VSGameUtilsKt.getShipObjectWorld(world).getLoadedShips().forEach(ship -> {
//            if (ship != null) {
//                KineticShipControl controller = ship.getAttachment(KineticShipControl.class);
//                if (controller != null) {
//                    controller.world = world; //TODO: Not sure why this is needed
//                    controller.periodicUpdate();
//                }
//            }
//        });

        //TODO: Add wind particles?
//        if (KineticConfig.windParticles && KineticConfig.windStrengthMultiplier > 0) {
//            //Spawn wind particles for all players being dragged by ships with a SailsShipControl attachment
//            world.players().forEach(serverPlayerEntity -> {
//                if (serverPlayerEntity instanceof IEntityDraggingInformationProvider player) {
//                    if (player.getDraggingInformation().getLastShipStoodOn() != null) {
//                        long shipId = player.getDraggingInformation().getLastShipStoodOn();
//                        LoadedServerShip ship = VSGameUtilsKt.getShipObjectWorld(world).getLoadedShips().getById(shipId);
//                        if (ship != null) {
//                            KineticShipControl controller = ship.getAttachment(KineticShipControl.class);
//                            if (controller != null) {
//                                //serverPlayerEntity.displayClientMessage(ship.getAttachment(SailsShipControl.class).message, true);
//                                if (controller.numSquareSails > 0 || controller.numFnASails > 0) {
//                                    if (player.getDraggingInformation().getTicksSinceStoodOnShip() < 100) {
//                                        Vector3dc shipPos = ship.getTransform().getPositionInWorld(); //fixme make sure this is the world pos of the ship
//                                        double windDir = Math.toRadians(WindManager.getWindDirection(world, new Vec3(shipPos.x(), shipPos.y(), shipPos.z())) + 180);
//                                        double windStr = WindManager.getWindStrength(world, serverPlayerEntity.blockPosition());
//                                        world.sendParticles(serverPlayerEntity, KineticParticles.WIND.get(), true, serverPlayerEntity.getX() + 75 * Math.cos(windDir) * windStr, serverPlayerEntity.getY() + 25, serverPlayerEntity.getZ() + 75 * Math.sin(windDir) * windStr, 10, 20, 10, 20, 0);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            });
//        }
    }
}
