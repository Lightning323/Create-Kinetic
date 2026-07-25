package org.lightning323.createkinetic.content.wing;

import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.lightning323.createkinetic.registries.PropulsionBlockEntities;

public class PropulsionCopycatWingBlockEntity extends CopycatBlockEntity {
    public PropulsionCopycatWingBlockEntity(BlockPos pos, BlockState state) {
        super(PropulsionBlockEntities.COPYCAT_WING_BLOCK_ENTITY.get(), pos, state);
    }
}

