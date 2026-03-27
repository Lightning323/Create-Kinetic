package com.lightning323.createkinetic.ship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.lightning323.createkinetic.CreateKinetic;
import com.lightning323.createkinetic.KineticConfig;
import com.lightning323.createkinetic.blocks.ballastTank.BallastTankBlockEntity;
import com.lightning323.createkinetic.blocks.sail.SailClothBlock;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
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


    public int helms = 0;
    public volatile double waterAmount = 0.0;
    public int boundx = 1;
    public int boundz = 1;

    /**
     * BALLAST / BUOY WEIGHT
     */
    //Each tank ballast can be at a different location or can change locations, we use a set to store all the locations
    //If a ballast is moved, they find their old location and Assign it to the new location
    @JsonIgnore
    private LongSet tankBallastLocations = new LongOpenHashSet();

    @JsonProperty("tankBallastLocations") //The Getter (Serialization)
    public long[] getTankBallastLocations() {
        return tankBallastLocations.toLongArray();
    }

    @JsonProperty("tankBallastLocations") //The Setter/Constructor (Deserialization)
    public void setTankBallastLocations(long[] data) {
        this.tankBallastLocations.clear();
        if (data != null) {
            for (long p : data) this.tankBallastLocations.add(p);
        }
    }

    public void addTankBallastLocation(BlockPos pos) {
        tankBallastLocations.add(pos.asLong());
    }

    public void removeTankBallastLocation(BlockPos pos) {
        tankBallastLocations.remove(pos.asLong());
    }

    public void reassignTankBallastLocation(BlockPos lastKnownPos, BlockPos newLoc) {
        if (tankBallastLocations.remove(lastKnownPos.asLong())) {
            tankBallastLocations.add(newLoc.asLong());
        }
    }

    public int numBallast = 0;
    public int numEnchantedBallast = 0;
    public int numBuoys = 0;
    public float tankBallastWeight = 0;

    public void updateBallastWeights() {
        tankBallastWeight = 0;
        for (long location : tankBallastLocations) {
            BlockPos pos = BlockPos.of(location);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BallastTankBlockEntity tbe) {
                tankBallastWeight += tbe.getWeight();
            }
        }
        LOGGER.debug("Tank ballast weight: {}", tankBallastWeight);
    }

    /**
     * SAIL COUNT
     */
    //TODO: Wind is 2D, meaning sails placed flat wont catch wind. Should this be changed?

    @JsonIgnore
    public LongSet sailsX = new LongOpenHashSet();

    @JsonIgnore
    public LongSet sailsZ = new LongOpenHashSet();

    //The Getter (Serialization)
    @JsonProperty("sailsX")
    public long[] getJsonSailPulleysX() {
        return sailsX.toLongArray();
    }

    @JsonProperty("sailsZ")
    public long[] getJsonSailPulleysZ() {
        return sailsZ.toLongArray();
    }

    //The Setter/Constructor (Deserialization)
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

    @JsonIgnore
    public Player seatedPlayer = null;

    @JsonIgnore
    private ControlData controlData = null;

    @JsonIgnore
    public LoadedServerShip ship = null;
    @JsonIgnore
    private static final Vector3d up = new Vector3d(0.0, 1.0, 0.0);

    private void updateShipBounds() {
        boundx = ship.getShipAABB().maxX() - ship.getShipAABB().minX();
        boundz = ship.getShipAABB().maxZ() - ship.getShipAABB().minZ();
        LOGGER.debug("Bounds X={} Z={}", boundx, boundz);
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
                LOGGER.debug("Sail types swapped!");
            }
            shipDirection = preferredDirection == Direction.EAST ? Direction.EAST : Direction.WEST;
        } else {
            if (shipDirection != Direction.SOUTH && shipDirection != Direction.NORTH) {
                //swap square and fna sails
                int x = numSquareSails;
                numSquareSails = numFnASails;
                numFnASails = x;
                LOGGER.debug("Sail types swapped!");
            }
            shipDirection = preferredDirection == Direction.NORTH ? Direction.NORTH : Direction.SOUTH;
        }
        LOGGER.debug("Ship direction = {}; Ship ratio = {}", shipDirection.toString(), ratio);
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
        if (isAnchored()) {
            physShip.setStatic(true);
            return; //If we are static, skip the rest of the tick
        } else {
            physShip.setStatic(false);
        }
        PhysShipImpl physShip1 = (PhysShipImpl) physShip;
        physShip1.setDoFluidDrag(true);
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
            keelForce = new Vector3d(0, 0, force.z() * KineticConfig.keelStrength);
        }

        if (helms > 0) physShip.applyRotDependentForce(keelForce);

        if (numEnchantedBallast > 0) {
            Vector3d worldUp = new Vector3d(0, 1, 0);
            Vector3d shipUp = new Vector3d(0, 1, 0);
            physShip1.getTransform().getShipToWorldRotation().transform(shipUp);

            double angleBetween = shipUp.angle(worldUp);

            if (angleBetween > 0.002) {//Right the ship
                Vector3d rotationAxis = new Vector3d();
                shipUp.cross(worldUp, rotationAxis);
                rotationAxis.normalize();

                // 1. Proportional Gain (How hard it pulls back based on angle)
                double pStrength = numEnchantedBallast * KineticConfig.enchantedBallastForce;
                Vector3d restorationTorque = new Vector3d(rotationAxis).mul(angleBetween * pStrength);

                // 2. Derivative Gain (Damping - resists current spinning)
                // Without this, the ship will wobble like a bobblehead.
                Vector3dc currentOmega = physShip1.getAngularVelocity();
                double dampingStrength = pStrength * 0.5; // Start with half of P strength
                Vector3d dampingTorque = new Vector3d(currentOmega).mul(-dampingStrength);

                // 3. Combine
                Vector3d totalTorque = restorationTorque.add(dampingTorque);

                // 4. Transform through Inertia (Crucial for heavy ships)
                Matrix3dc inertia = physShip1.getMomentOfInertia();
                Quaterniondc shipRot = physShip1.getTransform().getShipToWorldRotation();

                shipRot.transformInverse(totalTorque);
                inertia.transform(totalTorque);
                shipRot.transform(totalTorque);
//                LOGGER.debug("Applying enchanted ballast torque {}, strength: {}", totalTorque,pStrength);
                physShip1.applyInvariantTorque(totalTorque);
            }
        }

        if (numBallast > 0 || numBuoys > 0 || tankBallastWeight > 0) {
            physShip1.setBuoyantFactor(0.0//1.0
                    + (numBuoys * KineticConfig.buoyFloatStrength)
                    + (numBallast * KineticConfig.ballastFloatStrength)
                    - (tankBallastWeight * KineticConfig.tankBallastWeight) //Negative boyancy results in increased mass
            );
        }

        //sail force implementation
        if (numSquareSails > 0 || numFnASails > 0) {
            Vector3d sailForce = new Vector3d(
                    shipDirection.getNormal().getX(), shipDirection.getNormal().getY(), shipDirection.getNormal().getZ()
            );

//            Vector3dc worldShipPos = physShip1.getTransform().getPositionInWorld();
            //Get the position of our ship
//            Vec3 shipPosVec = new Vec3(worldShipPos.x(), worldShipPos.y(), worldShipPos.z());
//            BlockPos shipPosBlockPos = new BlockPos((int) worldShipPos.x(), (int) worldShipPos.y(), (int) worldShipPos.z());

            //Get the wind parameters
            double windDirection = WindManager.getWindDirection(); //in degrees
            double windStrength = WindManager.getWindStrength();  // -1.0 -- 1.0

            //Get the Y angle of our ship in radians
            double shipAngle = getShipYaw(physShip1.getTransform().getShipToWorldRotation()); //in radians

            double windAngle = windDirection + 90 % 360;
            double squareAngleBetween;
            double fnaAngleBetween;
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
        // Prevent division by zero
        if (noSail == 0) return 1.0;

        if (KineticConfig.forgivingSails) {
            //Forgiving sails doesnt stop the ship so agressively when going against the wind
            double ratio = windAngle / noSail;
            return Math.pow(2, ratio) + Math.pow(2, -ratio);
        }

        double ratioSq = (windAngle * windAngle) / noSail;
        return Math.pow(2, ratioSq) + Math.pow(2, -ratioSq);
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
