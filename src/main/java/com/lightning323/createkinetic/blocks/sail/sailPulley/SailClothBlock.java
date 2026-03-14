package com.lightning323.createkinetic.blocks.sail.sailPulley;

import com.lightning323.createkinetic.registries.KineticShapes;
import com.lightning323.createkinetic.ship.ShipUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SailClothBlock extends SailPulleyBlock.SailBlockBase {

    public SailClothBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);

        // Check if it's actually a new block type (not just a state change like a property update)
        if (!state.is(oldState.getBlock())) {

            //TODO: The block has a flipped axis, Lets implement a better solution when we have time
            Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            Direction.Axis flippedAxis = (axis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;
            ShipUtils.addSail(world, pos, flippedAxis);
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, worldIn, pos, newState, isMoving);

        //TODO: The block has a flipped axis, Lets implement a better solution when we have time
        Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        Direction.Axis flippedAxis = (axis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;
        ShipUtils.removeSail(worldIn, pos, flippedAxis);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        return KineticShapes.SAIL_CLOTH.get(axis);
    }
}
