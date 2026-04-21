package org.lightning323.createkinetic.registries;

import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.SoundType;
import org.lightning323.createkinetic.blocks.thruster.ThrusterBlock;

import static org.lightning323.createkinetic.CreateKinetic.REGISTRATE;

public class KineticBlocks {

    //We CANNOT register items with registrate, we just need to use deffered register instead.

    public static final BlockEntry<ThrusterBlock> THRUSTER = REGISTRATE
            .block("thruster", ThrusterBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p
                    .noOcclusion()
                    .strength(0.2f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.COPPER))
            .blockstate((c, p) -> p.directionalBlock(c.get(), p.models()
                    .withExistingParent(c.getName(), p.modLoc("block/thruster/thruster"))))
            .register();

    public static void register() {
    }

}