package org.lightning323.createkinetic.content.thruster.vector_thruster.liquid_vector_thruster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.createmod.catnip.lang.LangBuilder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.lightning323.createkinetic.config.KineticConfig;
import org.lightning323.createkinetic.content.thruster.AbstractThrusterBlock;
import org.lightning323.createkinetic.content.thruster.thruster.ThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.AbstractVectorThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorRedstoneLinkBehaviour;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorThruster_I;
import org.lightning323.createkinetic.content.thruster.vector_thruster.ion_vector_thruster.IonVectorThrusterBlockEntity;
import org.lightning323.createkinetic.registries.KineticBlockEntities;

import java.util.List;

public class LiquidVectorThrusterBlockEntity extends AbstractVectorThrusterBlockEntity implements VectorThruster_I {

    public SmartFluidTankBehaviour tank;

    public LiquidVectorThrusterBlockEntity(BlockPos pos, BlockState state) {
        super(KineticBlockEntities.LIQUID_VECTOR_THRUSTER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }

    @Override
    public void tick() {
        super.tick();
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
