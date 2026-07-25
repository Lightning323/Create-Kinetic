package org.lightning323.createkinetic.utility;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MultiFluidHandler implements IFluidHandler {
    private final IFluidHandler handler1;
    private final IFluidHandler handler2;

    public MultiFluidHandler(IFluidHandler handler1, IFluidHandler handler2) {
        this.handler1 = handler1;
        this.handler2 = handler2;
    }

    @Override
    public int getTanks() {
        return handler1.getTanks() + handler2.getTanks();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        int tanks1 = handler1.getTanks();
        if (tank < tanks1) {
            return handler1.getFluidInTank(tank);
        }
        return handler2.getFluidInTank(tank - tanks1);
    }

    @Override
    public int getTankCapacity(int tank) {
        int tanks1 = handler1.getTanks();
        if (tank < tanks1) {
            return handler1.getTankCapacity(tank);
        }
        return handler2.getTankCapacity(tank - tanks1);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        int tanks1 = handler1.getTanks();
        if (tank < tanks1) {
            return handler1.isFluidValid(tank, stack);
        }
        return handler2.isFluidValid(tank - tanks1, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filled = handler1.fill(resource, action);
        if (filled > 0) return filled;
        return handler2.fill(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack drained = handler1.drain(resource, action);
        if (!drained.isEmpty()) return drained;
        return handler2.drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack drained = handler1.drain(maxDrain, action);
        if (!drained.isEmpty()) return drained;
        return handler2.drain(maxDrain, action);
    }
}
