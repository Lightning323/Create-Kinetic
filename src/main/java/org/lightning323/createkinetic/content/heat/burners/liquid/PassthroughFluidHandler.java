package org.lightning323.createkinetic.content.heat.burners.liquid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.lightning323.createkinetic.content.thruster.ThrusterFuelManager;

public class PassthroughFluidHandler implements IFluidHandler {
    private final LiquidBurnerBlockEntity blockEntity;
    private final Direction inputSide;

    public PassthroughFluidHandler(LiquidBurnerBlockEntity blockEntity, Direction side) {
        this.blockEntity = blockEntity;
        this.inputSide = side;
    }

    private boolean isFuel(FluidStack stack) {
        if (stack.isEmpty()) return false;
        return ThrusterFuelManager.getProperties(stack.getFluid()) != null;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;

        int totalFilled = 0;
        FluidStack remaining = resource.copy();

        //Try to fill internal tank if it matches a valid fuel
        if (isFuel(resource)) {
            int filledIntoTank = blockEntity.tank.getPrimaryHandler().fill(remaining, action);
            totalFilled += filledIntoTank;
            remaining.shrink(filledIntoTank);
        }

        //Pass to the opposite side
        if (!remaining.isEmpty()) {
            Level level = blockEntity.getLevel();
            if (level != null) {
                Direction outputSide = inputSide.getOpposite();
                BlockPos neighborPos = blockEntity.getBlockPos().relative(outputSide);
                IFluidHandler cap = level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, inputSide);
                if (cap != null) {
                    int filledIntoNeighbor = cap.fill(remaining, action);
                    totalFilled += filledIntoNeighbor;
                }
            }
        }

        return totalFilled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return blockEntity.tank.getPrimaryHandler().drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return blockEntity.tank.getPrimaryHandler().drain(maxDrain, action);
    }

    @Override
    public int getTanks() { 
        return blockEntity.tank.getPrimaryHandler().getTanks(); 
    }

    @Override
    public FluidStack getFluidInTank(int tankIndex) { 
        return blockEntity.tank.getPrimaryHandler().getFluidInTank(tankIndex); 
    }

    @Override
    public int getTankCapacity(int tankIndex) { 
        return blockEntity.tank.getPrimaryHandler().getTankCapacity(tankIndex); 
    }

    @Override
    public boolean isFluidValid(int tankIndex, FluidStack stack) { 
        return blockEntity.tank.getPrimaryHandler().isFluidValid(tankIndex, stack); 
    }
}


