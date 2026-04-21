package org.lightning323.createkinetic.registries;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import org.lightning323.createkinetic.blocks.thruster.ThrusterBlockEntity;

import static org.lightning323.createkinetic.CreateKinetic.REGISTRATE;

public class KineticBlockEntitiyTypes {

    public static final BlockEntityEntry<ThrusterBlockEntity> THRUSTER = REGISTRATE
            .blockEntity("thruster", ThrusterBlockEntity::new)
            .validBlocks(KineticBlocks.THRUSTER)
            .register();

    public static void register() {
        // This just "wakes up" the class to ensure static fields are loaded
    }
}