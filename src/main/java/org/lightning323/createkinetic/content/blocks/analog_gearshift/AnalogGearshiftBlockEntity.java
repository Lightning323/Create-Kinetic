package org.lightning323.createkinetic.content.blocks.analog_gearshift;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

public class AnalogGearshiftBlockEntity extends SplitShaftBlockEntity {
    private int leftSignal = 0;
    private int rightSignal = 0;

    public AnalogGearshiftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // No tick() method! Fully event-driven.

    /**
     * Called by the Block when a neighbor changes. Evaluates exact 0-15 signals.
     */
    public void updateSignals() {
        if (level == null || level.isClientSide) {
            return;
        }

        BlockState state = getBlockState();
        int newLeft = getAnalogSignal(state, true);
        int newRight = getAnalogSignal(state, false);

        if (newLeft != leftSignal || newRight != rightSignal) {
            // Detach kinetics before changing rotation modifiers
            detachKinetics();
            removeSource();

            leftSignal = newLeft;
            rightSignal = newRight;
            setChanged();

            // Update blockstate booleans for client rendering/visuals
            BlockState newState = state.setValue(AnalogGearshiftBlock.LEFT_POWERED, leftSignal > 0)
                    .setValue(AnalogGearshiftBlock.RIGHT_POWERED, rightSignal > 0);

            if (newState != state) {
                level.setBlock(worldPosition, newState, 2); // Flag 2: Update clients without neighbor spam
            }

            sendData();

            // Re-attach kinetics next tick via RotationPropagator
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                RotationPropagator.handleRemoved(serverLevel, worldPosition, this);
                serverLevel.scheduleTick(worldPosition, getBlockState().getBlock(), 1, TickPriority.EXTREMELY_HIGH);
            }
        }
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource()) {
            if (face == getSourceFacing()) {
                return 1.0F;
            }
            // Full analog calculation using exact 0-15 signal strength
            return (leftSignal - rightSignal) / 15.0F;
        }
        return 0.0F;
    }

    private int getAnalogSignal(BlockState state, boolean left) {
        Direction side = left
                ? AnalogGearshiftBlock.getLeftDirection(state)
                : AnalogGearshiftBlock.getRightDirection(state);
        return level.getSignal(worldPosition.offset(side.getNormal()), side);
    }

    public void detachKinetics() {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            RotationPropagator.handleRemoved(serverLevel, worldPosition, this);
        }
    }

    @Override
    protected void write(CompoundTag compound, Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("LeftSignal", leftSignal);
        compound.putInt("RightSignal", rightSignal);
    }

    @Override
    protected void read(CompoundTag compound, Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        leftSignal = compound.getInt("LeftSignal");
        rightSignal = compound.getInt("RightSignal");
    }
}