package com.lightning323.createkinetic.blocks.rudder;

import com.lightning323.createkinetic.registries.KineticBlockEntities;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

public class RudderBlock extends DirectionalKineticBlock implements IBE<RudderBlockEntity>, TransformableBlock {
    public RudderBlock(Properties properties) {
        super(properties);
    }

    //Important for identifying the block entity
    @Override
    public Class<RudderBlockEntity> getBlockEntityClass() {
        return RudderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RudderBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.RUDDER.get();
    }


    /**
     * Rotation / orientation related stuff
     * Taken from directionalAxisKinetic block
     * <p>
     * The reason why we implement this ourselves
     * 1) We need direction long first coordinate instead of an axis
     */
    public static NonNullBiConsumer<DataGenContext<Block, RudderBlock>, RegistrateBlockstateProvider> getBlockstateDefinition() {
        return (c, p) -> {
            // Reference your base model
            ModelFile model = p.models().getExistingFile(p.modLoc("block/rudder/base"));

            p.getVariantBuilder(c.get()).forAllStates(state -> {
                Direction facing = state.getValue(DirectionalKineticBlock.FACING);
                boolean alongFirst = state.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);

                int xRot = 0;
                int yRot = (int) facing.toYRot();

                return ConfiguredModel.builder()
                        .modelFile(model)
                        .rotationX(xRot)
                        .rotationY(yRot)
                        .build();
            });
        };
    }

    public static final BooleanProperty AXIS_ALONG_FIRST_COORDINATE = BooleanProperty.create("axis_along_first");

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS_ALONG_FIRST_COORDINATE);
        super.createBlockStateDefinition(builder);
    }

    protected Direction getFacingForPlacement(BlockPlaceContext context) {
        Direction facing = context.getNearestLookingDirection()
                .getOpposite();
        if (context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown())
            facing = facing.getOpposite();
        return facing;
    }

    protected boolean getAxisAlignmentForPlacement(BlockPlaceContext context) {
        return context.getHorizontalDirection()
                .getAxis() == Direction.Axis.X;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = getFacingForPlacement(context);
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        boolean alongFirst = false;
        Direction.Axis faceAxis = facing.getAxis();

        if (faceAxis.isHorizontal()) {
            alongFirst = faceAxis == Direction.Axis.Z;
            Direction positivePerpendicular = faceAxis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;

            boolean shaftAbove = prefersConnectionTo(world, pos, Direction.UP, true);
            boolean shaftBelow = prefersConnectionTo(world, pos, Direction.DOWN, true);
            boolean preferLeft = prefersConnectionTo(world, pos, positivePerpendicular, false);
            boolean preferRight = prefersConnectionTo(world, pos, positivePerpendicular.getOpposite(), false);

            if (shaftAbove || shaftBelow || preferLeft || preferRight)
                alongFirst = faceAxis == Direction.Axis.X;
        }

        if (faceAxis.isVertical()) {
            alongFirst = getAxisAlignmentForPlacement(context);
            Direction prefferedSide = null;

            for (Direction side : Iterate.horizontalDirections) {
                if (!prefersConnectionTo(world, pos, side, true)
                        && !prefersConnectionTo(world, pos, side.getClockWise(), false))
                    continue;
                if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
                    prefferedSide = null;
                    break;
                }
                prefferedSide = side;
            }

            if (prefferedSide != null)
                alongFirst = prefferedSide.getAxis() == Direction.Axis.X;
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(AXIS_ALONG_FIRST_COORDINATE, alongFirst);
    }

    protected boolean prefersConnectionTo(LevelReader reader, BlockPos pos, Direction facing, boolean shaftAxis) {
        if (!shaftAxis)
            return false;
        BlockPos neighbourPos = pos.relative(facing);
        BlockState blockState = reader.getBlockState(neighbourPos);
        Block block = blockState.getBlock();
        return block instanceof IRotate
                && ((IRotate) block).hasShaftTowards(reader, neighbourPos, blockState, facing.getOpposite());
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        Direction.Axis pistonAxis = state.getValue(FACING)
                .getAxis();
        boolean alongFirst = state.getValue(AXIS_ALONG_FIRST_COORDINATE);

        if (pistonAxis == Direction.Axis.X)
            return alongFirst ? Direction.Axis.Y : Direction.Axis.Z;
        if (pistonAxis == Direction.Axis.Y)
            return alongFirst ? Direction.Axis.X : Direction.Axis.Z;
        if (pistonAxis == Direction.Axis.Z)
            return alongFirst ? Direction.Axis.X : Direction.Axis.Y;

        throw new IllegalStateException("Unknown axis??");
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        if (rot.ordinal() % 2 == 1)
            state = state.cycle(AXIS_ALONG_FIRST_COORDINATE);
        return super.rotate(state, rot);
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.mirror != null) {
            state = mirror(state, transform.mirror);
        }

        if (transform.rotationAxis == Direction.Axis.Y) {
            return rotate(state, transform.rotation);
        }

        Direction newFacing = transform.rotateFacing(state.getValue(FACING));
        if (transform.rotationAxis == newFacing.getAxis() && transform.rotation.ordinal() % 2 == 1) {
            state = state.cycle(AXIS_ALONG_FIRST_COORDINATE);
        }
        return state.setValue(FACING, newFacing);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == getRotationAxis(state);
    }
}