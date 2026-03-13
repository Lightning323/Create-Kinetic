package com.lightning323.createkinetic.ship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static com.lightning323.createkinetic.Createkinetic.LOGGER;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lightning323.createkinetic.Createkinetic;
import com.lightning323.createkinetic.KineticConfig;
import com.lightning323.createkinetic.blocks.sail.SailBlockEntity;
import com.lightning323.createkinetic.blocks.sail.sailPulley.SailPulleyBlockEntity;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.valkyrienskies.core.api.ships.*;
import org.valkyrienskies.core.api.world.PhysLevel;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import static java.lang.Math.*;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;


public final class KineticShipControl implements ShipPhysicsListener, ServerTickListener {

    /**
     * Ship Control MUST be serializable and deserializable with Jackson JSON, otherwise, this will happen, when it tries to load ship control:
     * <p>
     * `Java.lang.IllegalArgumentException: setAttachment: attempted to set attachment for class com.lightning323.createkinetic.ship.SailsShipControl
     * (key = com.lightning323.createkinetic.ship.SailsShipControl). Cannot set this attachment because we tried to deserialize it earlier,
     * and the deserialization failed`
     * <p>
     * This means you have a field in your SailsShipControl class that contains a Minecraft BlockState Property or an ImmutableMap.
     * Jackson (the library VS2 uses for JSON) has no idea how to turn complex Minecraft objects into text and back again.
     */

    private ConcurrentLinkedQueue<Vector3dc> invForces = new ConcurrentLinkedQueue<Vector3dc>();
    private ConcurrentLinkedQueue<Vector3dc> rotForces = new ConcurrentLinkedQueue<Vector3dc>();
    private ConcurrentLinkedQueue<ForceAtPos> invPosForces = new ConcurrentLinkedQueue<ForceAtPos>();
    private ConcurrentLinkedQueue<ForceAtPos> rotPosForces = new ConcurrentLinkedQueue<ForceAtPos>();
    private ConcurrentLinkedQueue<Double> buoyForces = new ConcurrentLinkedQueue<Double>();
    private ConcurrentLinkedQueue<Double> rotTorques = new ConcurrentLinkedQueue<Double>();


//    public static void deferUntilLoaded(ServerShip ship, Level level, Consumer<KineticShipControl> consumer) {
//        if(ship instanceof LoadedServerShip) {
//            consumer.accept(getOrCreate(ship, level));
//        } else {
//            ValkyrienSkiesMod.vsCore.shipLoadEvent.once(
//                    { event -> event.ship.id == ship.id },
//                    {event -> consumer.accept(getOrCreate(event.ship))}
//            );
//        }
//    }

    // 1. Tell Jackson to ignore the complex object itself
    @JsonIgnore
    public LongSet sailPulleys = new LongOpenHashSet();

    // 2. The Getter (Serialization)
    @JsonProperty("pulleys")
    public long[] getJsonSailPulleys() {
        return sailPulleys.toLongArray();
    }

    // 3. The Setter/Constructor (Deserialization)
    @JsonProperty("pulleys")
    public void setJsonSailPulleys(long[] data) {
        this.sailPulleys.clear();
        if (data != null) {
            for (long p : data) {
                this.sailPulleys.add(p);
            }
        }
    }

    public boolean frozen = false;
    public int blockSails = 0;

    //These values are calculated from sailPulleys and blockSails and should be read only
    public int numFnASails = 0;
    public int numSquareSails = 0;

    //Anchors
    public int anchors = 0;
    public int anchorsActive = 0;

