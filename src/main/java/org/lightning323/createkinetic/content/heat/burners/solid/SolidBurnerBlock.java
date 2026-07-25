package org.lightning323.createkinetic.content.heat.burners.solid;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.lightning323.createkinetic.content.heat.burners.AbstractBurnerBlock;
import org.lightning323.createkinetic.content.heat.burners.AbstractBurnerBlockEntity;
import org.lightning323.createkinetic.registries.PropulsionBlockEntities;
import org.lightning323.createkinetic.utility.burners.BurnerFuelBehaviour;

import javax.annotation.Nonnull;

public class SolidBurnerBlock extends AbstractBurnerBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public SolidBurnerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(LIT, false));
    }

    @Override
    public Class<AbstractBurnerBlockEntity> getBlockEntityClass() {
        return AbstractBurnerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AbstractBurnerBlockEntity> getBlockEntityType() {
        return PropulsionBlockEntities.SOLID_BURNER_BLOCK_ENTITY.get();
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new SolidBurnerBlockEntity(PropulsionBlockEntities.SOLID_BURNER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HEAT, LIT);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        if (type == PropulsionBlockEntities.SOLID_BURNER_BLOCK_ENTITY.get()) {
            return new SmartBlockEntityTicker<>();
        }
        return null;
    }

    private boolean handleUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        if (player == null) return false;
        if (player.isSpectator() || player.isShiftKeyDown()) {
            return false;
        }

        if (level.isClientSide()) {
            // Mirror the server-side check read-only so we don't eat block placements
            ItemStack stack = player.getItemInHand(hand);
            if (level.getBlockEntity(pos) instanceof SolidBurnerBlockEntity blockEntity) {
                BurnerFuelBehaviour behaviour = blockEntity.getBehaviour(BurnerFuelBehaviour.TYPE);
                if (behaviour != null) {
                    if (stack.isEmpty() && !behaviour.fuelStack.isEmpty()) return true;
                    if (!stack.isEmpty() && stack.getBurnTime(RecipeType.SMELTING) > 0 && !stack.hasCraftingRemainingItem()) return true;
                }
            }
            return false;
        }

        if (level.getBlockEntity(pos) instanceof SolidBurnerBlockEntity blockEntity) {
            BurnerFuelBehaviour behaviour = blockEntity.getBehaviour(BurnerFuelBehaviour.TYPE);
            if (behaviour == null) return false;

            if (behaviour.handlePlayerInteraction(player, hand)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return handleUse(state, level, pos, player, hand)
            ? ItemInteractionResult.SUCCESS
            : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return handleUse(state, level, pos, player, InteractionHand.MAIN_HAND)
            ? InteractionResult.SUCCESS
            : InteractionResult.PASS;
    }

    @Override
    public void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level.isClientSide()) {
            return;
        }
        if (level.getBlockEntity(pos) instanceof SolidBurnerBlockEntity burner) {
            burner.updatePoweredState();
        }
    }

    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SolidBurnerBlockEntity burnerBlockEntity) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), burnerBlockEntity.getFuelStack());
            }
            level.removeBlockEntity(pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide()) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof SolidBurnerBlockEntity burner) {
            burner.updatePoweredState();
        }
    }

    //Took this from blast furnace
    public void animateTick(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull RandomSource pRandom) {
        if (pState.getValue(LIT)) {
            double d0 = (double)pPos.getX() + 0.5D;
            double d1 = (double)pPos.getY();
            double d2 = (double)pPos.getZ() + 0.5D;
            if (pRandom.nextDouble() < 0.02D) {
            pLevel.playLocalSound(d0, d1, d2, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.35F, 0.95F + pRandom.nextFloat() * 0.1F, false);
            }

            Direction direction = pState.getValue(FACING);
            Direction.Axis direction$axis = direction.getAxis();
            double d4 = pRandom.nextDouble() * 0.6D - 0.3D;
            double d5 = direction$axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52D : d4;
            double d6 = pRandom.nextDouble() * 9.0D / 16.0D;
            double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52D : d4;
            pLevel.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected MapCodec<SolidBurnerBlock> codec() {
        return Block.CODEC.xmap(props -> (SolidBurnerBlock) this, block -> (SolidBurnerBlock) block);
    }
}

