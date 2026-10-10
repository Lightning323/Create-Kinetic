package org.lightning323.createkinetic.content.blocks.analog_gearshift;

import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AnalogGearshiftBlockEntity extends SplitShaftBlockEntity {
    private int leftSignal = 0;
    private int rightSignal = 0;

    public AnalogGearshiftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            int newLeft = getAnalogSignal(state, true);
            int newRight = getAnalogSignal(state, false);
            if (newLeft != leftSignal || newRight != rightSignal) {
                detachKinetics();
                removeSource();
                leftSignal = newLeft;
                rightSignal = newRight;
                setChanged();
                BlockState newState = state.setValue(AnalogGearshiftBlock.LEFT_POWERED, newLeft > 0)
                        .setValue(AnalogGearshiftBlock.RIGHT_POWERED, newRight > 0);
                if (newState != state) {
                    level.setBlockAndUpdate(worldPosition, newState);
                }
                sendData();
                attachKinetics();
            }
        }
        super.tick();
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource()) {
            if (face == getSourceFacing()) {
                return 1.0F;
            }
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