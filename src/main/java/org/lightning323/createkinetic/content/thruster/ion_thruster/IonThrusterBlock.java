package org.lightning323.createkinetic.content.thruster.ion_thruster;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.lightning323.createkinetic.content.thruster.AbstractThrusterBlock;
import org.lightning323.createkinetic.content.thruster.AbstractThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.ThrusterShapes;
import org.lightning323.createkinetic.content.thruster.thruster.ThrusterBlock;
import org.lightning323.createkinetic.registries.PropulsionBlockEntities;

import javax.annotation.Nonnull;

public class IonThrusterBlock extends AbstractThrusterBlock {
    public static final MapCodec<IonThrusterBlock> CODEC = simpleCodec(IonThrusterBlock::new);

    public IonThrusterBlock(final Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(ThrusterBlock.MULTIBLOCK, false));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ThrusterBlock.MULTIBLOCK);
    }

    @Override
    protected VoxelShape getShape(final BlockState state,
                                  final BlockGetter level,
                                  final BlockPos pos,
                                  final CollisionContext context) {
        if (state.hasProperty(ThrusterBlock.MULTIBLOCK) && state.getValue(ThrusterBlock.MULTIBLOCK)) {
            return Shapes.block();
        }
        final Direction direction = state.getValue(FACING);
        return ThrusterShapes.ION_THRUSTER.get(direction);
    }

    @Override
    public Class<AbstractThrusterBlockEntity> getBlockEntityClass() {
        return AbstractThrusterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AbstractThrusterBlockEntity> getBlockEntityType() {
        return PropulsionBlockEntities.ION_THRUSTER_BLOCK_ENTITY.get();
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        if (type == PropulsionBlockEntities.ION_THRUSTER_BLOCK_ENTITY.get()) {
            return new SmartBlockEntityTicker<>();
        }
        return null;
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }
}

