package org.lightning323.createkinetic.blocks.thruster;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import org.lightning323.createkinetic.registries.KineticBlockEntitiyTypes;

public class ThrusterBlock extends Block implements IBE<ThrusterBlockEntity> {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;


    public ThrusterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction normal = context.getNearestLookingDirection();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            normal = normal.getOpposite();
        }
        return this.defaultBlockState()
                .setValue(FACING, normal);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? (lvl, pos, st, be) -> {
            if (be instanceof ThrusterBlockEntity thruster) {
                ThrusterBlockEntity.tick(lvl, pos, st, thruster);
            }
        } : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }


    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide) {
            boolean shouldBePowered = level.hasNeighborSignal(pos);
            if (state.getValue(POWERED) != shouldBePowered) {
                level.setBlock(pos, state.setValue(POWERED, shouldBePowered), Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    public BlockEntityType<? extends ThrusterBlockEntity> getBlockEntityType() {
        return KineticBlockEntitiyTypes.THRUSTER.get();
    }

    @Override
    public Class<ThrusterBlockEntity> getBlockEntityClass() {
        return ThrusterBlockEntity.class;
    }

}