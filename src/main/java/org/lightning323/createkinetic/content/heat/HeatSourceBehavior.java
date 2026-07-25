package org.lightning323.createkinetic.content.heat;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.nbt.CompoundTag;

public class HeatSourceBehavior extends BlockEntityBehaviour {
    //Have some tea
    public static final BehaviourType<HeatSourceBehavior> TYPE = new BehaviourType<>();
    private HeatBuffer heatBuffer; 

    public HeatSourceBehavior(SmartBlockEntity be, float capacity, float expectedHeatProduction) {
        super(be);
        this.heatBuffer = new HeatBuffer(0, capacity, expectedHeatProduction);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public IHeatSource getHeatSource() {
        return this.heatBuffer;
    }

    @Override
    public void write(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
        nbt.put("HeatBuffer", heatBuffer.serializeNBT(registries));
        super.write(nbt, registries, clientPacket);
    }

    @Override
    public void read(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
        heatBuffer.deserializeNBT(registries, nbt.getCompound("HeatBuffer"));
        super.read(nbt, registries, clientPacket);
    }

}

