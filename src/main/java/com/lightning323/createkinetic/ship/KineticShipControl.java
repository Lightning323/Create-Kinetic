package com.lightning323.createkinetic.ship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.lightning323.createkinetic.CreateKinetic;
import com.lightning323.createkinetic.KineticConfig;
import com.lightning323.createkinetic.blocks.sail.sailPulley.SailClothBlock;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.slf4j.Logger;
import org.valkyrienskies.core.api.ships.*;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.api.world.PhysLevel;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

import static java.lang.Math.*;

import java.lang.Math;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;


public final class KineticShipControl implements ShipPhysicsListener, ServerTickListener {
    @JsonIgnore
    public static final Logger LOGGER = CreateKinetic.LOGGER;
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

    public Direction preferredDirection = Direction.NORTH;
    public Direction shipDirection = Direction.NORTH;
    public boolean frozen = false;
    public int anchors = 0;
    public int anchorsActive = 0;
    public int numBallast = 0;
    public int numEnchantedBallast = 0;
    public int numBuoys = 0;
    public int helms = 0;
    public volatile double waterAmount = 0.0;
    public int boundx = 1;
    public int boundz = 1;

    /**
     * SAIL COUNT
     */
    //TODO: Wind is 2D, meaning sails placed flat wont catch wind. Should this be changed?

    @JsonIgnore
    public LongSet sailsX = new LongOpenHashSet();

    @JsonIgnore
    public LongSet sailsZ = new LongOpenHashSet();

    // 2. The Getter (Serialization)
    @JsonProperty("sailsX")
    public long[] getJsonSailPulleysX() {
        return sailsX.toLongArray();
    }

    @JsonProperty("sailsZ")
    public long[] getJsonSailPulleysZ() {
        return sailsZ.toLongArray();
    }

    // 3. The Setter/Constructor (Deserialization)
    @JsonProperty("sailsX")
    public void setJsonSailPulleysX(long[] data) {
        this.sailsX.clear();
        if (data != null) {
            for (long p : data) this.sailsX.add(p);
        }
    }

    @JsonProperty("sailsZ")
    public void setJsonSailPulleysZ(long[] data) {
        this.sailsZ.clear();
        if (data != null) {
            for (long p : data) this.sailsZ.add(p);
        }
    }

    //These values are calculated from sailPulleys and blockSails and should be read only
    public int numFnASails = 0; //Fore and aft sails are the sails that are paralell to the length of the ship (sideways). They catch wind sideways.
    public int numSquareSails = 0; //Square sails are across the width of the ship, they catch wind from the front and back


    public void updateSailCount(ServerLevel world) {
        setWorld(world);

        //North = -Z (Direction.AxisDirection.NEGATIVE, Direction.Axis.Z)
        //South = +Z (Direction.AxisDirection.POSITIVE, Direction.Axis.Z)
        //East = +X  (Direction.AxisDirection.POSITIVE, Direction.Axis.X)
        //West = -X  (Direction.AxisDirection.NEGATIVE, Direction.Axis.X)
        switch (shipDirection) {
            case NORTH, SOUTH -> {
                numSquareSails = countAndRemoveSails(sailsZ, world);
                numFnASails = countAndRemoveSails(sailsX, world);
            }
            case EAST, WEST -> {
                numSquareSails = countAndRemoveSails(sailsX, world);
                numFnASails = countAndRemoveSails(sailsZ, world);
            }
            default -> {
                numSquareSails = 0;
                numFnASails = 0;
            }
        }
        LOGGER.debug("UPDATED SAILS. Square: {} ForeNAft: {}", numSquareSails, numFnASails);
    }

    /**
     * Checks if a block is a valid sail block (Not block entity)
     *
     * @param block
     * @return
     */
    public static boolean isValidSailBlock(Block block) {
        return block instanceof com.simibubi.create.content.contraptions.bearing.SailBlock ||
                block instanceof SailClothBlock;
    }

