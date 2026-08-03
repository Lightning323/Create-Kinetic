package org.lightning323.createkinetic.content.thruster.vector_thruster.ion_vector_thruster;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.lightning323.createkinetic.content.thruster.AbstractThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.ThrusterShapes;
import org.lightning323.createkinetic.content.thruster.ion_thruster.IonThrusterBlock;
import org.lightning323.createkinetic.registries.KineticBlockEntities;

public class IonVectorThrusterBlock extends IonThrusterBlock {
    public static final MapCodec<IonVectorThrusterBlock> CODEC = simpleCodec(IonVectorThrusterBlock::new);

    public IonVectorThrusterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if (facing == Direction.UP) {
            return ThrusterShapes.VECTOR_THRUSTER.get(Direction.DOWN);
        } else if (facing == Direction.DOWN) {
            return ThrusterShapes.VECTOR_THRUSTER.get(Direction.UP);
        }
        return ThrusterShapes.VECTOR_THRUSTER.get(facing);
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<AbstractThrusterBlockEntity> getBlockEntityClass() {
        return (Class<AbstractThrusterBlockEntity>) (Object) IonVectorThrusterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AbstractThrusterBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.ION_THRUSTER_BLOCK_ENTITY.get();
    }
}
