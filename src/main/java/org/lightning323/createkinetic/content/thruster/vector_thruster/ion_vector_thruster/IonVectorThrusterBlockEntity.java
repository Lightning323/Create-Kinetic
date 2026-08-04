package org.lightning323.createkinetic.content.thruster.vector_thruster.ion_vector_thruster;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.lightning323.createkinetic.config.KineticConfig;
import org.lightning323.createkinetic.content.thruster.vector_thruster.AbstractVectorThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorThruster_I;
import org.lightning323.createkinetic.registries.KineticBlockEntities;

import java.util.List;

public class IonVectorThrusterBlockEntity extends AbstractVectorThrusterBlockEntity implements VectorThruster_I {

    public IonVectorThrusterBlockEntity(BlockPos pos, BlockState state) {
        super(KineticBlockEntities.ION_VECTOR_THRUSTER_BLOCK_ENTITY.get(), pos, state);
    }

    public KineticConfig.ThrusterPlumeType getPlumeRenderType() {
        return KineticConfig.getVectorThrustersPlumeType();
    }


    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

    }

    @Override
    public void updateThrust(BlockState currentBlockState) {

    }

    @Override
    protected boolean isWorking() {
        return false;
    }

    @Override
    protected LangBuilder getGoggleStatus() {
        return null;
    }

}
