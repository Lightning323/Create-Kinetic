package com.lightning323.createkinetic;

import com.lightning323.createkinetic.ship.KineticShipControl;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import static com.lightning323.createkinetic.Createkinetic.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldTickEvent {

    private static int tickCount = 0;
    private static final int refreshRate = 4;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.level.isClientSide) {// Phase.START is equivalent to "PRE"
            if (tickCount == refreshRate) {//We only do this every N ticks
                tickCount = 0;
                if (event.level instanceof ServerLevel serverLevel) {
                    kineticWorldTick(serverLevel);
                }
            } else {
                tickCount++;
            }
        }
    }

    private static void kineticWorldTick(ServerLevel world) {
        VSGameUtilsKt.getShipObjectWorld(world).getLoadedShips().forEach(ship -> {
            if (ship != null) {
                KineticShipControl controller = ship.getAttachment(KineticShipControl.class);
                if (controller != null) {
                    controller.world = world;
                }
            }
        });

//        if (KineticConfig.enableWind || true) {
//            //Spawn wind particles for all players being dragged by ships with a SailsShipControl attachment
//            world.players().forEach(serverPlayerEntity -> {
//                if (serverPlayerEntity instanceof IEntityDraggingInformationProvider player) {
//                    if (player.getDraggingInformation().getLastShipStoodOn() != null) {
//                        long shipId = player.getDraggingInformation().getLastShipStoodOn();
//                        LoadedServerShip ship = VSGameUtilsKt.getShipObjectWorld(world).getLoadedShips().getById(shipId);
//                        if (ship != null) {
//                            SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
//                            if (controller != null) {
//                                //serverPlayerEntity.displayClientMessage(ship.getAttachment(SailsShipControl.class).message, true);
//                                if (controller.numSails > 0) {
//                                    if (player.getDraggingInformation().getTicksSinceStoodOnShip() < 100) {
//                                        Vector3dc shipPos = ship.getTransform().getPositionInWorld(); //fixme make sure this is the world pos of the ship
//                                        double windDir = Math.toRadians(ServerWindManager.getWindDirection(world, new Vec3(shipPos.x(), shipPos.y(), shipPos.z())) + 180);
//                                        double windStr = ServerWindManager.getWindStrength(world, serverPlayerEntity.blockPosition());
//                                        world.sendParticles(serverPlayerEntity, ValkyrienSails.WIND_PARTICLE, true, serverPlayerEntity.getX() + 75 * Math.cos(windDir) * windStr, serverPlayerEntity.getY() + 25, serverPlayerEntity.getZ() + 75 * Math.sin(windDir) * windStr, 10, 20, 10, 20, 0);
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