    private int countAndRemoveSails(LongSet set, ServerLevel world) {
        LongIterator iterator = set.iterator();
        int count = 0;

        while (iterator.hasNext()) {
            long packedPos = iterator.nextLong();
            BlockPos pos = BlockPos.of(packedPos);
//            BlockEntity blockEntity = world.getBlockEntity(pos);
            Block block = world.getBlockState(pos).getBlock();
            if (isValidSailBlock(block)) { //Valid sail blocks
                count++;
            } else {
                iterator.remove();
            }

            //We no longer need to check block entities because the actual pulley sail block can tell us how many sails we have
            /*
             else if (blockEntity != null && !blockEntity.isRemoved()) {
                //Valid sail block entities
                if (blockEntity instanceof SailPulleyBlockEntity sbe) count += sbe.getTotalSails();
                else if (blockEntity instanceof SailBlockEntity sbe) count += sbe.getTotalSails();
            } */
        }
        return count;
    }


    //TODO: There isnt a good way of getting the world (We can't just get it upon initialization) so perhaps it should be removed
    @JsonIgnore
    public ServerLevel world = null;

    private void setWorld(ServerLevel world) {
        if (this.world == null) this.world = world;
    }

    @JsonIgnore
    public Player seatedPlayer = null;

    @JsonIgnore
    private ControlData controlData = null;

    @JsonIgnore
    public LoadedServerShip ship = null;

    private void updateShipBounds() {
        boundx = ship.getShipAABB().maxX() - ship.getShipAABB().minX();
        boundz = ship.getShipAABB().maxZ() - ship.getShipAABB().minZ();
        LOGGER.info("Xbound=" + boundx + " Zbound=" + boundz);
    }

    public void updateShipDirection() {
        //TODO: Decide how to better estimate the ships "Forward" direction
        //Boundaries of the ship take precedence over the "preferred" direction
        //If a ship is longer than it is wide, it is more likely to be going in the direction of the longer side

        double major = Math.max(boundx, boundz);
        double minor = Math.min(boundx, boundz);

        // Ratio will be 1.0 for a perfect square, and higher as it gets "long"
        double ratio = (minor == 0) ? 0 : major / minor;


        if (boundx > boundz) {
            if (shipDirection != Direction.WEST && shipDirection != Direction.EAST) {
                //swap square and fna sails
                int x = numSquareSails;
                numSquareSails = numFnASails;
                numFnASails = x;
                LOGGER.info("Sail types swapped! New ship dir: " + shipDirection);
            }
            shipDirection = preferredDirection == Direction.EAST ? Direction.EAST : Direction.WEST;
        } else {
            if (shipDirection != Direction.SOUTH && shipDirection != Direction.NORTH) {
                //swap square and fna sails
                int x = numSquareSails;
                numSquareSails = numFnASails;
                numFnASails = x;
                LOGGER.info("Sail types swapped! New ship dir: " + shipDirection);
            }
            shipDirection = preferredDirection == Direction.NORTH ? Direction.NORTH : Direction.SOUTH;
        }
        LOGGER.info("Ship direction = {}; Ship ratio = {}", shipDirection.toString(), ratio);
    }

    @JsonIgnore
    public static KineticShipControl getOrCreate(LoadedServerShip ship, ServerLevel world) {
        if (ship != null) {
            if (ship.getAttachment(KineticShipControl.class) == null) {
                ship.setAttachment(KineticShipControl.class, new KineticShipControl());
            }
            KineticShipControl controller = ship.getAttachment(KineticShipControl.class);
            assert controller != null;
            controller.ship = ship;
            if (world != null) {
                controller.world = world;
            }

            if (ship.getShipAABB() != null) {
                controller.updateShipBounds();
            } else {
                controller.boundx = 0;
                controller.boundz = 0;
            }
            controller.updateShipDirection();
            return controller;
        } else {
            return null;
        }
    }

