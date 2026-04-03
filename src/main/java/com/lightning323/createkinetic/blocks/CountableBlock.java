package com.lightning323.createkinetic.blocks;

import com.lightning323.createkinetic.ship.KineticShipControl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CountableBlock extends Block {
    public CountableBlock(Properties p_49795_) {
        super(p_49795_);
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClientSide) {
            KineticShipControl controller = KineticShipControl.getOrAddController((ServerLevel) world, pos);
            if (controller != null) addToShip(state, world, pos, controller);
        }
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClientSide) {
            if (newState.isAir() || !newState.is(state.getBlock())) {
                KineticShipControl controller = KineticShipControl.getController((ServerLevel) world, pos);
                if (controller != null) removeFromShip(state, world, pos, controller);
            }
        }
    }

    public abstract void addToShip(BlockState state, Level level, BlockPos pos, KineticShipControl controller);

    public abstract void removeFromShip(BlockState state, Level level, BlockPos pos, KineticShipControl controller);


}
