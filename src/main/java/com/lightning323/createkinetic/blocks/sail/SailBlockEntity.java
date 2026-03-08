package com.lightning323.createkinetic.blocks.sail;

import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SailBlockEntity extends PulleyBlockEntity {
    public SailBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
