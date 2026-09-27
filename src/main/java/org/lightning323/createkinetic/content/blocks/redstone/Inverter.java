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

/**
 * An analog redstone inverter. The output is emitted from the side opposite {@link #FACING}
 * and is equal to {@code 15 - input}. Input is accepted from every side except the output
 * side, so the signal can be fed from the back, either flank, above or below.
 */
public class Inverter extends DiodeBlock {
    public static final MapCodec<Inverter> CODEC = simpleCodec(Inverter::new);
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public Inverter(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static Inverter create() {
        return new Inverter(BlockBehaviour.Properties.of()
                .mapColor(MapColor.NONE)
                .sound(SoundType.COPPER)
                .strength(0.0F, 0.0F));
    }

    @Override
    public MapCodec<Inverter> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWER, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        if (state == null) {
            return null;
        }
        int output = 15 - getInputSignal(ctx.getLevel(), ctx.getClickedPos(), state);
        return state.setValue(POWER, output).setValue(POWERED, output > 0);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        int power = state.getValue(POWER);
        if (power == 0 || state.getValue(FACING) != side) {
            return 0;
        }
        BlockState receiver = level.getBlockState(pos.relative(side.getOpposite()));
        return receiver.is(Blocks.REDSTONE_WIRE) ? Math.max(power - 1, 0) : power;
    }

    @Override
    protected int getOutputSignal(BlockGetter level, BlockPos pos, BlockState state) {
        return state.getValue(POWER);
    }

    @Override
    protected boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) {
        return getInputSignal(level, pos, state) < 15;
    }

    @Override
    protected int getInputSignal(Level level, BlockPos pos, BlockState state) {
        Direction output = state.getValue(FACING).getOpposite();
        int max = 0;
        for (Direction direction : Direction.values()) {
            if (direction == output) {
                continue;
            }
            int signal = level.getSignal(pos.relative(direction), direction);
            if (signal > max) {
                max = signal;
            }
        }
        return max;
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
        int output = 15 - getInputSignal(level, pos, state);
        boolean powered = output > 0;
        if (state.getValue(POWER) != output || state.getValue(POWERED) != powered) {
            level.setBlock(pos, state.setValue(POWER, output).setValue(POWERED, powered), Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS);
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
        if (state.getValue(POWERED) && random.nextFloat() > 0.4F) {
            makeParticle(state, level, pos);
        }
    }

    @Override
    protected int getDelay(BlockState state) {
        return 0;
    }
}
