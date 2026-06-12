/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.HorizontalKineticBlock
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package org.lightning323.createkinetic.content.blocks.sable_track;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import org.lightning323.createkinetic.registry.KineticBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SableTrackBlock
extends HorizontalKineticBlock
implements IBE<SableTrackBlockEntity> {
    public static final BooleanProperty HIDDEN = BooleanProperty.create((String)"hidden");
    public static final BooleanProperty SUSPENSION_MODEL = BooleanProperty.create((String)"suspension_model");
    private static final VoxelShape HIDDEN_SELECTION_SHAPE = Block.box((double)0.0, (double)14.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0);
    private final SableTrackRole role;

    public SableTrackBlock(Properties properties, SableTrackRole role) {
        super(properties);
        this.role = role;
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)HIDDEN, (Comparable)Boolean.valueOf(false))).setValue((Property)SUSPENSION_MODEL, (Comparable)Boolean.valueOf(false)));
    }

    public SableTrackRole role() {
        return this.role;
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            SableTrackBlockEntity be = (SableTrackBlockEntity)level.getBlockEntity(pos);
            if (be != null && !be.getHeldItem().isEmpty()) {
                Direction facing = (Direction)state.getValue(HORIZONTAL_FACING);
                BlockPos dropPos = pos.relative(facing);
                Containers.dropItemStack((Level)level, (double)dropPos.getX(), (double)dropPos.getY(), (double)dropPos.getZ(), (ItemStack)be.getHeldItem());
            }
            level.removeBlockEntity(pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(new Property[]{HIDDEN, SUSPENSION_MODEL});
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean crouching;
        Direction preferred = this.getPreferredHorizontalFacing(context);
        boolean bl = crouching = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        if (preferred == null || crouching) {
            Direction horizontalDirection = context.getHorizontalDirection();
            return (BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, crouching ? horizontalDirection : horizontalDirection.getOpposite());
        }
        return (BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, preferred.getOpposite());
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == ((Direction)state.getValue(HORIZONTAL_FACING)).getAxis();
    }

    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (this.role != SableTrackRole.MOUNT) {
            return super.useItemOn(heldItem, state, level, pos, player, hand, hitResult);
        }
        if (player.isShiftKeyDown() && hitResult.getDirection() == Direction.UP) {
            if (!level.isClientSide) {
                this.toggleHiddenMount(state, level, pos);
            }
            return ItemInteractionResult.CONSUME;
        }
        if (player.isShiftKeyDown() && hitResult.getDirection() == state.getValue(HORIZONTAL_FACING)) {
            if (!level.isClientSide) {
                this.withBlockEntityDo((BlockGetter)level, pos, SableTrackBlockEntity::toggleVisualSuspensionHidden);
                level.playSound(null, pos, (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5f, 1.1f);
            }
            return ItemInteractionResult.CONSUME;
        }
        Item item = heldItem.getItem();
        if (item instanceof DyeItem) {
            DyeItem dyeItem = (DyeItem)item;
            if (!level.isClientSide) {
                this.withBlockEntityDo((BlockGetter)level, pos, mount -> mount.applyBeltColorToConnectedTrack(dyeItem.getDyeColor()));
                if (!player.hasInfiniteMaterials()) {
                    heldItem.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.PLAYERS, 0.8f, 1.0f);
            }
            return ItemInteractionResult.CONSUME;
        }
        if (heldItem.is(Items.WATER_BUCKET)) {
            boolean[] changed = new boolean[]{false};
            if (!level.isClientSide) {
                this.withBlockEntityDo((BlockGetter)level, pos, mount -> {
                    changed[0] = mount.applyBeltColorToConnectedTrack(null);
                });
                if (changed[0] && !player.hasInfiniteMaterials()) {
                    heldItem.shrink(1);
                    player.getInventory().placeItemBackInInventory(new ItemStack((ItemLike)Items.BUCKET));
                }
                if (changed[0]) {
                    level.playSound(null, pos, SoundEvents.AXOLOTL_SPLASH, SoundSource.PLAYERS, 0.8f, 1.0f);
                }
            }
            return level.isClientSide ? ItemInteractionResult.SUCCESS : (changed[0] ? ItemInteractionResult.CONSUME : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
        }
        SableTrackPart heldPart = SableTrackPart.fromStack(heldItem);
        if (!heldItem.isEmpty() && heldPart == SableTrackPart.NONE) {
            return super.useItemOn(heldItem, state, level, pos, player, hand, hitResult);
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        boolean[] handled = new boolean[]{false};
        this.withBlockEntityDo((BlockGetter)level, pos, mount -> {
            if (!heldItem.isEmpty() && heldPart == SableTrackPart.NONE) {
                return;
            }
            ItemStack previous = mount.getHeldItem().copy();
            mount.setHeldItem(heldItem.isEmpty() ? ItemStack.EMPTY : heldItem.copyWithCount(1));
            if (!heldItem.isEmpty() && !player.hasInfiniteMaterials()) {
                heldItem.shrink(1);
            }
            if (!previous.isEmpty()) {
                player.getInventory().placeItemBackInInventory(previous);
            }
            float pitch = 0.8f + level.random.nextFloat() * 0.4f;
            level.playSound(null, pos, heldItem.isEmpty() ? SoundEvents.ITEM_PICKUP : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 0.75f, pitch);
            handled[0] = true;
        });
        return handled[0] ? ItemInteractionResult.CONSUME : super.useItemOn(heldItem, state, level, pos, player, hand, hitResult);
    }

    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (this.role == SableTrackRole.MOUNT && context.getClickedFace() == Direction.UP) {
            Level level = context.getLevel();
            if (!level.isClientSide) {
                this.toggleHiddenMount(state, level, context.getClickedPos());
            }
            return InteractionResult.SUCCESS;
        }
        if (this.role == SableTrackRole.MOUNT && context.getClickedFace() == state.getValue(HORIZONTAL_FACING)) {
            Level level = context.getLevel();
            if (!level.isClientSide) {
                this.withBlockEntityDo((BlockGetter)level, context.getClickedPos(), SableTrackBlockEntity::toggleVisualSuspensionHidden);
                level.playSound(null, context.getClickedPos(), (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5f, 1.1f);
            }
            return InteractionResult.SUCCESS;
        }
        return super.onSneakWrenched(state, context);
    }

    private void toggleHiddenMount(BlockState state, Level level, BlockPos pos) {
        level.setBlock(pos, (BlockState)state.cycle((Property)HIDDEN), 2);
        level.playSound(null, pos, (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5f, (Boolean)state.getValue((Property)HIDDEN) != false ? 0.8f : 1.2f);
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue(HORIZONTAL_FACING)).getAxis();
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return (Boolean)state.getValue((Property)HIDDEN) != false ? HIDDEN_SELECTION_SHAPE : Shapes.block();
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (((Boolean)state.getValue((Property)HIDDEN)).booleanValue() && context != CollisionContext.empty()) {
            return Shapes.empty();
        }
        return Shapes.block();
    }

    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return (Boolean)state.getValue((Property)HIDDEN) != false ? Shapes.empty() : Shapes.block();
    }

    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return (Boolean)state.getValue((Property)HIDDEN) != false ? Shapes.empty() : super.getVisualShape(state, level, pos, context);
    }

    protected boolean useShapeForLightOcclusion(BlockState state) {
        return (Boolean)state.getValue((Property)HIDDEN) == false && super.useShapeForLightOcclusion(state);
    }

    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return (Boolean)state.getValue((Property)HIDDEN) != false || super.propagatesSkylightDown(state, level, pos);
    }

    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return (Boolean)state.getValue((Property)HIDDEN) != false ? 1.0f : super.getShadeBrightness(state, level, pos);
    }

    protected RenderShape getRenderShape(BlockState state) {
        return (Boolean)state.getValue((Property)HIDDEN) != false ? RenderShape.INVISIBLE : super.getRenderShape(state);
    }

    public Class<SableTrackBlockEntity> getBlockEntityClass() {
        return SableTrackBlockEntity.class;
    }

    public BlockEntityType<? extends SableTrackBlockEntity> getBlockEntityType() {
        return (BlockEntityType) KineticBlockEntityTypes.SABLE_TRACK.get();
    }
}

