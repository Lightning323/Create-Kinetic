package com.lightning323.createkinetic.blocks.sail.sailPulley;

import com.lightning323.createkinetic.blocks.sail.RetractableSailBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;

import com.lightning323.createkinetic.blocks.sail.RetractableSailBlockEntity;
import com.lightning323.createkinetic.registries.KineticBlockEntities;
import com.lightning323.createkinetic.registries.KineticBlocks;
import com.lightning323.createkinetic.registries.KineticShapes;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.lightning323.createkinetic.ship.ShipUtils;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SailBlockBase extends Block implements SimpleWaterloggedBlock {

    public SailBlockBase(Properties properties) {
        super(properties);
        //Default definitions prevent crashes if not all of them are specified when the block is set
        registerDefaultState(super.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, DyeColor.WHITE));
    }


    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_AXIS, BlockStateProperties.WATERLOGGED);
        builder.add(COLOR);
        super.createBlockStateDefinition(builder);
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
        return KineticBlocks.SAIL_PULLEY.asStack();
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving) {

            if (state.getBlock() == newState.getBlock()
                    && newState.hasProperty(COLOR)
                    && newState.getValue(COLOR) != state.getValue(COLOR)) {
                DyeColor newColor = newState.getValue(COLOR);
                if (!worldIn.isClientSide) {
                    BlockState above = worldIn.getBlockState(pos.above());
                    BlockState below = worldIn.getBlockState(pos.below());

                    if (above.getBlock() instanceof SailPulleyBlock || above.getBlock() instanceof RetractableSailBlock) {
                        //Blockstate is immutable, meaning in order to change it, it must be replaced with a new one
                        worldIn.setBlock(pos.above(), above.setValue(COLOR, newColor), 3);
                    } else if (above.getBlock() instanceof SailBlockBase)
                        worldIn.setBlock(pos.above(), above.setValue(COLOR, newColor), 3);
                    if (below.getBlock() instanceof SailBlockBase)
                        worldIn.setBlock(pos.below(), below.setValue(COLOR, newColor), 3);
                }
            } else if (!state.hasProperty(BlockStateProperties.WATERLOGGED)
                    || !newState.hasProperty(BlockStateProperties.WATERLOGGED)
                    || state.getValue(BlockStateProperties.WATERLOGGED) == newState.getValue(BlockStateProperties.WATERLOGGED)) {
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
        }
        if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            worldIn.removeBlockEntity(pos);
        }
    }

    private static void onRopeBroken(Level world, BlockPos sailPos) {
        BlockEntity be = world.getBlockEntity(sailPos);
        if (be instanceof SailPulleyBlockEntity sail) {
            sail.initialOffset = 0;
            sail.onLengthBroken();
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
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