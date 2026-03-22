package com.lightning323.createkinetic.blocks.ballastTank;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BallastTankBlock extends FluidTankBlock {
    protected BallastTankBlock(Properties p_i48440_1_, boolean creative) {
        super(p_i48440_1_, creative);
    }

    //Override creates factory method for a regular (non-creative) tank
    public static BallastTankBlock kRegular(Properties p_i48440_1_) {
        return new BallastTankBlock(p_i48440_1_, false);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult ray) {
        InteractionResult use = super.use(state, world, pos, player, hand, ray);

        FluidTankBlockEntity be = ConnectivityHandler.partAt(getBlockEntityType(), world, pos);
        if (be == null)
            return InteractionResult.FAIL;
        LazyOptional<IFluidHandler> tankCapability = be.getCapability(ForgeCapabilities.FLUID_HANDLER);
        if (!tankCapability.isPresent())
            return InteractionResult.PASS;
        IFluidHandler fluidTank = tankCapability.orElse(null);
        FluidStack fluidInTank = tankCapability.map(fh -> fh.getFluidInTank(0))
                .orElse(FluidStack.EMPTY);


        System.out.println("FLUID AMOUNT: "+fluidInTank.getAmount());


        return use;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), FluidTankBlockEntity::toggleWindows);
        return InteractionResult.SUCCESS;
    }
}
