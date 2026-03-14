package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.blocks.helm.ShipHelmBlockEntity;
import com.lightning323.createkinetic.blocks.sail.SailBlockEntity;
import com.lightning323.createkinetic.blocks.sail.SailRenderer;
import com.lightning323.createkinetic.blocks.sail.SailVisual;
import com.lightning323.createkinetic.blocks.sail.sailPulley.SailPulleyBlockEntity;
import com.lightning323.createkinetic.blocks.sail.sailPulley.SailPulleyRenderer;
import com.lightning323.createkinetic.blocks.sail.sailPulley.SailPulleyVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import org.valkyrienskies.eureka.blockentity.renderer.ShipHelmBlockEntityRenderer;

import static com.lightning323.createkinetic.Createkinetic.REGISTRATE;


public class KineticBlockEntities {
    /**
     * [17:12:54] [Render thread/WARN] [minecraft/LevelChunk]: Block entity create:rope_pulley @ BlockPos{x=-69, y=67, z=198} state Block{createkinetic:rope_pulley}[axis=x] invalid for ticking:
     * [17:12:54] [Server thread/WARN] [minecraft/LevelChunk]: Block entity create:rope_pulley @ BlockPos{x=-69, y=67, z=198} state Block{createkinetic:rope_pulley}[axis=x] invalid for ticking:
     */
    public static final BlockEntityEntry<SailPulleyBlockEntity> SAIL_PULLEY = REGISTRATE
            .blockEntity("sail_pulley", SailPulleyBlockEntity::new)
            .visual(() -> SailPulleyVisual::new, false)
            .validBlocks(KineticBlocks.SAIL_PULLEY)
            .renderer(() -> SailPulleyRenderer::new)
            .register();

    public static final BlockEntityEntry<SailBlockEntity> SAIL = REGISTRATE
            .blockEntity("sail", SailBlockEntity::new)
            .visual(() -> SailVisual::new, false)
            .validBlocks(KineticBlocks.RETRACTABLE_SAIL)
            .renderer(() -> SailRenderer::new)
            .register();

    public static final BlockEntityEntry<ShipHelmBlockEntity> SHIP_HELM = REGISTRATE
            .<ShipHelmBlockEntity>blockEntity("ship_helm", ShipHelmBlockEntity::new)
            .validBlocks(
                    KineticBlocks.OAK_SHIP_HELM,
                    KineticBlocks.BIRCH_SHIP_HELM,
                    KineticBlocks.JUNGLE_SHIP_HELM,
                    KineticBlocks.ACACIA_SHIP_HELM,
                    KineticBlocks.DARK_OAK_SHIP_HELM,
                    KineticBlocks.CRIMSON_SHIP_HELM,
                    KineticBlocks.WARPED_SHIP_HELM
            )
            //TODO: Add visual for ship helm for better performance
            .renderer(() -> ShipHelmBlockEntityRenderer::new)
            .register();

    public static void register() {
        // This just "wakes up" the class to ensure static fields are loaded
    }
}