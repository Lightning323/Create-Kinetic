package com.lightning323.createkinetic.blocks.shipHelm;

import com.lightning323.createkinetic.CreateKinetic;
import com.lightning323.createkinetic.blocks.crank.KCrankBlockEntity;
import com.lightning323.createkinetic.registries.KineticPackets;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toDoubles;

public class ShipHelmBlockEntity extends GeneratingKineticBlockEntity {

    // For the renderer
    public float renderHelmRotation = 0.0f;
    public double controlImpulse = 0.0;

    public static final float SPEED = 64;

    private final List<ShipMountingEntity> seats = new ArrayList<>();
    private final List<GeneratingKineticBlockEntity> controls_forwardBackward = new ArrayList<>();
    private final List<GeneratingKineticBlockEntity> controls_upDown = new ArrayList<>();

    public ShipHelmBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
            updateGeneratedRotation();
    }

    @Override
    public float getGeneratedSpeed() {
        return convertToDirection((float) (controlImpulse * SPEED), getBlockState().getValue(ShipHelmBlock.HORIZONTAL_FACING));
    }

    private LoadedServerShip getShip() {
        if (!(getLevel() instanceof ServerLevel serverLevel)) return null;
        return VSGameUtilsKt.getLoadedShipManagingPos(serverLevel, getBlockPos());
    }

    private KineticShipControl getShipController() {
        LoadedServerShip ship = getShip();
        if (ship == null) return null;
        return ship.getAttachment(KineticShipControl.class);
    }

    public ShipMountingEntity spawnSeat(BlockPos blockPos, BlockState state, ServerLevel level) {
        BlockPos newPos = blockPos.relative(state.getValue(HorizontalDirectionalBlock.FACING));
        BlockState newState = level.getBlockState(newPos);
        double height = 0.0;

        if (!newState.isAir()) {
            if (newState.getBlock() instanceof StairBlock &&
                    (!newState.hasProperty(StairBlock.HALF) || newState.getValue(StairBlock.HALF) == Half.BOTTOM)) {
                height = 0.5;
            } else {
                height = newState.getShape(level, newPos).max(Axis.Y);
            }
        } else {
            BlockState stateBelow = level.getBlockState(new BlockPos(newPos.getX(), newPos.getY() - 1, newPos.getZ()));
            double shapeHeight = stateBelow.getShape(level, newPos).max(Axis.Y);
            if (shapeHeight >= 0.5 && shapeHeight < 1.0) {
                height = shapeHeight - 1.0;
            }
        }

        ShipMountingEntity entity = ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE.create(level);
        assert entity != null;

        Vec3 offset;
        if (height > 0.15) {
            Vec3 facingNormal = toDoubles(state.getValue(HorizontalDirectionalBlock.FACING).getNormal());
            offset = facingNormal.scale(-0.1).add(0.5, height - 0.5, 0.5);
        } else {
            offset = new Vec3(0.5, height + 0.1, 0.5);
        }

        Vector3dc seatEntityPos = new Vector3d(
                newPos.getX() + offset.x,
                newPos.getY() + offset.y,
                newPos.getZ() + offset.z
        );

        entity.moveTo(seatEntityPos.x(), seatEntityPos.y(), seatEntityPos.z());
        entity.lookAt(
                EntityAnchorArgument.Anchor.EYES,
                toDoubles(state.getValue(BlockStateProperties.HORIZONTAL_FACING).getNormal())
                        .add(entity.position())
        );
        entity.setController(true);

        level.addFreshEntityWithPassengers(entity);
        return entity;
    }

    Player seatedPlayer;

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        List<Long> additionalControls = new ArrayList<Long>();
        for (GeneratingKineticBlockEntity be : controls_forwardBackward) {
            additionalControls.add(be.getBlockPos().asLong());
        }
        for (GeneratingKineticBlockEntity be : controls_upDown) {
            additionalControls.add(be.getBlockPos().asLong());
        }
        compound.putLongArray("additionalControls", additionalControls);

        if (seatedPlayer != null) {
            compound.putUUID("seatedPlayer", seatedPlayer.getUUID());
        }

        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        if (compound.contains("additionalControls")) {
            if(level==null) return;
            long[] controlPositions = compound.getLongArray("additionalControls");
            controls_forwardBackward.clear();
            controls_upDown.clear();
            for (long p : controlPositions) {
                BlockPos pos = BlockPos.of(p);
                checkAndAddControls(level, pos, 0, 0, 0);
                checkAndAddControls(level, pos, 0, 0, 0);
            }
        }
        if (compound.contains("seatedPlayer")) {
            UUID uuid = compound.getUUID("seatedPlayer");
            if (level instanceof ServerLevel serverLevel) {
                seatedPlayer = serverLevel.getPlayerByUUID(uuid);
            } else if (level instanceof ClientLevel clientLevel) {
                seatedPlayer = Minecraft.getInstance().level.getPlayerByUUID(uuid);
            }
        }
        super.read(compound, clientPacket);
    }

    public void updateControl(HelmControlData controlData) {
        if (controlData != null) {
            controlImpulse = controlData.leftImpulse;
            updateGeneratedRotation();

            for (GeneratingKineticBlockEntity be : controls_forwardBackward) {
                if (be instanceof KCrankBlockEntity kbe) {
                    kbe.setAutomaticImpulse(controlData.forwardImpulse);
                }
            }
            for (GeneratingKineticBlockEntity be : controls_upDown) {
                if (be instanceof KCrankBlockEntity kbe) {
                    kbe.setAutomaticImpulse(controlData.upImpulse);
                }
            }
        }
    }

    @Override
    public void remove() {
        if (level != null && !level.isClientSide) {
            for (ShipMountingEntity seat : seats) {
                seat.kill();
            }
            seats.clear();
        }
        super.remove();
    }

    public boolean sit(ServerPlayer player, BlockState state, Level level, BlockPos pos, boolean force) {
        //Runs on server
        ShipMountingEntity seat = spawnSeat(getBlockPos(), getBlockState(), (ServerLevel) level);
        Direction direction = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        CreateKinetic.LOGGER.debug("Helm seating direction: {}", direction);

        KineticShipControl control = getShipController();
        if (control != null) {
            control.preferredDirection = direction;
            control.updateShipDirection();
            control.seatedPlayer = player;
        }

        controls_forwardBackward.clear();
        controls_upDown.clear();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                checkAndAddControls(level, pos, x, 0, z);
                checkAndAddControls(level, pos, x, 1, z);
            }
        }

        //Send a packet to the client
        KineticPackets.PLAYER_RIDING.sendToClient(new PlayerRidingPacket(pos), player);
        return player.startRiding(seat, force);
    }

    private void checkAndAddControls(Level level, BlockPos pos, int x, int y, int z) {
        BlockPos pos2 = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
        BlockEntity e = level.getBlockEntity(pos2);
        if (e instanceof KCrankBlockEntity kbe) {
            switch (kbe.getControlMode()) {
                case FORWARD_BACKWARD -> controls_forwardBackward.add(kbe);
                case UP_DOWN -> controls_upDown.add(kbe);
            }
        }
    }
}