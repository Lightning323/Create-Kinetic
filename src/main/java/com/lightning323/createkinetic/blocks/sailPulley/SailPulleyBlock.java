package com.lightning323.createkinetic.blocks.sailPulley;

import com.lightning323.createkinetic.Createkinetic;
import com.lightning323.createkinetic.registries.KineticBlockEntities;
import com.lightning323.createkinetic.registries.KineticItems;
import com.lightning323.createkinetic.registries.KineticShapes;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.lightning323.createkinetic.utils.VSUtils;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SailPulleyBlock extends HorizontalAxisKineticBlock implements IBE<SailBlockEntity> {


    public SailPulleyBlock(Properties properties) {
        super(properties);
    }

    private static void onRopeBroken(Level world, BlockPos sailPos) {
        BlockEntity be = world.getBlockEntity(sailPos);
        if (be instanceof SailBlockEntity sail) {
            sail.initialOffset = 0;
            sail.onLengthBroken();
        }
    }

//    @SuppressWarnings("deprecation")
//    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
//        if (world.isClientSide) return;
//
//        SailsShipControl shipController = VSUtils.getShipController(world, pos);
//        if (shipController != null) {
//            Createkinetic.LOGGER.info("PLACED SAIL BLOCK HERE");
//            SailBlockEntity be = (SailBlockEntity) world.getBlockEntity(pos);
//            if (be != null) shipController.sailBlocks.add(be);
//        }
//    }

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
    public BlockEntityType<? extends SailBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.SAIL.get();
    }

    private static class SailBlockBase extends Block implements SimpleWaterloggedBlock {

        public SailBlockBase(Properties properties) {
            super(properties);
            registerDefaultState(super.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
        }

        @Override
        public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
            return false;
        }


        @Override
        public PushReaction getPistonPushReaction(BlockState state) {
            return PushReaction.BLOCK;
        }

        @Override
        public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos,
                                           Player player) {
            return KineticItems.SAIL_PULLEY.asStack();
        }

        @Override
        public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
            KineticShipControl shipController = VSUtils.getOrCreateShipController(worldIn, pos);
            if (shipController != null) {
                shipController.numSquareSails--;
                shipController.countSails();
            }


            if (!isMoving && (!state.hasProperty(BlockStateProperties.WATERLOGGED) || !newState.hasProperty(BlockStateProperties.WATERLOGGED) || state.getValue(BlockStateProperties.WATERLOGGED) == newState.getValue(BlockStateProperties.WATERLOGGED))) {
                onRopeBroken(worldIn, pos.above());
                if (!worldIn.isClientSide) {
                    BlockState above = worldIn.getBlockState(pos.above());
                    BlockState below = worldIn.getBlockState(pos.below());
                    if (above.getBlock() instanceof SailBlockBase)
                        worldIn.destroyBlock(pos.above(), true);
                    if (below.getBlock() instanceof SailBlockBase)
                        worldIn.destroyBlock(pos.below(), true);
                }
            }
            if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
                worldIn.removeBlockEntity(pos);
            }
        }


        @Override
        public FluidState getFluidState(BlockState state) {
            return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            // This allows the block to actually hold the AXIS and WATERLOGGED values
            builder.add(BlockStateProperties.HORIZONTAL_AXIS, BlockStateProperties.WATERLOGGED);
            super.createBlockStateDefinition(builder);
        }

        @Override
        public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState,
                                      LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
            if (state.getValue(BlockStateProperties.WATERLOGGED))
                world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
            return state;
        }

        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
            return super.getStateForPlacement(context)
                    .setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER)
                    // Add a default axis so it doesn't break when placed manually
                    .setValue(BlockStateProperties.HORIZONTAL_AXIS, context.getHorizontalDirection().getAxis());
        }

    }

    public static class MagnetBlock extends SailBlockBase {

        public MagnetBlock(Properties properties) {
            super(properties);
        }

        @Override
        public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            return KineticShapes.SAIL_MAGNET.get(axis);
        }

    }

    public static class SailBlock extends SailBlockBase {

        public SailBlock(Properties properties) {
            super(properties);
        }

        @Override
        public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            return KineticShapes.SAIL_CLOTH.get(axis);
        }
    }

}