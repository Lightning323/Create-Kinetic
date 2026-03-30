package com.lightning323.createkinetic.blocks.helm;

import com.lightning323.createkinetic.CreateKinetic;
import com.lightning323.createkinetic.blocks.rudder.RudderBlockEntity;
import com.lightning323.createkinetic.registries.KineticBlockEntities;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class ShipHelmBlock extends KineticBlock implements IBE<ShipHelmBlockEntity> {
    public final WoodType woodType;
    private final WoodTypeEnum woodTypeEnum;
    public static final VoxelShape SIMPLE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public ShipHelmBlock(Properties properties, WoodType woodType) {
        super(properties);
        this.woodType = woodType;
        this.woodTypeEnum = WoodTypeEnum.fromVanilla(woodType);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    public WoodTypeEnum getWoodTypeEnum() {
        return this.woodTypeEnum;
    }

    @Override
    public Class<ShipHelmBlockEntity> getBlockEntityClass() {
        return ShipHelmBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ShipHelmBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.SHIP_HELM.get();
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (level.isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) level;

        // Using Valkyrien Skies static bridge methods
        if (VSGameUtilsKt.getShipManagingPos(serverLevel, pos) != null) {
            KineticShipControl control = KineticShipControl.getOrAddController(serverLevel, pos);
            if (control != null) {
                Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                control.preferredDirection = (direction);
                control.helms = (control.helms + 1);
                control.updateShipDirection();
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) level;
                KineticShipControl control = KineticShipControl.getController(serverLevel, pos);
                if (control != null) {
                    if (control.helms <= 1 && control.seatedPlayer != null) {
                        if (control.seatedPlayer.getVehicle() != null &&
                                control.seatedPlayer.getVehicle().getType() == ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE) {
                            control.seatedPlayer.unRide();
                            control.seatedPlayer = (null);
                        }
                    }
                    control.helms = (control.helms - 1);
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ShipHelmBlockEntity blockEntity)) return InteractionResult.PASS;

        if (VSGameUtilsKt.getShipManagingPos(level, pos) == null) {
            player.displayClientMessage(Component.translatable("info." + CreateKinetic.MOD_ID + ".needs_ship"), true);
            return InteractionResult.CONSUME;
        } else if (blockEntity.sit(player, false)) {
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SIMPLE_SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState blockState) {
        return true;
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (lvl, pos, blockState, t) -> {
            if (lvl.isClientSide) return;
            if (t instanceof ShipHelmBlockEntity helm) {
                helm.tick();
            }
        };
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }
}