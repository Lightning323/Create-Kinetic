package com.lightning323.createkinetic.blocks.rudder;

import com.lightning323.createkinetic.registries.KineticBlockEntities;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

public class RudderBlock extends DirectionalKineticBlock implements IBE<RudderBlockEntity> {
    //, TransformableBlock {
    public RudderBlock(Properties properties) {
        super(properties);
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClientSide) {
            return;
        }
        KineticShipControl controller = KineticShipControl.getOrAddController((ServerLevel) world, pos);
        if (controller != null) controller.addRudder(pos);
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.isClientSide) {
            return;
        }
        if (newState.isAir() || !newState.is(state.getBlock())) {
            KineticShipControl controller = KineticShipControl.getOrAddController((ServerLevel) world, pos);
            if (controller != null) controller.removeRudder(pos);
        }
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

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING).getOpposite();
    }

    /**
     * Rotation / orientation related stuff
     * <p>
     * DIRECTION - tells us the direction of the block
     * FACE_ANGLE - tells us the angle of the block on the face, this is purely aesthetic and serves no functional purpose
     *
     */
    public static NonNullBiConsumer<DataGenContext<Block, RudderBlock>, RegistrateBlockstateProvider> getBlockstateDefinition() {
        return (c, p) -> {
            // Reference your base model
            ModelFile model = p.models().getExistingFile(p.modLoc("block/rudder/base"));

            p.getVariantBuilder(c.get()).forAllStates(state -> {
                Direction facing = state.getValue(DirectionalKineticBlock.FACING);
//                boolean alongFirst = state.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);

                int xRot = 90;
                int yRot = 0;

                switch (facing) {
                    case SOUTH:
                        yRot = 180;
                        break;
                    case WEST:
                        yRot = 270;
                        break;
                    case EAST:
                        yRot = 90;
                        break;
                    case UP:
                        xRot = 270 + 90;
                        break;
                    case DOWN:
                        xRot = 90 + 90;
                        break;
                }

                return ConfiguredModel.builder()
                        .modelFile(model)
                        .rotationX(xRot)
                        .rotationY(yRot)
                        .build();
            });
        };
    }

//    /**
//     * This is for transformable blocks, it changes the block state based on the transform
//     * We use this to wrench the block in different directions
//     *
//     * @param state
//     * @param transform
//     * @return
//     */
//    @Override
//    public BlockState transform(BlockState state, StructureTransform transform) {
//        if (transform.mirror != null) {
//            state = mirror(state, transform.mirror);
//        }
//
//        if (transform.rotationAxis == Direction.Axis.Y) {
//            return rotate(state, transform.rotation);
//        }
//
//        Direction newFacing = transform.rotateFacing(state.getValue(FACING));
////        if (transform.rotationAxis == newFacing.getAxis() && transform.rotation.ordinal() % 2 == 1) {
////            state = state.cycle(AXIS_ALONG_FIRST_COORDINATE);
////        }
//        return state.setValue(FACING, newFacing);
//    }

    //TODO: ADD LATER
//    public static final BooleanProperty AXIS_ALONG_FIRST_COORDINATE = BooleanProperty.create("axis_along_first");
//
//    @Override
//    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
//        builder.add(AXIS_ALONG_FIRST_COORDINATE);
//        super.createBlockStateDefinition(builder);
//    }


//    @Override
//    public BlockState rotate(BlockState state, Rotation rot) {
//        if (rot.ordinal() % 2 == 1)
//            state = state.cycle(AXIS_ALONG_FIRST_COORDINATE);
//        return super.rotate(state, rot);
//    }
//

}