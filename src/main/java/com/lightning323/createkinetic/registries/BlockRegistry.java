package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.blocks.BallastBlock;
import com.lightning323.createkinetic.blocks.BuoyBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.lightning323.createkinetic.Createkinetic.MOD_ID;
import static com.lightning323.createkinetic.registries.ItemRegistry.ITEMS;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    // FIX 1: Change to static and use a Supplier for the BlockItem to be safe
    public static RegistryObject<Block> registerBlockItem(String id, java.util.function.Supplier<Block> blockSupplier) {
        RegistryObject<Block> blockReg = BLOCKS.register(id, blockSupplier);
        // We pass the RegistryObject itself (which is a supplier) to the BlockItem
        ITEMS.register(id, () -> new BlockItem(blockReg.get(), new Item.Properties()));
        return blockReg;
    }

    private static final BlockBehaviour.Properties BUOY_BLOCK_PROPERTIES =
            BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f);

    //public static final ensures blocks are registered AS SOON as it is accessed by the main mod class

    public static final RegistryObject<Block> BALLAST_BLOCK = registerBlockItem("ballast_block",
            () -> new BallastBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).explosionResistance(0.0f)));

    public static final RegistryObject<Block> BUOY_BLOCK = registerBlockItem("buoy_block", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> WHITE_BUOY = registerBlockItem("white_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> LIGHT_GRAY_BUOY = registerBlockItem("light_gray_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> GRAY_BUOY = registerBlockItem("gray_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> BLACK_BUOY = registerBlockItem("black_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> BROWN_BUOY = registerBlockItem("brown_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> RED_BUOY = registerBlockItem("red_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> ORANGE_BUOY = registerBlockItem("orange_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> YELLOW_BUOY = registerBlockItem("yellow_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> LIME_BUOY = registerBlockItem("lime_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> GREEN_BUOY = registerBlockItem("green_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> CYAN_BUOY = registerBlockItem("cyan_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> LIGHT_BLUE_BUOY = registerBlockItem("light_blue_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> BLUE_BUOY = registerBlockItem("blue_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> PURPLE_BUOY = registerBlockItem("purple_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> MAGENTA_BUOY = registerBlockItem("magenta_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> PINK_BUOY = registerBlockItem("pink_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));


    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}