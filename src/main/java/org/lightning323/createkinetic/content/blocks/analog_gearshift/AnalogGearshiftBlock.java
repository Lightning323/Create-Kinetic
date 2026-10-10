package org.lightning323.createkinetic.content.blocks.analog_gearshift;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.Nullable;
import org.lightning323.createkinetic.registries.KineticBlockEntities;

public class AnalogGearshiftBlock extends DirectionalAxisKineticBlock implements IBE<AnalogGearshiftBlockEntity>, IRotate {
    public static final BooleanProperty LEFT_POWERED = BooleanProperty.create("left_powered");
    public static final BooleanProperty RIGHT_POWERED = BooleanProperty.create("right_powered");

    public AnalogGearshiftBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(LEFT_POWERED, false).setValue(RIGHT_POWERED, false));
    }

    @Override
    public BlockState updateAfterWrenched(BlockState newState, UseOnContext context) {
        return super.updateAfterWrenched(getPoweredState(context.getLevel(), newState, context.getClickedPos()), context);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(LEFT_POWERED).add(RIGHT_POWERED));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction lookingDirection = context.getNearestLookingDirection();
        boolean shiftKeyDown = context.getPlayer().isShiftKeyDown();
        Axis preferredAxis = RotatedPillarKineticBlock.getPreferredAxis(context);
        boolean axisAlongFirst = false;
        Direction facing;
        if (preferredAxis != null && preferredAxis != lookingDirection.getAxis() && !shiftKeyDown) {
            facing = lookingDirection;
            if (preferredAxis == Axis.X) {
                axisAlongFirst = true;
            } else if (preferredAxis == Axis.Y && lookingDirection.getAxis() == Axis.X) {
                axisAlongFirst = true;
            }
        } else if (lookingDirection.getAxis().isHorizontal()) {
            facing = lookingDirection.getCounterClockWise();
            if (lookingDirection.getAxis() == Axis.X) {
                axisAlongFirst = true;
            }
        } else {
            facing = context.getHorizontalDirection().getCounterClockWise();
            if (context.getHorizontalDirection().getAxis() == Axis.Z) {
                axisAlongFirst = true;
            }
        }

        if (shiftKeyDown) {
            facing = facing.getOpposite();
        }

        BlockState state = defaultBlockState().setValue(FACING, facing)
                .setValue(AXIS_ALONG_FIRST_COORDINATE, axisAlongFirst);
        return getPoweredState(context.getLevel(), state, context.getClickedPos());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            boolean previouslyLeftPowered = state.getValue(LEFT_POWERED);
            boolean previouslyRightPowered = state.getValue(RIGHT_POWERED);
            BlockState newState = getPoweredState(level, state, pos);
            if (previouslyLeftPowered != newState.getValue(LEFT_POWERED) || previouslyRightPowered != newState.getValue(RIGHT_POWERED)) {
                detachKinetics(level, pos, true);
                level.setBlock(pos, newState, 2);
            }
        }
    }

    public BlockState getPoweredState(Level level, BlockState state, BlockPos pos) {
        Direction leftDirection = getLeftDirection(state);
        Direction rightDirection = getRightDirection(state);
        int leftSignal = level.getSignal(pos.offset(leftDirection.getNormal()), leftDirection);
        int rightSignal = level.getSignal(pos.offset(rightDirection.getNormal()), rightDirection);
        boolean previouslyLeftPowered = state.getValue(LEFT_POWERED);
        if (previouslyLeftPowered != leftSignal > 0) {
            state = state.cycle(LEFT_POWERED);
        }

        boolean previouslyRightPowered = state.getValue(RIGHT_POWERED);
        if (previouslyRightPowered != rightSignal > 0) {
            state = state.cycle(RIGHT_POWERED);
        }

        return state;
    }

    public void detachKinetics(Level worldIn, BlockPos pos, boolean reAttachNextTick) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be instanceof KineticBlockEntity) {
            RotationPropagator.handleRemoved(worldIn, pos, (KineticBlockEntity) be);
            if (reAttachNextTick) {
                worldIn.scheduleTick(pos, this, 1, TickPriority.EXTREMELY_HIGH);
            }
        }
    }

    public static Direction getLeftDirection(BlockState state) {
        return state.getValue(FACING);
    }

    public static Direction getRightDirection(BlockState state) {
        return getLeftDirection(state).getOpposite();
    }

    @Override
    public Class<AnalogGearshiftBlockEntity> getBlockEntityClass() {
        return AnalogGearshiftBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AnalogGearshiftBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.ANALOG_GEARSHIFT.get();
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        if (worldIn.getBlockEntity(pos) instanceof KineticBlockEntity kte) {
            RotationPropagator.handleAdded(worldIn, pos, kte);
        }
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        InteractionResult interactionResult = super.onWrenched(state, context);
        if (interactionResult.consumesAction() && !context.getLevel().isClientSide) {
            detachKinetics(context.getLevel(), context.getClickedPos(), true);
        }

        return interactionResult;
    }
}