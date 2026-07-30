package org.lightning323.createkinetic.content.platinum;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.lightning323.createkinetic.registries.KineticBlockEntities;

public class PlatinumFluidTankBlockEntity extends FluidTankBlockEntity {
    public PlatinumFluidTankBlockEntity(BlockPos pos, BlockState state) {
        super(KineticBlockEntities.PLATINUM_FLUID_TANK_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(getCapacityMultiplier() * 2, this::onFluidStackChanged);
    }

    @Override
    public void applyFluidTankSize(int blocks) {
        tankInventory.setCapacity(blocks * getCapacityMultiplier() * 2);
        int overflow = tankInventory.getFluidAmount() - tankInventory.getCapacity();
        if (overflow > 0) {
            tankInventory.drain(overflow, FluidAction.EXECUTE);
        }
        forceFluidLevelUpdate = true;
    }

    @Override
    public int getTankSize(int tank) {
        return getCapacityMultiplier() * 2;
    }

    public IFluidHandler getCapabilityHandler() {
        return fluidCapability;
    }

    @Override
    public float getFillState() {
        int capacity = Math.max(1, getTotalTankSize() * getCapacityMultiplier() * 2);
        return (float) tankInventory.getFluidAmount() / capacity;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (!isController()) {
            return;
        }
        tankInventory.setCapacity(getTotalTankSize() * getCapacityMultiplier() * 2);
        if (compound.contains("TankContent")) {
            tankInventory.readFromNBT(registries, compound.getCompound("TankContent"));
            if (tankInventory.getSpace() < 0) {
                tankInventory.drain(-tankInventory.getSpace(), FluidAction.EXECUTE);
            }
        }
    }
}
