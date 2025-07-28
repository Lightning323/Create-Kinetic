package org.zipcoder.createkinetic;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static org.zipcoder.createkinetic.Createkinetic.MODID;
import static org.zipcoder.createkinetic.ModItems.ITEMS;

public class ModBlocks {
    public static RegistryObject<Block> registerBlockItem(String id, Block block) {
        RegistryObject<Block> blockReg = BLOCKS.register(id, () -> block);
        ITEMS.register(id, () -> new BlockItem(blockReg.get(), new Item.Properties()));
        return blockReg;
    }

    //Ender chests
    public static Block IRON_CHEST, COPPER_CHEST, GOLD_CHEST, LAPIS_CHEST, REDSTONE_CHEST, DIAMOND_CHEST, EMERALD_CHEST, NETHERITE_CHEST;

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
//
//    static {
//        registerBlockItem("example_block", new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
//    }


}
