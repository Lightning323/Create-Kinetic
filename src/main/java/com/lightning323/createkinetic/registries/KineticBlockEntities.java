package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.blocks.sail.CustomPulleyBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyRenderer;
import com.simibubi.create.content.contraptions.pulley.RopePulleyVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.lightning323.createkinetic.Createkinetic.REGISTRATE;


public class KineticBlockEntities {
    public static final BlockEntityEntry<CustomPulleyBlockEntity> ROPE_PULLEY = REGISTRATE
            .blockEntity("rope_pulley", CustomPulleyBlockEntity::new)
            .visual(() -> RopePulleyVisual::new, false)
            .validBlocks(KineticItems.ROPE_PULLEY)
            .renderer(() -> PulleyRenderer::new)
            .register();

    public static void register() {
        // This just "wakes up" the class to ensure static fields are loaded
    }
}