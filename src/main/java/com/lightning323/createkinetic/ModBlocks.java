package com.lightning323.createkinetic;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.lightning323.createkinetic.Createkinetic.MODID;
import static com.lightning323.createkinetic.ModItems.ITEMS;

public class ModBlocks {
    public static RegistryObject<Block> registerBlockItem(String id, Block block) {
        RegistryObject<Block> blockReg = BLOCKS.register(id, () -> block);
        ITEMS.register(id, () -> new BlockItem(blockReg.get(), new Item.Properties()));
        return blockReg;
    }

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
//
//    static {
//        registerBlockItem("example_block", new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
//    }


}
