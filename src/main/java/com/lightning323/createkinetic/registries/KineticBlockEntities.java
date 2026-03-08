package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.blocks.sail.SailBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyRenderer;
import com.simibubi.create.content.contraptions.pulley.RopePulleyVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.lightning323.createkinetic.Createkinetic.REGISTRATE;


public class KineticBlockEntities {
    /**
     * [17:12:54] [Render thread/WARN] [minecraft/LevelChunk]: Block entity create:rope_pulley @ BlockPos{x=-69, y=67, z=198} state Block{createkinetic:rope_pulley}[axis=x] invalid for ticking:
     * [17:12:54] [Server thread/WARN] [minecraft/LevelChunk]: Block entity create:rope_pulley @ BlockPos{x=-69, y=67, z=198} state Block{createkinetic:rope_pulley}[axis=x] invalid for ticking:
     */
    public static final BlockEntityEntry<SailBlockEntity> ROPE_PULLEY = REGISTRATE
            .blockEntity("rope_pulley", SailBlockEntity::new)
            .visual(() -> RopePulleyVisual::new, false)
            .validBlocks(KineticItems.ROPE_PULLEY)
            .renderer(() -> PulleyRenderer::new)
            .register();

    public static void register() {
        // This just "wakes up" the class to ensure static fields are loaded
    }
}