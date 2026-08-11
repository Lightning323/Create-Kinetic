package org.lightning323.createkinetic.content.thruster.vector_thruster.ion_vector_thruster;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.lightning323.createkinetic.compat.KineticCompat;
import org.lightning323.createkinetic.compat.computercraft.ComputerBehaviour;
import org.lightning323.createkinetic.config.KineticConfig;
import org.lightning323.createkinetic.content.thruster.SimulatedThrustAdapter;
import org.lightning323.createkinetic.content.thruster.ThrusterDamager;
import org.lightning323.createkinetic.content.thruster.vector_thruster.AbstractVectorThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorThruster_I;
import org.lightning323.createkinetic.registries.KineticBlockEntities;

import javax.annotation.Nullable;
import java.util.List;

public class IonVectorThrusterBlockEntity extends AbstractVectorThrusterBlockEntity implements VectorThruster_I {

    private int energyStored;
    private double energyDrainAccumulator;
    private long lastEnergyDrainGameTime = -1L;
    private double lastConsumedFePerTick;

    public IEnergyStorage getEnergyHandler(final Direction side) {
        // Expose FE on every side for broad cable/interface compatibility.
        // Some networks probe or pull from arbitrary faces instead of the nominal input face.
        return this.energyHandler;
    }

    private final IEnergyStorage energyHandler = new IEnergyStorage() {
        @Override
        public int receiveEnergy(final int maxReceive, final boolean simulate) {
            if (maxReceive <= 0) {
                return 0;
            }
            final boolean wasEmpty = getTotalEnergyStoredFe() <= 0;
            final int accepted = insertEnergy(maxReceive, simulate);
            if (!simulate && accepted > 0) {
                setChanged();
                // Recalculate immediately only when transitioning from unpowered to powered.
                if (wasEmpty) {
                    dirtyThrust();
                }
                notifyUpdate();
            }
            return accepted;
        }

        @Override
        public int extractEnergy(final int maxExtract, final boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return getTotalEnergyStoredFe();
        }

        @Override
        public int getMaxEnergyStored() {
            return getTotalEnergyCapacityFe();
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    };

    private int getTotalEnergyCapacityFe() {
        if (!isController() && isMultiblock()) {
            IonVectorThrusterBlockEntity ctrl = getControllerBE();
            return ctrl instanceof IonVectorThrusterBlockEntity ion ? ion.getTotalEnergyCapacityFe() : this.getEnergyCapacity();
        }
        int members = isMultiblock() ? width * width * width : 1;
        return this.getEnergyCapacity() * members;
    }

    public IonVectorThrusterBlockEntity(BlockPos pos, BlockState state) {
        super(KineticBlockEntities.ION_VECTOR_THRUSTER_BLOCK_ENTITY.get(), pos, state);
    }

    public KineticConfig.ThrusterPlumeType getPlumeRenderType() {
        return KineticConfig.getVectorThrustersPlumeType();
    }

    @Nullable
    public IonVectorThrusterBlockEntity getControllerBE() {
        return this;
    }

    private int getTotalEnergyStoredFe() {
        if (!isController() && isMultiblock()) {
            IonVectorThrusterBlockEntity ctrl = getControllerBE();
            return ctrl instanceof IonVectorThrusterBlockEntity ion ? ion.getTotalEnergyStoredFe() : this.energyStored;
        }
        if (!isMultiblock() || level == null) {
            return this.energyStored;
        }
        int total = 0;
        BlockPos origin = worldPosition;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                for (int z = 0; z < width; z++) {
                    net.minecraft.world.level.block.entity.BlockEntity be = SimulatedThrustAdapter.getBlockEntitySafe(level, origin.offset(x, y, z));
                    if (be instanceof IonVectorThrusterBlockEntity ion) {
                        total += ion.energyStored;
                    }
                }
            }
        }
        return total;
    }

    @Override
    public void tick() {
        // Ion thrusters should evaluate power/consumption every server tick so FE usage is stable and responsive.
        this.isThrustDirty = true;
        super.tick();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        // Keep ion/vector tankless, but still inherit base behaviours like CC + plume damage.
        if (KineticCompat.CC_ACTIVE) {
            behaviours.add(computerBehaviour = new ComputerBehaviour(this));
        }
        behaviours.add(new ThrusterDamager(this));
    }

    @Override
    public void updateThrust(BlockState currentBlockState) {
        float thrust = 0;
        float currentPower = getPower();

        if (currentPower > 0 && energyStored > 0) {
            float obstructionEffect = calculateObstructionEffect();
            float thrustPercentage = Math.min(currentPower, obstructionEffect);

            if (thrustPercentage > 0) {
                long currentGameTime = level != null ? level.getGameTime() : 0L;
                int ticksElapsed = 1;
                if (lastEnergyDrainGameTime >= 0L) {
                    ticksElapsed = (int) Math.max(0L, currentGameTime - lastEnergyDrainGameTime);
                }
                lastEnergyDrainGameTime = currentGameTime;

                // Config value is FE/t at full throttle; scale by throttle and elapsed ticks.
                double requestedDrain = energyDrainAccumulator
                        + (double) ticksElapsed * thrustPercentage * KineticConfig.ION_THRUSTER_FE_PER_TICK_AT_FULL_THROTTLE.get();
                int totalDrain = (int) Math.floor(requestedDrain);
                energyDrainAccumulator = requestedDrain - totalDrain;

                int consumed = Math.min(energyStored, totalDrain);
                if (consumed > 0) {
                    energyStored -= consumed;
                    float consumptionRatio = (float) consumed / (float) totalDrain;
                    float baseThrustPn = (float) (KineticConfig.ION_THRUSTER_BASE_THRUST.get() * getThrustUnitsPerKn());
                    baseThrustPn *= (float) calculateAtmosphericFactor();
                    thrust = baseThrustPn * thrustPercentage * consumptionRatio;
                }
                lastConsumedFePerTick = ticksElapsed > 0 ? (double) consumed / (double) ticksElapsed : 0.0d;
            } else {
                lastConsumedFePerTick = 0.0d;
            }
        } else {
            lastConsumedFePerTick = 0.0d;
        }
        // Mark dirty if energy was depleted to force thrust recalculation
        if (energyStored == 0 && thrust == 0) {
            isThrustDirty = true;
        } else {
            isThrustDirty = false;
        }
        setThrustAndSync(thrust);
        // Sync energy/consumption values used in goggles.
        setChanged();
        notifyUpdate();
    }

    private int insertEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) {
            return 0;
        }
        if (!isController() && isMultiblock()) {
            IonVectorThrusterBlockEntity ctrl = getControllerBE();
            return ctrl instanceof IonVectorThrusterBlockEntity ion ? ion.insertEnergy(maxReceive, simulate) : 0;
        }
        if (!isMultiblock() || level == null) {
            int accepted = Math.min(maxReceive, Math.max(0, getEnergyCapacity() - energyStored));
            if (!simulate && accepted > 0) {
                energyStored += accepted;
            }
            return accepted;
        }



        int remaining = maxReceive;
        BlockPos origin = worldPosition;
        for (int x = 0; x < width && remaining > 0; x++) {
            for (int y = 0; y < width && remaining > 0; y++) {
                for (int z = 0; z < width && remaining > 0; z++) {
                    net.minecraft.world.level.block.entity.BlockEntity be = SimulatedThrustAdapter.getBlockEntitySafe(level, origin.offset(x, y, z));
                    if (!(be instanceof IonVectorThrusterBlockEntity ion)) {
                        continue;
                    }
                    int accepted = Math.min(remaining, Math.max(0, ion.getEnergyCapacity() - ion.energyStored));
                    if (accepted > 0) {
                        if (!simulate) {
                            ion.energyStored += accepted;
                            ion.setChanged();
                            ion.notifyUpdate();
                        }
                        remaining -= accepted;
                    }
                }
            }
        }
        return maxReceive - remaining;
    }

    public int getEnergyCapacity() {
        return KineticConfig.ION_THRUSTER_ENERGY_CAPACITY_FE.get();
    }


    @Override
    protected boolean isWorking() {
        return getThrottle() > 0 && getTotalEnergyStoredFe() > 0;
    }

    @Override
    protected LangBuilder getGoggleStatus() {
        if (this.getThrottle() <= 0.0d) {
            return CreateLang.builder().add(Component.translatable("createkinetic.gui.goggles.thruster.status.not_powered"))
                    .style(ChatFormatting.GOLD);
        }
        if (this.getTotalEnergyStoredFe() <= 0) {
            return CreateLang.builder().add(Component.translatable("createkinetic.gui.goggles.thruster.status.no_energy"))
                    .style(ChatFormatting.RED);
        }
        return CreateLang.builder().add(Component.translatable("createkinetic.gui.goggles.thruster.status.working"))
                .style(ChatFormatting.GREEN);
    }

}
