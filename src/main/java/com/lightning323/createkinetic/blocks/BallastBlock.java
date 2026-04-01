package com.lightning323.createkinetic.blocks;

import com.lightning323.createkinetic.ship.KineticShipControl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BallastBlock extends CountableBlock {

    public BallastBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void addToShip(BlockState state, Level level, BlockPos pos, KineticShipControl controller) {
        controller.numBallast++;
    }

    @Override
    public void removeFromShip(BlockState state, Level level, BlockPos pos, KineticShipControl controller) {
        controller.numBallast--;
    }

}
