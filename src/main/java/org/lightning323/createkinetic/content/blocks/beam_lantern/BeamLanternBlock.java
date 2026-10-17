package org.lightning323.createkinetic.content.blocks.beam_lantern;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.lightning323.createkinetic.registries.KineticBlockEntities;

public class BeamLanternBlock extends Block implements IBE<BeamLanternBlockEntity>, IWrenchable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DEFAULT_ON = BooleanProperty.create("default_on");

    public BeamLanternBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(POWERED, false)
                .setValue(LIT, false)
                .setValue(UP, false)
                .setValue(DEFAULT_ON, true));
    }

    public static Direction getFacing(BlockState state) {
        boolean isUp = state.getValue(BeamLanternBlock.UP);
        return isUp ? Direction.UP : Direction.DOWN;
    }

    /**
     * When DEFAULT_ON is set, the lantern is lit while unpowered and dark while powered (inverted).
     * Otherwise it behaves like a regular redstone lamp (lit while powered).
     */
    public static boolean isLitFor(BlockState state) {
        return state.getValue(POWERED) != state.getValue(DEFAULT_ON);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            // Cycle the UP property (change orientation)
            BlockState newState = state.cycle(UP);
            level.setBlock(pos, newState, 11);

            // Tell the block entity to instantly clean up old beam and re-emit in the new direction
            withBlockEntityDo(serverLevel, pos, be -> {
                be.handleDirectionChange(getFacing(newState));
            });
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UP, POWERED, LIT, DEFAULT_ON);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        if (facing != Direction.UP && facing != Direction.DOWN) {
            facing = Direction.DOWN;
        }
        BlockState state = this.defaultBlockState().setValue(BeamLanternBlock.UP, facing == Direction.UP);
        state = state.setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
        // Derive the initial lit state so the block is correct the moment it is placed. Do NOT
        // try to reconcile this from onPlace(): onPlace runs on every setBlockState, so any state
        // write from there recursively re-enters onPlace and overflows the stack.
        return state.setValue(LIT, isLitFor(state));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hitResult) {
        // Let the wrench handle its own interaction (rotation/pickup).
        if (player.getMainHandItem().is(AllItems.WRENCH)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            level.setBlock(pos, state.cycle(DEFAULT_ON), Block.UPDATE_CLIENTS);
            withBlockEntityDo(level, pos, BeamLanternBlockEntity::updatePowered);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }
        // Delegate power evaluation and charge scheduling directly to the block entity
        withBlockEntityDo(level, pos, BeamLanternBlockEntity::updatePowered);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Triggers when a scheduled charge-up tick finishes
        withBlockEntityDo(level, pos, BeamLanternBlockEntity::serverTick);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && level instanceof ServerLevel serverLevel) {
            withBlockEntityDo(serverLevel, pos, be -> be.removeBeam(serverLevel));
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }


    @Override
    public boolean isSignalSource(BlockState state) {
        return false;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(LIT) ? 15 : 0;
    }

    @Override
    public Class<BeamLanternBlockEntity> getBlockEntityClass() {
        return BeamLanternBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BeamLanternBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.BEAM_LANTERN.get();
    }
}