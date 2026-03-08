package com.lightning323.createkinetic.blocks.sail;

import com.lightning323.createkinetic.registries.KineticBlockEntities;
import com.simibubi.create.content.contraptions.pulley.PulleyBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SailPulley extends PulleyBlock {
    public SailPulley(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends PulleyBlockEntity> getBlockEntityType() {
        return KineticBlockEntities.ROPE_PULLEY.get();
    }
}