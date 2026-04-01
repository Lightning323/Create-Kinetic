package com.lightning323.createkinetic.blocks.sail;

import com.lightning323.createkinetic.blocks.sail.sailPulley.SailBlockBase;
import com.lightning323.createkinetic.registries.KineticShapes;
import com.lightning323.createkinetic.ship.KineticShipControl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SailClothBlock extends SailBlockBase {

    public void addToShip(BlockState state, Level world, BlockPos pos, KineticShipControl controller) {
        Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        Direction.Axis flippedAxis = (axis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;
        controller.addSail((ServerLevel) world, pos, flippedAxis);
    }

    public void removeFromShip(BlockState state, Level world, BlockPos pos, KineticShipControl controller) {
        Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        Direction.Axis flippedAxis = (axis == Direction.Axis.X) ? Direction.Axis.Z : Direction.Axis.X;
        controller.removeSail((ServerLevel) world, pos, flippedAxis);
    }


    public SailClothBlock(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof DyeItem dye) {
            DyeColor dyeColor = dye.getDyeColor();

            // 1. Check if the block is already that color to avoid wasted updates
            if (state.getValue(COLOR) != dyeColor) {
                if (!level.isClientSide) {
                    // 2. Apply the new state with the updated color property
                    level.setBlock(pos, state.setValue(COLOR, dyeColor), 3);

                    // 3. Consume the dye if not in creative
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    // 4. Play a sound (optional but makes it feel "Create-y")
                    level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        return KineticShapes.SAIL_CLOTH.get(axis);
    }
}
