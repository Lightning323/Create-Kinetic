package org.lightning323.createkinetic.content.wing;

import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.lightning323.createkinetic.registries.KineticBlocks;

import javax.annotation.Nonnull;

public class CopycatWingItem extends BlockItem {
    public CopycatWingItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState clickedState = world.getBlockState(pos);

        if (player != null && player.isShiftKeyDown() && clickedState.getBlock() instanceof CopycatWingBlock) {
            CopycatWingBlock clickedWing = (CopycatWingBlock) clickedState.getBlock();
            if (clickedWing.getWidth() != 12) {
                BlockState targetState = (clickedWing.getWidth() == 4)
                    ? KineticBlocks.COPYCAT_WING_8.get().defaultBlockState()
                    : KineticBlocks.COPYCAT_WING_12.get().defaultBlockState();
                
                targetState = targetState.setValue(CopycatWingBlock.FACING, clickedState.getValue(CopycatWingBlock.FACING));
                if (!world.isClientSide()) {
                    CopycatBlockEntity oldCopycat = null;
                    BlockEntity oldBE = world.getBlockEntity(pos);
                    if (oldBE instanceof CopycatBlockEntity) {
                        oldCopycat = (CopycatBlockEntity) oldBE;
                    }

                    world.setBlock(pos, targetState, 3);

                    // Retain copycat material safely without copying full BE metadata between variants.
                    BlockEntity newBE = world.getBlockEntity(pos);
                    if (oldCopycat != null && newBE instanceof CopycatBlockEntity newCopycat) {
                        newCopycat.setMaterial(oldCopycat.getMaterial());
                        newCopycat.setConsumedItem(oldCopycat.getConsumedItem());
                    }
                    
                    if (!player.getAbilities().instabuild) {
                        context.getItemInHand().shrink(1);
                    }
                }
                
                world.playSound(player, pos, targetState.getSoundType(world, pos, player).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.sidedSuccess(world.isClientSide());
            }
        }
        return super.useOn(context);
    }

}