    public boolean isPlayerValid() {
        // Check if we have a player and if they are still riding a mounting entity
        return seatedPlayer != null &&
                seatedPlayer.getVehicle() instanceof ShipMountingEntity &&
                !isAnchored(); // Use your own anchor logic
    }

    @Override
    public void physTick(@NotNull PhysShip physShip, @NotNull PhysLevel physLevel) {
//        Createkinetic.LOGGER.debug("PHYS TICK");
        PhysShipImpl physShip1 = (PhysShipImpl) physShip;


        physShip1.setDoFluidDrag(true);
        physShip.setStatic(isAnchored());

        boolean validPlayer = isPlayerValid();
        if (validPlayer) {
            // Manually extract the inputs from the Minecraft Player object
            // xxa = left/right (A/D)
            // zza = forward/backward (W/S)
            // jja = up/down (Space/Shift)

            float leftImpulse = seatedPlayer.xxa;
            float forwardImpulse = seatedPlayer.zza;
            float upImpulse = seatedPlayer.yya;

            // Pass these values into your applyPlayerControl method
            this.controlData = new ControlData(
                    Direction.NORTH, // Or get the seat's direction
                    forwardImpulse,
                    leftImpulse,
                    upImpulse,
                    seatedPlayer.isSprinting()
            );


            applyPlayerControl(this.controlData, physShip);
        }

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

        if (helms > 0) physShip.applyRotDependentForce(keelForce);

        if (numEnchantedBallast > 0) {
            Vector3d shipUp = new Vector3d(0.0, 1.0, 0.0);
            Vector3d worldUp = new Vector3d(0.0, 1.0, 0.0);
            //todo possibly modify worldUp based on wind angle & numsails to make ship heel (should really do it separately)
            physShip1.getTransform().getShipToWorldRotation().transform(shipUp);

            double angleBetween = shipUp.angle(worldUp);
            Vector3d idealAngularAcceleration = new Vector3d(0, 0, 0);

            if (angleBetween > 0.01) {
                Vector3d stabilizationRotationAxisNormalized = shipUp.cross(worldUp, new Vector3d()).normalize();
                idealAngularAcceleration.add(stabilizationRotationAxisNormalized.mul(
                        angleBetween, stabilizationRotationAxisNormalized)
                );
            }

            Vector3dc omega = physShip1.getAngularVelocity();
            idealAngularAcceleration.sub(omega.x(), omega.y(), omega.z());

            Vector3d stabilizationTorque = physShip1.getTransform().getShipToWorldRotation().transform(
                    physShip1.getMomentOfInertia().transform(
                            physShip1.getTransform().getShipToWorldRotation().transformInverse(idealAngularAcceleration)
                    )
            );

            stabilizationTorque.mul(numEnchantedBallast * KineticConfig.enchantedBallastForce);
            physShip1.applyInvariantTorque(stabilizationTorque);

        }

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
                double windDirection = WindManager.getWindDirection(world, shipPosVec); //in degrees
                double windStrength = WindManager.getWindStrength(world, shipPosBlockPos)  // -1.0 -- 1.0
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
        if (shouldDispose()) {//Dispose of this ship if its no longer needed
            ship.removeAttachment(KineticShipControl.class);
        }
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
        return numBallast <= 0 && numFnASails <= 0 && numSquareSails <= 0 && numEnchantedBallast <= 0 && numBuoys <= 0 && helms == 0 && !frozen;
    }

