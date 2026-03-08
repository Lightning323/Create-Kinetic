package com.lightning323.createkinetic.datagen;

import net.minecraft.core.BlockPos;
import net.minecraft.data.PackOutput;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import static com.lightning323.createkinetic.Createkinetic.MOD_ID;
import static com.lightning323.createkinetic.registries.BlockRegistry.BLOCKS;
import static com.lightning323.createkinetic.registries.ItemRegistry.ITEMS;

public class ModBlockStateProvider extends BlockStateProvider {
    public static RegistryObject<Block> registerBlockItem(String id, java.util.function.Supplier<Block> blockSupplier) {
        RegistryObject<Block> blockReg = BLOCKS.register(id, blockSupplier);
        // We pass the RegistryObject itself (which is a supplier) to the BlockItem
        ITEMS.register(id, () -> new BlockItem(blockReg.get(), new Item.Properties()));
        return blockReg;
    }

    public ModBlockStateProvider() {
        super();
    }


    @Override
    protected BlockStateProviderType<?> type() {
        return null;
    }

    @Override
    public BlockState getState(RandomSource randomSource, BlockPos blockPos) {
        return null;
    }
}