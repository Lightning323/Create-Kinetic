package com.lightning323.createkinetic.blocks.sail;

import com.lightning323.createkinetic.registries.KineticBlockEntities;
import com.lightning323.createkinetic.registries.KineticItems;
import com.simibubi.create.content.contraptions.pulley.PulleyBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class CustomPulley extends PulleyBlock {
    public CustomPulley(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends PulleyBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.ROPE_PULLEY.get();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos,
                                       Player player) {
        return KineticItems.ROPE_PULLEY.asStack();
    }
}
