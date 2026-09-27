package org.lightning323.createkinetic.content.blocks.redstone;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.event.EventHooks;

import java.util.EnumSet;

public class Crossroad extends DiodeBlock {
    public static final MapCodec<Crossroad> CODEC = simpleCodec(Crossroad::new);
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final IntegerProperty FLANK_POWER = IntegerProperty.create("flank_power", 0, 15);
    public static final IntegerProperty MODEL_TYPE = IntegerProperty.create("model_type", 0, 3);

    public Crossroad(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static Crossroad create() {
        return new Crossroad(BlockBehaviour.Properties.of()
                .mapColor(MapColor.NONE)
                .sound(SoundType.COPPER)
                .strength(0.0F, 0.0F));
    }

    @Override
    public MapCodec<Crossroad> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWER, FLANK_POWER, MODEL_TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        return state != null ? state.setValue(POWER, getInputSignal(ctx.getLevel(), ctx.getClickedPos(), state)) : null;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        if (state.getValue(POWER) == 0 && state.getValue(FLANK_POWER) == 0) {
            return 0;
        }
        Direction facing = state.getValue(FACING);
        boolean wire = level.getBlockState(pos.relative(side.getOpposite())).is(Blocks.REDSTONE_WIRE);
        if (facing == side || facing.getOpposite() == side) {
            int power = state.getValue(POWER);
            return wire ? Math.max(power - 1, 0) : power;
        }
        if (facing.getCounterClockWise() == side || facing.getClockWise() == side) {
            int power = state.getValue(FLANK_POWER);
            return wire ? Math.max(power - 1, 0) : power;
        }
        return 0;
    }

    @Override
    protected int getOutputSignal(BlockGetter level, BlockPos pos, BlockState state) {
        return state.getValue(POWER);
    }

    protected int getFlankOutputSignal(BlockGetter level, BlockPos pos, BlockState state) {
        return state.getValue(FLANK_POWER);
    }

    @Override
    protected boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) {
        return state.getValue(POWER) > 0;
    }

    @Override
    protected int getInputSignal(Level level, BlockPos pos, BlockState state) {
        Direction back = state.getValue(FACING);
        BlockPos backPos = pos.relative(back);
        Direction front = back.getOpposite();
        BlockPos frontPos = pos.relative(front);
        BlockPos source = level.getSignal(frontPos, front) > level.getSignal(backPos, back) ? frontPos : backPos;
        int signal = Math.max(level.getSignal(frontPos, front), level.getSignal(backPos, back));
        BlockState sourceState = level.getBlockState(source);
        if (RedstoneGateHelper.attenuates(sourceState)) {
            signal = Math.max(signal - 1, 0);
        }
        int wire = RedstoneGateHelper.wirePower(sourceState);
        return wire > 0 ? wire - 1 : signal;
    }

    protected int getFlankInputSignal(Level level, BlockPos pos, BlockState state) {
        Direction left = state.getValue(FACING).getClockWise();
        BlockPos leftPos = pos.relative(left);
        Direction right = left.getOpposite();
        BlockPos rightPos = pos.relative(right);
        BlockPos source = level.getSignal(rightPos, right) > level.getSignal(leftPos, left) ? rightPos : leftPos;
        int signal = Math.max(level.getSignal(rightPos, right), level.getSignal(leftPos, left));
        BlockState sourceState = level.getBlockState(source);
        if (RedstoneGateHelper.attenuates(sourceState)) {
            signal = Math.max(signal - 1, 0);
        }
        int wire = RedstoneGateHelper.wirePower(sourceState);
        return wire > 0 ? wire - 1 : signal;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos fromPos, boolean isMoving) {
        if (!state.canSurvive(level, pos)) {
            super.neighborChanged(state, level, pos, neighbor, fromPos, isMoving);
            return;
        }
        refreshOutput(level, pos, state);
    }

    @Override
    protected void checkTickOnNeighbor(Level level, BlockPos pos, BlockState state) {
        refreshOutput(level, pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        refreshOutput(level, pos, state);
    }

    private void refreshOutput(Level level, BlockPos pos, BlockState state) {
        if (isLocked(level, pos, state)) {
            return;
        }
        int power = getInputSignal(level, pos, state);
        int flankPower = getFlankInputSignal(level, pos, state);
        int modelType = (power != 0 ? 2 : 0) | (flankPower != 0 ? 1 : 0);
        if (state.getValue(POWER) != power || state.getValue(FLANK_POWER) != flankPower || state.getValue(MODEL_TYPE) != modelType) {
            level.setBlock(pos, state
                    .setValue(POWER, power)
                    .setValue(FLANK_POWER, flankPower)
                    .setValue(MODEL_TYPE, modelType), Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void updateNeighborsInFront(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        EnumSet<Direction> sides = EnumSet.of(
                facing,
                facing.getOpposite(),
                facing.getClockWise(),
                facing.getCounterClockWise());
        if (EventHooks.onNeighborNotify(level, pos, level.getBlockState(pos), sides, false).isCanceled()) {
            return;
        }
        for (Direction side : sides) {
            BlockPos neighbor = pos.relative(side.getOpposite());
            level.neighborChanged(neighbor, this, pos);
            level.updateNeighborsAtExceptFromFacing(neighbor, this, side);
        }
    }

    private static void makeParticle(BlockState state, LevelAccessor level, BlockPos pos) {
        Direction direction = state.getValue(FACING).getOpposite();
        double x = pos.getX() + 0.5D - 0.1D * direction.getStepX();
        double y = pos.getY() + 0.35D;
        double z = pos.getZ() + 0.5D - 0.1D * direction.getStepZ();
        level.addParticle((ParticleOptions) new DustParticleOptions(DustParticleOptions.REDSTONE_PARTICLE_COLOR, 0.9F), x, y, z, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if ((state.getValue(POWER) > 0 || state.getValue(FLANK_POWER) > 0) && random.nextFloat() > 0.4F) {
            makeParticle(state, level, pos);
        }
    }

    @Override
    protected int getDelay(BlockState state) {
        return 0;
    }
}
