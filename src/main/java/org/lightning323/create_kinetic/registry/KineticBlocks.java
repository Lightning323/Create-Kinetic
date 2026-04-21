package org.lightning323.create_kinetic.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.create_kinetic.propulsion.ThrusterBlock;

import static org.lightning323.create_kinetic.CreateKinetic.MODID;

public class KineticBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    
    public static final DeferredHolder<Block, ThrusterBlock> THRUSTER = BLOCKS.register(
            "thruster",
            () -> new ThrusterBlock(BlockBehaviour.Properties.of()
                                            .noOcclusion()
                                            .strength(0.2f)
                                            .requiresCorrectToolForDrops()
                                            .sound(SoundType.COPPER))
    );
    public static void registerBlockItems(DeferredRegister.Items itemRegistry) {
        BLOCKS.getEntries().forEach(block -> {
            if (block.getId().getPath().equals("wretched_swine")) {
                return;
            }
            itemRegistry.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
        });
    }
    

}