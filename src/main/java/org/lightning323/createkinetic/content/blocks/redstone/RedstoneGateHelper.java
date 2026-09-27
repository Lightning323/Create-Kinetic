package org.lightning323.createkinetic.content.blocks.redstone;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.lightning323.createkinetic.registries.KineticBlocks;

final class RedstoneGateHelper {
    private RedstoneGateHelper() {
    }

    static boolean attenuates(BlockState state) {
        return state.is(KineticBlocks.DIODE_BLOCK.get()) || state.is(KineticBlocks.CROSSROAD_BLOCK.get());
    }

    static int wirePower(BlockState state) {
        return state.is(Blocks.REDSTONE_WIRE) ? state.getValue(BlockStateProperties.POWER) : 0;
    }
}
