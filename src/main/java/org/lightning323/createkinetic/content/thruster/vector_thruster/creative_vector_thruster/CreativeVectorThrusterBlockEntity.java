package org.lightning323.createkinetic.content.thruster.vector_thruster.creative_vector_thruster;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.lightning323.createkinetic.config.KineticConfig;
import org.lightning323.createkinetic.content.thruster.SimulatedThrustAdapter;
import org.lightning323.createkinetic.content.thruster.thruster.creative_thruster.CreativeThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.thruster.creative_thruster.CreativeThrusterPowerScrollValueBehaviour;
import org.lightning323.createkinetic.content.thruster.vector_thruster.AbstractVectorThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorThruster_I;
import org.lightning323.createkinetic.registries.KineticBlockEntities;

import java.util.List;

public class CreativeVectorThrusterBlockEntity extends AbstractVectorThrusterBlockEntity implements VectorThruster_I {
    private CreativeThrusterPowerScrollValueBehaviour powerBehaviour;
    private CreativeThrusterBlockEntity.PlumeType plumeType = CreativeThrusterBlockEntity.PlumeType.PLASMA;
    private float peripheralThrustOutput = -1.0f;

    public CreativeVectorThrusterBlockEntity(BlockPos pos, BlockState state) {
        super(KineticBlockEntities.CREATIVE_VECTOR_THRUSTER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        ValueBoxTransform slot = new CreativeVectorThrusterValueBox(true);
        powerBehaviour = new CreativeThrusterPowerScrollValueBehaviour(this, slot, () -> KineticConfig.CREATIVE_VECTOR_THRUSTER_MAX_THRUST.get());
        double base = KineticConfig.CREATIVE_VECTOR_THRUSTER_BASE_THRUST.get();
        double max = KineticConfig.CREATIVE_VECTOR_THRUSTER_MAX_THRUST.get();
        int startStep = (int) Math.round((base / max) * (CreativeThrusterPowerScrollValueBehaviour.TOTAL_STEPS - 1));
        powerBehaviour.value = Math.max(0, Math.min(CreativeThrusterPowerScrollValueBehaviour.TOTAL_STEPS - 1, startStep));
        powerBehaviour.withCallback(i -> {
            updateThrust(getBlockState());
            sendData();
        });
        behaviours.add(powerBehaviour);
    }

    @Override
    public void updateThrust(BlockState currentBlockState) {
        float thrust = 0;
        float currentPower = getPower();
        if (currentPower > 0) {
            float baseThrustPn = peripheralThrustOutput >= 0.0f ? peripheralThrustOutput : (float) (powerBehaviour.getTargetThrust() * getThrustUnitsPerKn());
            baseThrustPn *= (float) calculateAtmosphericFactor();
            thrust = currentPower * baseThrustPn;
        }
        setThrustAndSync(thrust);
        isThrustDirty = false;
    }

    @Override
    protected boolean isWorking() {
        return true;
    }

    @Override
    public boolean isCreative() {
        return true;
    }

    @Override
    public CreativeThrusterBlockEntity.PlumeType getPlumeType() {
        return plumeType;
    }

    @Override
    public boolean shouldEmitPlume() {
        if (plumeType == CreativeThrusterBlockEntity.PlumeType.NONE)
            return false;
        if (!isPowered())
            return false;
        return hasPlumeSpace();
    }

    private boolean hasPlumeSpace() {
        if (level == null)
            return false;

        Direction facing = getBlockState().getValue(CreativeVectorThrusterBlock.FACING);
        BlockPos plumeOccupiedPosition = worldPosition.relative(facing.getOpposite());
        return !SimulatedThrustAdapter.getBlockStateSafe(level, plumeOccupiedPosition).isFaceSturdy(level, plumeOccupiedPosition, facing);
    }

    public void cyclePlumeType() {
        int ordinal = plumeType.ordinal() + 1;
        if (ordinal >= CreativeThrusterBlockEntity.PlumeType.values().length) {
            ordinal = 0;
        }
        plumeType = CreativeThrusterBlockEntity.PlumeType.values()[ordinal];
        setChanged();
        sendData();
    }

    @Override
    protected LangBuilder getGoggleStatus() {
        if (isPowered()) {
            return CreateLang.builder()
                    .add(Component.translatable("createkinetic.gui.goggles.thruster.status.working"))
                    .style(ChatFormatting.GREEN);
        }
        return CreateLang.builder()
                .add(Component.translatable("createkinetic.gui.goggles.thruster.status.not_powered"))
                .style(ChatFormatting.GOLD);
    }

    @Override
    protected double getBaseThrust() {
        if (peripheralThrustOutput >= 0.0f) {
            return (float) (peripheralThrustOutput / getThrustUnitsPerKn());
        }
        return powerBehaviour.getTargetThrust();
    }

    @Override
    protected double getRawThrustCap() {
        if (peripheralThrustOutput >= 0.0f) {
            return (float) (peripheralThrustOutput / getThrustUnitsPerKn());
        }
        return powerBehaviour.getTargetThrust();
    }

    /**
     * Overrides base thrust from the scroll when {@code >= 0} (pN). Pass a negative value or use
     * {@link #clearPeripheralThrustOutput()} to use scroll thrust again. Values are clamped to
     * {@link KineticConfig#CREATIVE_VECTOR_THRUSTER_MAX_THRUST} (kN) converted to pN.
     */
    public void setThrustOutput(float thrustOutputPn) {
        if (thrustOutputPn < 0.0f || Float.isNaN(thrustOutputPn)) {
            this.peripheralThrustOutput = -1.0f;
        } else {
            float maxPn = (float) (KineticConfig.CREATIVE_VECTOR_THRUSTER_MAX_THRUST.get() * getThrustUnitsPerKn());
            this.peripheralThrustOutput = Math.min(Math.max(0.0f, thrustOutputPn), maxPn);
        }
        updateThrust(getBlockState());
        setChanged();
        notifyUpdate();
    }

    public boolean hasPeripheralThrustOverride() {
        return peripheralThrustOutput >= 0.0f;
    }

    public void clearPeripheralThrustOutput() {
        setThrustOutput(-1.0f);
    }

    @Override
    protected void write(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("plumeType", plumeType.ordinal());
        compound.putFloat("PeripheralThrustOutput", peripheralThrustOutput);
    }

    @Override
    protected void read(CompoundTag compound, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (compound.contains("plumeType")) {
            int idx = compound.getInt("plumeType");
            plumeType = CreativeThrusterBlockEntity.PlumeType.values()[Mth.clamp(idx, 0, CreativeThrusterBlockEntity.PlumeType.values().length - 1)];
        }
        if (compound.contains("PeripheralThrustOutput")) {
            peripheralThrustOutput = Math.max(-1.0f, compound.getFloat("PeripheralThrustOutput"));
            if (peripheralThrustOutput >= 0.0f) {
                float maxPn = (float) (KineticConfig.CREATIVE_VECTOR_THRUSTER_MAX_THRUST.get() * getThrustUnitsPerKn());
                peripheralThrustOutput = Math.min(peripheralThrustOutput, maxPn);
            }
        }
    }
}
