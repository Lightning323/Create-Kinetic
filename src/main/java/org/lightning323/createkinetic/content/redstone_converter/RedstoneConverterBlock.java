package org.lightning323.createkinetic.content.redstone_converter;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.redstone.diodes.AbstractDiodeBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import org.lightning323.createkinetic.registries.KineticBlockEntities;

public class RedstoneConverterBlock extends AbstractDiodeBlock implements IBE<RedstoneConverterBlockEntity> {
    public static final MapCodec<RedstoneConverterBlock> CODEC = simpleCodec(RedstoneConverterBlock::new);

    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(0, 0, 0, 16, 2, 16),
        Block.box(4, 2, 4, 12, 7, 12)
    );

    public RedstoneConverterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected MapCodec<? extends DiodeBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    protected void checkTickOnNeighbor(Level level, BlockPos pos, BlockState state) {
        super.checkTickOnNeighbor(level, pos, state);
        BlockState newState = getUpdatedBlockState(pos, state, level);
        if (state.getValue(POWERED) != newState.getValue(POWERED)) {
            level.setBlock(pos, newState, 2);
            level.updateNeighborsAt(pos, this);
            level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {}

    public BlockState getUpdatedBlockState(BlockPos pos, BlockState state, Level level) {
        final Direction facing = state.getValue(FACING);
        final BlockPos inputPos = pos.relative(facing);
        final boolean hasSignal = level.getSignal(inputPos, facing) > 0;
        return state.setValue(POWERED, hasSignal);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        return state.getValue(FACING) == dir ? getOutputSignal(level, pos, state) : 0;
    }

    protected int getOutputSignal(BlockGetter level, BlockPos pos, BlockState state) {
        if (!state.getValue(POWERED)) return 0;
        final RedstoneConverterBlockEntity be = (RedstoneConverterBlockEntity) level.getBlockEntity(pos);
        return be != null ? be.outputStrength.getValue() : 15;
    }

    @Override
    protected int getDelay(BlockState state) {
        return 0;
    }

    @Override
    public Class<RedstoneConverterBlockEntity> getBlockEntityClass() {
        return RedstoneConverterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RedstoneConverterBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.REDSTONE_CONVERTER_BLOCK_ENTITY.get();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED) && random.nextFloat() < 0.25f)
            level.addParticle(new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.0F), 1f),
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
    }
}
