package com.lightning323.createkinetic.blocks;

import com.lightning323.createkinetic.ship.KineticShipControl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BuoyBlock extends CountableBlock {
    public BuoyBlock(Properties settings) {
        super(settings);
    }
//
    @Override
    public void addToShip(BlockState state, Level level, BlockPos pos, KineticShipControl controller) {
        controller.numBuoys++;
    }

    @Override
    public void removeFromShip(BlockState state, Level level, BlockPos pos, KineticShipControl controller) {
        controller.numBuoys--;
    }

    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

}