    public void updateSailCount() {
        numSquareSails = blockSails;

        // Use the specialized LongIterator to avoid object creation
        LongIterator iterator = sailPulleys.iterator();

        while (iterator.hasNext()) {
            long packedPos = iterator.nextLong();
            BlockPos pos = BlockPos.of(packedPos);

            // Ensure we check the level associated with the ship
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity == null || blockEntity.isRemoved()) {
                iterator.remove(); // Safely remove stale positions
                continue;
            }
            if (blockEntity instanceof SailPulleyBlockEntity sbe) numSquareSails += sbe.getTotalSails();
            else if (blockEntity instanceof SailBlockEntity sbe) numSquareSails += sbe.getTotalSails();
            else {
                iterator.remove();
            }
        }
        countSails();
    }

    public int numBallast = 0;
    public int numMagicBallast = 0;
    public int numBuoys = 0;
    public int numHelms = 0;

    public volatile double waterAmount = 0.0;

    public int boundx = 1;
    public int boundz = 1;

    @JsonIgnore
    public Level world = null;

    @JsonIgnore
    public Component message;

    public Direction shipDirection = Direction.NORTH;

    @JsonIgnore
    public LoadedServerShip ship = null;


    @JsonIgnore
    public static KineticShipControl getOrCreate(LoadedServerShip ship, Level world) {
        if (ship != null) {
            if (ship.getAttachment(KineticShipControl.class) == null) {
                ship.setAttachment(KineticShipControl.class, new KineticShipControl());
            }
            KineticShipControl controller = ship.getAttachment(KineticShipControl.class);
            assert controller != null;
            if (world != null) {
                controller.world = world;
            }
            if (ship.getShipAABB() != null) {
                controller.boundx = ship.getShipAABB().maxX() - ship.getShipAABB().minX();
                controller.boundz = ship.getShipAABB().maxZ() - ship.getShipAABB().minZ();
            } else {
                controller.boundx = 0;
                controller.boundz = 0;
            }
            if (controller.boundx > controller.boundz) {
                if (controller.shipDirection != Direction.WEST && controller.shipDirection != Direction.EAST) {
                    controller.shipDirection = Direction.EAST;
                    //swap square and fna sails
                    int x = controller.numSquareSails;
                    controller.numSquareSails = controller.numFnASails;
                    controller.numFnASails = x;
                    LOGGER.info("Sail types swapped! New ship dir: " + controller.shipDirection);
                }
            } else {
                if (controller.shipDirection != Direction.SOUTH && controller.shipDirection != Direction.NORTH) {
                    controller.shipDirection = Direction.NORTH;
                    //swap square and fna sails
                    int x = controller.numSquareSails;
                    controller.numSquareSails = controller.numFnASails;
                    controller.numFnASails = x;
                    LOGGER.info("Sail types swapped! New ship dir: " + controller.shipDirection);
                }
            }

            //LOGGER.info("Xbound=" + boundx + " Zbound=" + boundz);
            //LOGGER.info("dir=" + controller.shipDirection.toString());
            return controller;
        } else {
            return null;
        }
    }

    @Override
    public void physTick(@NotNull PhysShip physShip, @NotNull PhysLevel physLevel) {
//        Createkinetic.LOGGER.debug("PHYS TICK");
        if (numSquareSails < 0) numSquareSails = 0;
        if (numFnASails < 0) numFnASails = 0;
        PhysShipImpl physShip1 = (PhysShipImpl) physShip;

        physShip1.setDoFluidDrag(true);
        physShip.setStatic(isAnchored());

        while (!invForces.isEmpty()) {
            physShip1.applyInvariantForce(Objects.requireNonNull(invForces.poll()));
            //LOGGER.info("invForce applied");
        }

        while (!rotForces.isEmpty()) {
            physShip1.applyRotDependentForce(Objects.requireNonNull(rotForces.poll()));
            //LOGGER.info("rotDependentForce applied");
        }

        while (!invPosForces.isEmpty()) {
            ForceAtPos invData = invPosForces.poll();
            if (invData != null) { //fixme if you ever come across a physpipelinecrash(too many game frames)
                physShip1.applyInvariantForceToPos(invData.force, invData.pos);
            }
            //LOGGER.info("invForceTOPOS applied");
        }
        while (!rotPosForces.isEmpty()) {
            ForceAtPos rotData = rotPosForces.poll();
            if (rotData != null && rotData.force != null && rotData.pos != null) { //fixme if rotposforces are not applying
                physShip1.applyRotDependentForceToPos(rotData.force, rotData.pos);
            }
            //LOGGER.info("rotDependentForceTOPOS applied");
        }

//        while (!rotTorques.isEmpty()) {
//            physShip1.applyRotDependentTorque(rotTorques.poll());
//        }

        //KEEL BEHAVIOR
        Vector3dc linearVelocity = physShip1.getVelocity();

        Vector3d acceleration = linearVelocity.negate(new Vector3d());
        Vector3d force = acceleration.mul(physShip1.getMass());

        force = physShip1.getTransform().getWorldToShip().transformDirection(force);

        Vector3d keelForce; //todo perhaps make this based on length/width ratio?
        if (shipDirection == Direction.NORTH || shipDirection == Direction.SOUTH) {
            keelForce = new Vector3d(force.x() * KineticConfig.keelStrength, 0, 0);
        } else {
            keelForce = new Vector3d(0, 0, force.z() * 4);
        }

        if (numHelms > 0) physShip.applyRotDependentForce(keelForce);

//        if (numMagicBallast > 0) { //TODO: Implement this
//            Vector3d shipUp = new Vector3d(0.0, 1.0, 0.0);
//            Vector3d worldUp = new Vector3d(0.0, 1.0, 0.0);
//            //todo possibly modify worldUp based on wind angle & numsails to make ship heel (should really do it separately)
//            physShip1.getTransform().getShipToWorldRotation().transform(shipUp);
//
//            double angleBetween = shipUp.angle(worldUp);
//            Vector3d idealAngularAcceleration = new Vector3d(0, 0, 0);
//
//            if (angleBetween > 0.01) {
//                Vector3d stabilizationRotationAxisNormalized = shipUp.cross(worldUp, new Vector3d()).normalize();
//                idealAngularAcceleration.add(stabilizationRotationAxisNormalized.mul(
//                        angleBetween, stabilizationRotationAxisNormalized)
//                );
//            }
//
//            Vector3dc omega = physShip1.getAngularVelocity();
//            idealAngularAcceleration.sub(omega.x(), omega.y(), omega.z());
//
//            Vector3d stabilizationTorque = physShip1.getTransform().getShipToWorldRotation().transform(
//                    physShip1.getMomentOfInertia().transform(
//                            physShip1.getTransform().getShipToWorldRotation().transformInverse(idealAngularAcceleration)
//                    )
//            );
//
//            stabilizationTorque.mul(numMagicBallast * KineticConfig.magicBallastForce);
//            physShip1.applyInvariantTorque(stabilizationTorque);
//
//        }

        if (numBallast > 0 || numBuoys > 0) {
            physShip1.setBuoyantFactor(1.0 + numBuoys * KineticConfig.buoyStrength + numBallast * KineticConfig.ballastStrength);
        }

        //sail force implementation
        if (numSquareSails > 0 || numFnASails > 0) {
            Vector3d sailForce = new Vector3d(
                    shipDirection.getNormal().getX(), shipDirection.getNormal().getY(), shipDirection.getNormal().getZ()
            );

            if (KineticConfig.windStrengthMultiplier > 0) {
                Vector3dc worldShipPos = physShip1.getTransform().getPositionInWorld();
                //Get the position of our ship
                Vec3 shipPosVec = new Vec3(worldShipPos.x(), worldShipPos.y(), worldShipPos.z());
                BlockPos shipPosBlockPos = new BlockPos((int) worldShipPos.x(), (int) worldShipPos.y(), (int) worldShipPos.z());

                //Get the wind parameters
                double windDirection = ServerWindManager.getWindDirection(world, shipPosVec); //in degrees
                double windStrength = ServerWindManager.getWindStrength(world, shipPosBlockPos)  // -1.0 -- 1.0
                        * KineticConfig.windStrengthMultiplier;

                //Get the Y angle of our ship in radians
                double shipAngle = getShipYaw(physShip1.getTransform().getShipToWorldRotation()); //in radians

                double windAngle;
                double squareAngleBetween;
                double fnaAngleBetween;
                //LOGGER.info("wind:"+windAngle+" ship:"+shipAngle);
                if (windStrength > 0) {
                    windAngle = windDirection + 90 % 360;
                } else {
                    windAngle = windDirection + 270 % 360;
                }
                if (shipDirection == Direction.WEST) {
                    windAngle = (windAngle - 90) % 360;
                } else if (shipDirection == Direction.NORTH) {
                    windAngle = (windAngle + 180) % 360;
                } else if (shipDirection == Direction.EAST) {
                    windAngle = (windAngle + 90) % 360;
                }

                windAngle = toRadians(windAngle);
                squareAngleBetween = abs(min(abs(shipAngle - windAngle), 2 * PI - abs(shipAngle - windAngle)));
                double fnaAngle1 = abs(shipAngle + PI / 2 - windAngle);
                double fnaAngle2 = abs(shipAngle - PI / 2 - windAngle);
                fnaAngleBetween = abs(min(min(fnaAngle1, 2 * PI - fnaAngle1), min(fnaAngle2, 2 * PI - fnaAngle2)));
                if (squareAngleBetween > PI / 2) {
                    fnaAngleBetween *= 2;
                }

                //Square sails are hung from a horizontal "yard" perpendicular to the mast.
                double squareWindModifier = numSquareSails / calculateWindAngleModifier(squareAngleBetween, PI - KineticConfig.noSailZone);

                //Fore-and-aft sails (like Jibs, Staysails, or Bermuda rigs) are aligned with the centerline of the ship (front-to-back).
                double fnAWindModifier = numFnASails / calculateWindAngleModifier(fnaAngleBetween, PI - KineticConfig.noSailZone);

                double mul = -(squareWindModifier + fnAWindModifier) * KineticConfig.sailSpeed * (windStrength * windStrength);
//                LOGGER.debug("Sail speed = {}", mul);
                sailForce.mul(mul);

//                LOGGER.info("sailforce=" + sailForce.toString() + " shipdir=" + shipDirection.toString());
                physShip1.applyRotDependentForce(sailForce);
            }
        }
        waterAmount = physShip1.getLiquidOverlap();
    }

    private boolean isAnchored() {
        return anchorsActive > 0 || frozen;
    }


    public void applyInvariantForce(Vector3dc force) {
        //LOGGER.info("inv force requested");
        invForces.add(force);
    }

    public void applyRotDependentForce(Vector3dc force) {
        rotForces.add(force);
        //LOGGER.info("applyrotforce called");
    }

    public void applyInvariantForceToPos(Vector3dc force, Vector3dc pos) {
        ForceAtPos data = new ForceAtPos();
        data.force = force;
        data.pos = pos;
        invPosForces.add(data);
    }

    public void applyRotDependentForceToPos(Vector3dc force, Vector3dc pos) {
        ForceAtPos data = new ForceAtPos();
        data.force = force;
        data.pos = pos;
        rotPosForces.add(data);
        //LOGGER.info("applyrotforceTOPOS called");
    }

    public void updateRudderAngle(double angle) {

    }

    private double calculateWindAngleModifier(double windAngle, double noSail) {
        if (KineticConfig.forgivingSails) {
            return pow(2, windAngle / noSail) + pow(2, -windAngle / noSail);
        }
        return pow(2, pow(windAngle, 2) / noSail) + pow(2, -pow(windAngle, 2) / noSail);
    }

    public static double getShipYaw(Quaterniondc shipRotation) {
        Vector3d worldForwardDirection = new Vector3d();
        Vector3d LOCAL_SHIP_FORWARD_NEGATIVE_Z = new Vector3d(0.0, 0.0, -1.0);
        shipRotation.transform(LOCAL_SHIP_FORWARD_NEGATIVE_Z, worldForwardDirection);

        if (worldForwardDirection.lengthSquared() < 1.0e-12) {
            return 0.0;
        }

        double horizontalDistance = sqrt(worldForwardDirection.x * worldForwardDirection.x + worldForwardDirection.z * worldForwardDirection.z);

        double yaw;
        if (horizontalDistance < 1.0e-9) {
            yaw = 0.0;
        } else {
            yaw = atan2(worldForwardDirection.x, -worldForwardDirection.z);
        }
        if (yaw < 0) {
            yaw = 2 * PI + yaw;
        }

        return yaw;
    }

    public void addBuoyancy(double buoyancy) {
        buoyForces.add(buoyancy);
    }

    public int getNumBallast() {
        return numBallast;
    }

    public boolean shouldDispose() {
        return numBallast <= 0 && numFnASails <= 0 && numSquareSails <= 0 && numMagicBallast <= 0 && numBuoys <= 0 && numHelms == 0 && !frozen;
    }

    private void deleteIfEmpty() { //fixme add call for this
        if (shouldDispose()) {
            ship.removeAttachment(KineticShipControl.class);
        }
    }

    @Override
    public void onServerTick() {
//        Createkinetic.LOGGER.debug("SERVER TICK");
    }

    public void countSails() {
        Createkinetic.LOGGER.debug("SAIL COUNT Square: {}, FNA: {}", numSquareSails, numFnASails);
    }

    public void setStatic(boolean frozen) {
        this.frozen = frozen;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ForceAtPos {
        Vector3dc force;
        Vector3dc pos;
    }
}
