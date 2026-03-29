package com.lightning323.createkinetic.blocks;

import com.lightning323.createkinetic.ship.KineticShipControl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantedBallastBlock extends CountableBlock {
    public EnchantedBallastBlock(Properties settings) {
        super(settings);
    }

    @Override
    void addToShip(BlockState state, Level level, BlockPos pos, KineticShipControl controller) {
        controller.numEnchantedBallast++;
    }

    @Override
    void removeFromShip(BlockState state, Level level, BlockPos pos, KineticShipControl controller) {
        controller.numEnchantedBallast--;
    }
}
