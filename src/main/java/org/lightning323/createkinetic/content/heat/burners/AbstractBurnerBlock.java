package org.lightning323.createkinetic.content.heat.burners;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.lightning323.createkinetic.content.heat.burners.liquid.LiquidBurnerBlockEntity;

import javax.annotation.Nonnull;

public abstract class AbstractBurnerBlock extends HorizontalDirectionalBlock implements IBE<AbstractBurnerBlockEntity> {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    //Heat property is from Create. It is used in all blaze-related interactions (boilers, heated mixers, etc.)
    //Using BlazeBurnerBlock.HEAT_LEVEL is required for heated mixers. If I want to disallow my burners to be used in heated mixers - just define custom enum
    public static final EnumProperty<HeatLevel> HEAT = BlazeBurnerBlock.HEAT_LEVEL;

    protected AbstractBurnerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(HEAT, HeatLevel.KINDLED));
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HEAT);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return this.onBlockEntityUseItemOn(level, pos, be -> {
            if (be instanceof LiquidBurnerBlockEntity liquid) {
                if (liquid.tryConsumeFuelBucket(player, hand, stack)) {
                    return ItemInteractionResult.sidedSuccess(level.isClientSide());
                }
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        });
    }

    @Override
    public abstract BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state);

    @Override
    public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type);
}
