package org.lightning323.createkinetic.content.thruster.vector_thruster.liquid_vector_thruster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.joml.Vector3d;
import org.lightning323.createkinetic.config.KineticConfig;
import org.lightning323.createkinetic.content.thruster.AbstractThrusterBlock;
import org.lightning323.createkinetic.content.thruster.FluidThrusterProperties;
import org.lightning323.createkinetic.content.thruster.SimulatedThrustAdapter;
import org.lightning323.createkinetic.content.thruster.ThrusterFuelManager;
import org.lightning323.createkinetic.content.thruster.thruster.ThrusterBlock;
import org.lightning323.createkinetic.content.thruster.thruster.ThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.AbstractVectorThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorRedstoneLinkBehaviour;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorThruster_I;
import org.lightning323.createkinetic.content.thruster.vector_thruster.ion_vector_thruster.IonVectorThrusterBlockEntity;
import org.lightning323.createkinetic.registries.KineticBlockEntities;
import org.lightning323.createkinetic.registries.KineticFluids;
import org.lightning323.createkinetic.utility.MultiFluidHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class LiquidVectorThrusterBlockEntity extends AbstractVectorThrusterBlockEntity implements VectorThruster_I {

    public SmartFluidTankBehaviour tank;
    protected boolean updateConnectivity = true;
    protected double lastConsumedMbPerTick = 0.0d;
    protected double lastOxidizerConsumedMbPerTick = 0.0d;
    protected double fuelDrainAccumulator = 0.0d;

    public LiquidVectorThrusterBlockEntity(BlockPos pos, BlockState state) {
        super(KineticBlockEntities.LIQUID_VECTOR_THRUSTER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        tank = SmartFluidTankBehaviour.single(this, getBaseTankCapacityMb());
        behaviours.add(tank);
        tank.getPrimaryHandler().setValidator(stack -> ThrusterFuelManager.getProperties(stack.getFluid()) != null);
    }

    protected int getBaseTankCapacityMb() {
        return KineticConfig.FUEL_TANK_CAPACITY_MB.get();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void updateThrust(BlockState currentBlockState) {
        isThrustDirty = false;
        updateSingleThrust(currentBlockState);
    }

    public FluidThrusterProperties getFuelProperties(Fluid fluid) {
        return ThrusterFuelManager.getProperties(fluid);
    }

    protected void updateSingleThrust(BlockState currentBlockState) {
        System.out.println("LiquidVectorThrusterBlockEntity.updateSingleThrust");
        final double prevConsumedMbPerTick = lastConsumedMbPerTick;
        final int prevFuelAmount = tank != null ? tank.getPrimaryHandler().getFluidAmount() : 0;
        float thrust = 0;
        float currentPower = getPower();
        lastConsumedMbPerTick = 0.0d;
        if (isWorking() && currentPower > 0) {
            System.out.println("LiquidVectorThrusterBlockEntity.updateSingleThrust: currentPower = " + currentPower);
            FluidThrusterProperties properties = getFuelProperties(fluidStack().getFluid());
            float obstructionEffect = calculateObstructionEffect();
            float thrustPercentage = Math.min(currentPower, obstructionEffect);

            if (thrustPercentage > 0 && properties != null) {
                final int tickRate = 10;
                double requestedConsumption = calculateFuelConsumption(currentPower, properties.consumptionMultiplier(), tickRate);
                int consumption = consumeFuelWithAccumulator(requestedConsumption);
                FluidStack drainedStack = tank.getPrimaryHandler().drain(consumption, IFluidHandler.FluidAction.EXECUTE);
                int fuelConsumed = drainedStack.getAmount();

                if (fuelConsumed > 0 || (consumption == 0 && !fluidStack().isEmpty())) {
                    // Keep thrust continuous for sub-1 mB windows: accumulator-based drain can
                    // legitimately round to 0 for this update while fuel is still available.
                    float consumptionRatio = consumption > 0
                            ? (float) fuelConsumed / (float) consumption
                            : 1.0f;
                    float fuelEfficiency = ThrusterFuelManager.getEfficiency(fluidStack().getFluid());
                    float baseThrustPn = (float) (getBaseThrust() * getThrustUnitsPerKn());
                    baseThrustPn *= (float) calculateAtmosphericFactor();
                    thrust = baseThrustPn * thrustPercentage * properties.thrustMultiplier() * fuelEfficiency * consumptionRatio;
                    lastConsumedMbPerTick = (double) fuelConsumed / (double) tickRate;
                }
            }
        }
        setThrustAndSync(thrust);
        if (didSingleTooltipTelemetryChange(prevConsumedMbPerTick, prevFuelAmount)) {
            setChanged();
            notifyUpdate();
        }
        isThrustDirty = false;
    }

    private int consumeFuelWithAccumulator(double requestedAmount) {
        if (requestedAmount <= 0.0d) {
            return 0;
        }
        double total = fuelDrainAccumulator + requestedAmount;
        int toConsume = (int) Math.floor(total);
        fuelDrainAccumulator = total - toConsume;
        return Math.max(0, toConsume);
    }

    private double calculateFuelConsumption(float powerPercentage, float fluidPropertiesConsumptionMultiplier, int tickRate) {
        return getFuelConsumptionPerTickAtFullThrottle() * powerPercentage * fluidPropertiesConsumptionMultiplier * tickRate;
    }

    protected double getFuelConsumptionPerTickAtFullThrottle() {
        return KineticConfig.FUEL_MB_PER_TICK_AT_FULL_THROTTLE.get();
    }

    private boolean didSingleTooltipTelemetryChange(double prevConsumedMbPerTick, int prevFuelAmount) {
        if (Math.abs(lastConsumedMbPerTick - prevConsumedMbPerTick) > 1e-4d) {
            return true;
        }
        int fuelAmountNow = tank != null ? tank.getPrimaryHandler().getFluidAmount() : 0;
        return fuelAmountNow != prevFuelAmount;
    }

    @Override
    protected boolean isWorking() {
        return validFluid();
    }

    @Override
    protected LangBuilder getGoggleStatus() {
        if (fluidStack().isEmpty()) {
            return CreateLang.builder().add(Component.translatable("createkinetic.gui.goggles.thruster.status.no_fuel")).style(ChatFormatting.RED);
        } else if (!validFluid()) {
            return CreateLang.builder().add(Component.translatable("createkinetic.gui.goggles.thruster.status.wrong_fuel")).style(ChatFormatting.RED);
        } else if (!isPowered()) {
            return CreateLang.builder().add(Component.translatable("createkinetic.gui.goggles.thruster.status.not_powered")).style(ChatFormatting.GOLD);
        } else if (getEmptyBlocks() == 0) {
            return CreateLang.builder().add(Component.translatable("createkinetic.gui.goggles.thruster.obstructed")).style(ChatFormatting.RED);
        } else {
            return CreateLang.builder().add(Component.translatable("createkinetic.gui.goggles.thruster.status.working")).style(ChatFormatting.GREEN);
        }
    }

    private void addFluidContainerTooltip(List<Component> tooltip, Component label,
                                          IFluidHandler handler, double consumptionRate) {
        if (handler == null || handler.getTanks() <= 0) return;
        int capacity = handler.getTankCapacity(0);
        FluidStack fluid = handler.getFluidInTank(0);
        int amount = fluid.getAmount();

        // Label line: "Fuel:"
        CreateLang.builder()
                .add(label.copy())
                .style(ChatFormatting.WHITE)
                .forGoggles(tooltip);

        // Storage line: "  100 / 1000 mB"
        CreateLang.builder()
                .add(Component.literal("  "))
                .add(Component.literal(Integer.toString(amount)).withStyle(ChatFormatting.AQUA))
                .add(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                .add(Component.literal(Integer.toString(capacity)).withStyle(ChatFormatting.AQUA))
                .add(Component.literal(" mB").withStyle(ChatFormatting.GRAY))
                .forGoggles(tooltip);

        // Consumption line: "  1.5 mB/t"
        CreateLang.builder()
                .add(Component.literal("  "))
                .add(Component.literal(String.format(Locale.ROOT, "%.1f", consumptionRate)).withStyle(ChatFormatting.AQUA))
                .add(Component.literal(" mB/t").withStyle(ChatFormatting.GRAY))
                .forGoggles(tooltip);
    }

    @Override
    protected void addThrusterDetails(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addThrusterDetails(tooltip, isPlayerSneaking);
        LiquidVectorThrusterBlockEntity ctrl = getControllerBE();
        if (ctrl == null) return;


        if (ctrl.tank == null) return;

        // --- Fuel tank (always shown) ---
        addFluidContainerTooltip(tooltip,
                Component.translatable("createkinetic.gui.goggles.thruster.fuel_label"),
                ctrl.tank.getPrimaryHandler(), ctrl.lastConsumedMbPerTick);
    }

    @Nullable
    public LiquidVectorThrusterBlockEntity getControllerBE() {
        return this;
    }

    public Direction getFluidCapSide() {
        return getBlockState().getValue(ThrusterBlock.FACING);
    }

    private boolean isFrontLayerCell(LiquidVectorThrusterBlockEntity ctrl, Direction cubeFacing) {
        BlockPos origin = ctrl.worldPosition;
        int size = ctrl.width;
        int rel;
        switch (cubeFacing.getAxis()) {
            case X -> rel = worldPosition.getX() - origin.getX();
            case Y -> rel = worldPosition.getY() - origin.getY();
            case Z -> rel = worldPosition.getZ() - origin.getZ();
            default -> {
                return false;
            }
        }
        int frontIdx = cubeFacing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? size - 1 : 0;
        return rel == frontIdx;
    }

    public IFluidHandler getFluidHandler(Direction side) {
        LiquidVectorThrusterBlockEntity ctrl = getControllerBE();
        if (ctrl == null) return null;

        IFluidHandler fuel = ctrl.tank.getPrimaryHandler();

        if (!ctrl.isMultiblock()) {
            if (side == null || side == getFluidCapSide()) {
                return fuel;
            }
            return null;
        }

        if (side == null) {
            return fuel;
        }

        if (!isFrontLayerCell(ctrl, ctrl.getBlockState().getValue(AbstractThrusterBlock.FACING))) return null;

        if (side == ctrl.getBlockState().getValue(AbstractThrusterBlock.FACING).getOpposite()) {
            return null;
        }

        return fuel;
    }


    @Override
    protected void write(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("Width", width);
        compound.putDouble("LastConsumedMbPerTick", lastConsumedMbPerTick);
        compound.putDouble("LastOxidizerConsumedMbPerTick", lastOxidizerConsumedMbPerTick);
        compound.putDouble("FuelDrainAccumulator", fuelDrainAccumulator);

        if (tank != null) {
            compound.put("FuelTankSync", tank.getPrimaryHandler().getFluid().saveOptional(registries));
        }

        if (controllerPos != null) {
            compound.putInt("ControllerOffX", controllerPos.getX() - worldPosition.getX());
            compound.putInt("ControllerOffY", controllerPos.getY() - worldPosition.getY());
            compound.putInt("ControllerOffZ", controllerPos.getZ() - worldPosition.getZ());
        }
        if (updateConnectivity) {
            compound.putBoolean("UpdateConnectivity", true);
        }
    }

    @Override
    protected void read(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        width = Math.max(1, compound.getInt("Width"));
        lastConsumedMbPerTick = compound.getDouble("LastConsumedMbPerTick");
        lastOxidizerConsumedMbPerTick = compound.getDouble("LastOxidizerConsumedMbPerTick");
        fuelDrainAccumulator = compound.getDouble("FuelDrainAccumulator");


        // Update capacity before loading fluid to avoid truncation
        if (isController() && isMultiblock()) {
            int cap = getBaseTankCapacityMb() * width * width * width;
            if (tank != null) tank.getPrimaryHandler().setCapacity(cap);

        }

        if (tank != null && compound.contains("FuelTankSync")) {
            tank.getPrimaryHandler().setFluid(FluidStack.parseOptional(registries, compound.getCompound("FuelTankSync")));
        }

        if (compound.contains("ControllerOffX")) {
            controllerPos = worldPosition.offset(
                    compound.getInt("ControllerOffX"),
                    compound.getInt("ControllerOffY"),
                    compound.getInt("ControllerOffZ"));
        } else {
            controllerPos = null;
        }
        updateConnectivity = compound.getBoolean("UpdateConnectivity");
    }

}
