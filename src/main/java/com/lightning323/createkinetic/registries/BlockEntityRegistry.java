package com.lightning323.createkinetic.registries;

import net.minecraftforge.eventbus.api.IEventBus;

public class BlockEntityRegistry {
//    public static final BlockEntityEntry<PulleyBlockEntity> ROPE_PULLEY = REGISTRATE
//        .blockEntity("rope_pulley", PulleyBlockEntity::new)
//        .validBlocks(BlockRegistry.ROPE_PULLEY) // Link it to your block
//        .renderer(() -> PulleyRenderer::new)    // This is why the rope is missing!
//        .register();

    public static void register(IEventBus modEventBus) {
        // This just "wakes up" the class to ensure static fields are loaded
    }
}