    @SuppressWarnings("unchecked")
    private void applyPlayerControl(ControlData control, PhysShip physShip) {
        if (this.ship == null) return;

        final ShipTransform transform = physShip.getTransform();
        final org.joml.primitives.AABBdc aabb = this.ship.getWorldAABB();
        final Vector3dc center = transform.getPositionInWorld();

        // 1. Calculate Largest Distance for Turn Penalty
        double dist1 = center.distance(aabb.minX(), center.y(), aabb.minZ());
        double dist2 = center.distance(aabb.minX(), center.y(), aabb.maxZ());
        double dist3 = center.distance(aabb.maxX(), center.y(), aabb.minZ());
        double dist4 = center.distance(aabb.maxX(), center.y(), aabb.maxZ());

        double largestDistance = Math.max(Math.max(dist1, dist2), Math.max(dist3, dist4));

        // Equivalent to .coerceIn(0.5, maxSize)
        double maxSize = KineticConfig.maxSizeForTurnSpeedPenalty;
        largestDistance = Math.max(0.5, Math.min(largestDistance, maxSize));

        // 2. Physics Constants
        final Matrix3dc moiTensor = physShip.getMomentOfInertia();
        final Vector3dc omega = physShip.getAngularVelocity();

        double maxLinearAcceleration = KineticConfig.turnAcceleration;
        double extraForceAngular = 0.0;
        double maxLinearSpeed = KineticConfig.turnSpeed + extraForceAngular;

        // acceleration = alpha * r -> maxAlpha = maxAcceleration / r
        double maxOmegaY = maxLinearSpeed / largestDistance;
        double maxAlphaY = maxLinearAcceleration / largestDistance;

        boolean isBelowMaxTurnSpeed = Math.abs(omega.y()) < maxOmegaY;

        // 3. Determine Acceleration Multiplier
        double normalizedAlphaYMultiplier;
        if (isBelowMaxTurnSpeed && control.getLeftImpulse() != 0.0f) {
            normalizedAlphaYMultiplier = (double) control.getLeftImpulse();
        } else {
            // If not turning or over speed, apply counter-torque to stabilize
            normalizedAlphaYMultiplier = -Math.max(-1.0, Math.min(1.0, omega.y()));
        }

        double idealAlphaY = normalizedAlphaYMultiplier * maxAlphaY;
        // 4. Apply Torque (Rotation)
        Vector3d torque = new Vector3d(0.0, idealAlphaY, 0.0);
        moiTensor.transform(torque); // Applies the Moment of Inertia tensor to the vector

        // Add banking effect (leaning into the turn)
        torque.add(getPlayerControlledBanking(control, physShip, moiTensor, -idealAlphaY));

        physShip.applyWorldTorque(torque);

        // 5. Apply Force (Forward/Backward)
//        physShip.applyWorldForce(getPlayerForwardVel(control, physShip));
//        LOGGER.debug("Turn rotation: {}; ControlData: {}", idealAlphaY, this.controlData);
    }

    private Vector3d getPlayerControlledBanking(ControlData control, PhysShip physShip, Matrix3dc moiTensor, double strength) {
        // 1. Get the direction the seat is facing (e.g., North, East)
        // Assuming toJOMLD() converted a Vector3i or Direction.Normal to a Vector3d
        Vec3i normal = control.getSeatInDirection().getNormal();
        Vector3d rotationVector = new Vector3d(normal.getX(), normal.getY(), normal.getZ());

        // 2. Transform the local seat direction to world rotation
        physShip.getTransform().getShipToWorldRotation().transform(rotationVector);

        // 3. Project onto the horizontal plane and apply strength
        rotationVector.y = 0.0;
        rotationVector.mul(strength * 1.5);

        // 4. Transform into Ship Space, apply Moment of Inertia, and transform back to World Space
        // This is the JOML equivalent of the nested Kotlin calls
        Quaterniondc shipToWorld = physShip.getTransform().getShipToWorldRotation();

        // transformInverse -> moiTensor.transform -> transform
        shipToWorld.transformInverse(rotationVector);
        moiTensor.transform(rotationVector);
        shipToWorld.transform(rotationVector);

        return rotationVector;
    }


    @Override
    public void onServerTick() {
//        Createkinetic.LOGGER.debug("SERVER TICK");
    }

    public void countAndRemoveSails() {
        CreateKinetic.LOGGER.debug("SAIL COUNT Square: {}, FNA: {}", numSquareSails, numFnASails);
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
