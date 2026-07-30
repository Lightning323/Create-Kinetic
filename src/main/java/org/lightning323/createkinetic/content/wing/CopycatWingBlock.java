package org.lightning323.createkinetic.content.wing;

import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import dev.ryanhcode.sable.api.block.BlockSubLevelCustomCenterOfMass;
import dev.ryanhcode.sable.api.block.BlockSubLevelLiftProvider;
import net.createmod.catnip.math.VoxelShaper;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.lightning323.createkinetic.registries.KineticBlocks;
import org.lightning323.createkinetic.registries.KineticBlockEntities;
import org.lightning323.createkinetic.registries.PropulsionShapes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CopycatWingBlock extends CopycatBlock implements BlockSubLevelLiftProvider, BlockSubLevelCustomCenterOfMass, SpecialBlockItemRequirement {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final Vector3dc CENTER_OF_MASS = new Vector3d(0.5, 0.5, 0.5);
    private static final float BASE_LIFT_SCALAR = 0.475f;
    private final int width;

    private static final List<Supplier<? extends Block>> entires =
        List.of(
                KineticBlocks.COPYCAT_WING,
                KineticBlocks.COPYCAT_WING_8,
                KineticBlocks.COPYCAT_WING_12,
            KineticBlocks.WING_BLOCK);
    private static final int placementHelperId = PlacementHelpers.register(new WingPlacementHelper(entires));

    private static final Map<Integer, VoxelShaper> wingShapers = Map.of(
        4, PropulsionShapes.WING,
        8, PropulsionShapes.WING_8,
        12, PropulsionShapes.WING_12
    );

    public CopycatWingBlock(Properties properties, int width) {
        super(properties);
        this.width = width;
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    public int getWidth() {
        return width;
    }

    @Override
    public BlockEntityType<? extends CopycatBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.COPYCAT_WING_BLOCK_ENTITY.get();
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return wingShapers.get(this.width).get(state.getValue(FACING));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        if (player != null && !player.isShiftKeyDown() && player.mayBuild()) {
            IPlacementHelper placementHelper = PlacementHelpers.get(placementHelperId);
            if (placementHelper.matchesItem(stack) && stack.getItem() instanceof BlockItem blockItem) {
                var offset = placementHelper.getOffset(player, world, state, pos, ray);
                if (offset.isSuccessful()) {
                    if (world.isClientSide) {
                        return ItemInteractionResult.SUCCESS;
                    }
                    return offset.placeInWorld(world, blockItem, player, hand, ray);
                }
            }
        }

        return super.useItemOn(stack, state, world, pos, player, hand, ray);
    }

	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        // Placement with an item is handled in useItemOn() to support Create's helper arrows.
        return InteractionResult.PASS;
    }
    
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        BlockState material = getMaterial(level, pos);
        if (player != null && player.isShiftKeyDown()) {
            return new ItemStack(KineticBlocks.COPYCAT_WING.get());
        }
        
        return material.getBlock().asItem().getDefaultInstance();
    }

    @Override
    public boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        BlockState toState = reader.getBlockState(toPos);
        if (!toState.is(this)) {
            return false;
        }

        return state.getValue(FACING) == toState.getValue(FACING);
    }

    @Override
    public boolean isIgnoredConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face, @Nullable BlockPos fromPos, @Nullable BlockPos toPos) {
        if (fromPos == null || toPos == null) return true;

        BlockState toState = reader.getBlockState(toPos);
        Direction facing = state.getValue(FACING);

        if (!toState.is(this)) return facing != face.getOpposite();

        Direction toFacing = toState.getValue(FACING);
        BlockPos diff = toPos.subtract(fromPos);

        //Avoiding over-gap connections
        if (diff.getX() == 0 && diff.getZ() == 0 && diff.getY() != 0 && facing.getAxis() == Direction.Axis.Y && toFacing.getAxis() == Direction.Axis.Y) {
            return true;
        }
        if (diff.getY() == 0 && diff.getZ() == 0 && diff.getX() != 0 && facing.getAxis() == Direction.Axis.X && toFacing.getAxis() == Direction.Axis.X) {
            return true;
        }
        if (diff.getX() == 0 && diff.getY() == 0 && diff.getZ() != 0 && facing.getAxis() == Direction.Axis.Z && toFacing.getAxis() == Direction.Axis.Z) {
            return true;
        }

        return false;
    }

    @Override
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootParams.Builder builder) {
        int dropCount = this.width / 4;
        if (dropCount < 1) {
            return Collections.emptyList();
        }
        return List.of(new ItemStack(KineticBlocks.COPYCAT_WING.get(), dropCount));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() instanceof CopycatWingBlock) {
            if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
                level.removeBlockEntity(pos);
            }
            return;
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public Direction sable$getNormal(BlockState state) {
        return state.getValue(FACING).getOpposite();
    }

    @Override
    public Vector3dc getCenterOfMass(BlockGetter level, BlockState state) {
        return CENTER_OF_MASS;
    }

    @Override
    public float sable$getLiftScalar() {
        return switch (width) {
            case 8 -> BASE_LIFT_SCALAR * 1.5f;
            case 12 -> BASE_LIFT_SCALAR * 2.0f;
            default -> BASE_LIFT_SCALAR;
        };
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity blockEntity) {
        return new ItemRequirement(
            ItemRequirement.ItemUseType.CONSUME,
            new ItemStack(KineticBlocks.COPYCAT_WING.get(), Math.max(1, width / 4))
        );
    }
}
