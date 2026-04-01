package com.lightning323.createkinetic.blocks.sail;

import com.lightning323.createkinetic.blocks.sail.sailPulley.SailBlockBase;
import com.lightning323.createkinetic.blocks.sail.sailPulley.SailPulleyBlock;
import com.lightning323.createkinetic.registries.KineticBlockEntities;
import com.lightning323.createkinetic.registries.KineticBlocks;
import com.lightning323.createkinetic.registries.KineticShapes;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RetractableSailBlock extends HorizontalAxisKineticBlock implements IBE<RetractableSailBlockEntity> {

    public RetractableSailBlock(Properties properties) {
        super(properties);
        this.defaultBlockState().setValue(SailBlockBase.COLOR, DyeColor.WHITE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SailBlockBase.COLOR);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, worldIn, pos, newState, isMoving);
        if (state.is(newState.getBlock()))
            return;
        if (worldIn.isClientSide)
            return;

        BlockState below = worldIn.getBlockState(pos.below());
        if (below.getBlock() instanceof SailBlockBase)
            worldIn.destroyBlock(pos.below(), true);
    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos,
                                 Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!player.mayBuild())
            return InteractionResult.PASS;
        if (player.isShiftKeyDown())
            return InteractionResult.PASS;
        if (player.getItemInHand(handIn)
                .isEmpty()) {
            withBlockEntityDo(worldIn, pos, be -> be.assembleNextTick = true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public Class<RetractableSailBlockEntity> getBlockEntityClass() {
        return RetractableSailBlockEntity.class;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos,
                                       Player player) {
        return KineticBlocks.RETRACTABLE_SAIL.asStack();
    }

    @Override
    public BlockEntityType<? extends RetractableSailBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.SAIL.get();
    }

    public static class WeightBlock extends SailBlockBase {

        public WeightBlock(Properties properties) {
            super(properties);
        }

        @Override
        public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            return KineticShapes.SAIL_MAGNET.get(axis);
        }
    }


}