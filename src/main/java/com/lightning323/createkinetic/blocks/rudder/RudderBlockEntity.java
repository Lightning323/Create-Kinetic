package com.lightning323.createkinetic.blocks.rudder;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RudderBlockEntity extends KineticBlockEntity {
    public float flapRotation = 0;

    public RudderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Override
    public void tick() {
        super.tick();
        System.out.println("TICK");
    }
}