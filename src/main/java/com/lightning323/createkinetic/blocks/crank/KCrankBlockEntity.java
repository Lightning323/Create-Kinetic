package com.lightning323.createkinetic.blocks.crank;

import com.lightning323.createkinetic.registries.KineticBlocks;
import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KCrankBlockEntity extends GeneratingKineticBlockEntity {

    public int inUse;
    public boolean backwards;
    /**
     * In degrees
     */
    public float independentAngle;
    public float chasingAngularVelocity;
    private float automaticImpulse;

    public ControlMode getControlMode() {
        return ((KCrankBlock) getBlockState().getBlock()).controlMode;
    }

    public void setAutomaticImpulse(float value) {
        if (automaticImpulse != value) {
            automaticImpulse = value;
            updateGeneratedRotation();
        }
    }

    public KCrankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void turn(boolean back) {
        boolean update = false;

        if (getGeneratedSpeed() == 0 || back != backwards)
            update = true;

        inUse = 10;
        this.backwards = back;
        if (update && !level.isClientSide)
            updateGeneratedRotation();
    }

    /**
     * In degrees
     */
    public float getIndependentAngle(float partialTicks) {
        return independentAngle + partialTicks * chasingAngularVelocity;
    }

    @Override
    public float getGeneratedSpeed() {
        Block block = getBlockState().getBlock();
        if (!(block instanceof KCrankBlock crank))
            return 0;
        int manualMovement = (inUse == 0 ? 0 : clockwise() ? -1 : 1);
        float speed = (manualMovement + automaticImpulse) * crank.getRotationSpeed();
        return convertToDirection(speed, getBlockState().getValue(KCrankBlock.FACING));
    }

    protected boolean clockwise() {
        return backwards;
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("InUse", inUse);
        compound.putBoolean("Backwards", backwards);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        inUse = compound.getInt("InUse");
        backwards = compound.getBoolean("Backwards");
        super.read(compound, clientPacket);
    }

    @Override
    public void tick() {
        super.tick();

        float actualAngularSpeed = KineticBlockEntity.convertToAngular(getSpeed());
        chasingAngularVelocity += (actualAngularSpeed - chasingAngularVelocity) / 4f;
        independentAngle += chasingAngularVelocity;

        if (inUse > 0) {
            inUse--;

            if (inUse == 0 && !level.isClientSide) {
                sequenceContext = null;
                updateGeneratedRotation();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public SuperByteBuffer getRenderedHandle() {
        BlockState blockState = getBlockState();
        Direction facing = blockState.getOptionalValue(KCrankBlock.FACING)
                .orElse(Direction.UP);
        return CachedBuffers.partialFacing(KineticPartialModels.BRASS_CRANK_HANDLE, blockState, facing.getOpposite());
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderShaft() {
        return true;
    }

    @Override
    protected Block getStressConfigKey() {
        return KineticBlocks.FORWARD_CRANK.has(getBlockState()) ? KineticBlocks.FORWARD_CRANK.get()
                : AllBlocks.COPPER_VALVE_HANDLE.get();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();
        if (inUse > 0 && AnimationTickHolder.getTicks() % 10 == 0) {
            if (!KineticBlocks.FORWARD_CRANK.has(getBlockState()))
                return;
            AllSoundEvents.CRANKING.playAt(level, worldPosition, (inUse) / 2.5f, .65f + (10 - inUse) / 10f, true);
        }
    }

}
