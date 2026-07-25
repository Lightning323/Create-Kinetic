package org.lightning323.createkinetic.compat.computercraft;

import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lightning323.createkinetic.PropulsionConfig;
import org.lightning323.createkinetic.content.thruster.AbstractThrusterBlockEntity.ControlMode;
import org.lightning323.createkinetic.content.thruster.ion_thruster.IonThrusterBlockEntity;

/**
 * Ion FE thrusters use their own peripheral type so Lua can distinguish them from fuel thrusters ({@link ThrusterPeripheral}).
 */
public class IonThrusterPeripheral extends SyncedPeripheral<IonThrusterBlockEntity> {

    public IonThrusterPeripheral(IonThrusterBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public final String getType() {
        return "ion_thruster";
    }

    @LuaFunction
    public final int getObstruction() {
        return blockEntity.getUnobstructedBlocks();
    }

    @LuaFunction(mainThread = true)
    public final void setPower(int redstonePower) {
        ThrusterComputerHelpers.setThrottleFromRedstone(blockEntity, redstonePower);
    }

    @LuaFunction(mainThread = true)
    public final void setPowerNormalized(double power) {
        ThrusterComputerHelpers.setThrottleNormalized(blockEntity, power);
    }

    @LuaFunction(mainThread = true)
    public final double getPower() {
        return blockEntity.getThrottle();
    }

    @LuaFunction
    public final double getCurrentThrustPN() {
        return blockEntity.getCurrentThrust();
    }

    @LuaFunction
    public final double getCurrentThrustKN() {
        return getCurrentThrustPN() / PropulsionConfig.getThrustUnitsPerKnOrDefault();
    }

    @LuaFunction
    public final double getDisplayedThrustPN() {
        return blockEntity.getDisplayedThrustPnForTooltip();
    }

    @LuaFunction
    public final double getDisplayedThrustKN() {
        return getDisplayedThrustPN() / PropulsionConfig.getThrustUnitsPerKnOrDefault();
    }

    @LuaFunction
    public final double getAirflowMs() {
        return blockEntity.getDisplayedAirflowMsForTooltip();
    }

    @LuaFunction(mainThread = true)
    public final int getEnergyAmountFe() {
        return blockEntity.getEnergyStoredFe();
    }

    @LuaFunction(mainThread = true)
    public final int getEnergyCapacityFe() {
        return blockEntity.getEnergyCapacity();
    }

    @Override
    public boolean equals(IPeripheral other) {
        if (this == other)
            return true;
        if (other instanceof IonThrusterPeripheral ion)
            return this.blockEntity == ion.blockEntity;
        return false;
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        super.attach(computer);
        blockEntity.setDigitalInput(Mth.clamp(blockEntity.getPower(), 0.0f, 1.0f));
        blockEntity.setControlMode(ControlMode.PERIPHERAL);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        super.detach(computer);
        blockEntity.setDigitalInput(0.0f);
        blockEntity.setRedstonePower(0);
        blockEntity.setControlMode(ControlMode.NORMAL);
    }
}
