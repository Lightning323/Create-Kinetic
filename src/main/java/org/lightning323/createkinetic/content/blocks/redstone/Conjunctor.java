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
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;

public class Conjunctor extends DiodeBlock {
    public static final MapCodec<Conjunctor> CODEC = simpleCodec(Conjunctor::new);
    public static final IntegerProperty POWER_TYPE = IntegerProperty.create("power_type", 0, 3);

    public Conjunctor(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static Conjunctor create() {
        return new Conjunctor(BlockBehaviour.Properties.of()
                .mapColor(MapColor.NONE)
                .sound(SoundType.COPPER)
                .strength(0.0F, 0.0F));
    }

    @Override
    public MapCodec<Conjunctor> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWER_TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        return state != null ? state.setValue(POWER_TYPE, getInputSignal(ctx.getLevel(), ctx.getClickedPos(), state)) : null;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        if (state.getValue(POWER_TYPE) != 3 || state.getValue(FACING) != side) {
            return 0;
        }
        return getOutputSignal(level, pos, state);
    }

    @Override
    protected int getOutputSignal(BlockGetter level, BlockPos pos, BlockState state) {
        return 15;
    }

    @Override
    protected boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) {
        return getInputSignal(level, pos, state) == 3;
    }

    @Override
    protected int getInputSignal(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        Direction right = facing.getCounterClockWise();
        Direction left = facing.getClockWise();
        boolean rightOn = level.getSignal(pos.relative(right), right) >= 1;
        boolean leftOn = level.getSignal(pos.relative(left), left) >= 1;
        if (rightOn && leftOn) {
            return 3;
        }
        if (leftOn) {
            return 2;
        }
        if (rightOn) {
            return 1;
        }
        return 0;
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
        int type = getInputSignal(level, pos, state);
        if (state.getValue(POWER_TYPE) != type) {
            level.setBlock(pos, state.setValue(POWER_TYPE, type), Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS);
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
        if (state.getValue(POWER_TYPE) == 3 && random.nextFloat() > 0.4F) {
            makeParticle(state, level, pos);
        }
    }

    @Override
    protected int getDelay(BlockState state) {
        return 0;
    }
}
