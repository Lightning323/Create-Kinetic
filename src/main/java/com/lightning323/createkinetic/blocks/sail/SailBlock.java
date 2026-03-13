package com.lightning323.createkinetic.blocks.sail;

import com.lightning323.createkinetic.blocks.sail.sailPulley.SailPulleyBlock;
import com.lightning323.createkinetic.registries.KineticBlockEntities;
import com.lightning323.createkinetic.registries.KineticItems;
import com.lightning323.createkinetic.registries.KineticShapes;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SailBlock extends HorizontalAxisKineticBlock implements IBE<SailBlockEntity> {


    public SailBlock(Properties properties) {
        super(properties);
    }

    private static void onRopeBroken(Level world, BlockPos sailPos) {
        BlockEntity be = world.getBlockEntity(sailPos);
        if (be instanceof SailBlockEntity sail) {
            sail.initialOffset = 0;
            sail.onLengthBroken();
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, worldIn, pos, newState, isMoving);
        if (state.is(newState.getBlock()))
            return;
        if (worldIn.isClientSide)
            return;

        BlockState below = worldIn.getBlockState(pos.below());
        if (below.getBlock() instanceof SailPulleyBlock.SailBlockBase)
            worldIn.destroyBlock(pos.below(), true);
    }

    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit) {
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
    public Class<SailBlockEntity> getBlockEntityClass() {
        return SailBlockEntity.class;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos,
                                       Player player) {
        return KineticItems.RETRACTABLE_SAIL.asStack();
    }

    @Override
    public BlockEntityType<? extends SailBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.SAIL.get();
    }

    public static class WeightBlock extends SailPulleyBlock.SailBlockBase {

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