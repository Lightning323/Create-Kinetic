package com.lightning323.createkinetic.blocks;

import com.lightning323.createkinetic.ship.KineticShipControl;
import com.lightning323.createkinetic.ship.ShipUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CountableBlock extends Block {
    public CountableBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClientSide) {
            return;
        }
        addToShip(ShipUtils.getOrAddShipController((ServerLevel) world, pos));
    }

    abstract void addToShip(KineticShipControl controller);

    abstract void removeFromShip(KineticShipControl controller);


    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.isClientSide) {
            return;
        }
        if (newState.isAir() || !newState.is(state.getBlock())) {
            removeFromShip(ShipUtils.getOrAddShipController((ServerLevel) world, pos));
        }
    }
}
