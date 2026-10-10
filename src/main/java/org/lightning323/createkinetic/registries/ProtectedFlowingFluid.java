package org.lightning323.createkinetic.registries;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class ProtectedFlowingFluid {

    public static class Source extends BaseFlowingFluid.Source {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        protected boolean canSpreadTo(BlockGetter level, BlockPos fromPos, BlockState fromState, Direction direction,
                                      BlockPos toPos, BlockState toState, FluidState toFluidState, Fluid fluid) {
            if (toFluidState.is(FluidTags.WATER)) {
                return false;
            }
            return super.canSpreadTo(level, fromPos, fromState, direction, toPos, toState, toFluidState, fluid);
        }
    }

    public static class Flowing extends BaseFlowingFluid.Flowing {
        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        protected boolean canSpreadTo(BlockGetter level, BlockPos fromPos, BlockState fromState, Direction direction,
                                      BlockPos toPos, BlockState toState, FluidState toFluidState, Fluid fluid) {
            if (toFluidState.is(FluidTags.WATER)) {
                return false;
            }
            return super.canSpreadTo(level, fromPos, fromState, direction, toPos, toState, toFluidState, fluid);
        }
    }
}
