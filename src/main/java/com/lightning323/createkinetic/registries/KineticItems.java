package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.blocks.BallastBlock;
import com.lightning323.createkinetic.blocks.BuoyBlock;
import com.lightning323.createkinetic.items.ShipTotemItem;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.pulley.PulleyBlock;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;

import static com.lightning323.createkinetic.Createkinetic.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class KineticItems {


    public static final BlockEntry<PulleyBlock> ROPE_PULLEY = REGISTRATE.block("rope_pulley", PulleyBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .properties(p -> p.noOcclusion())
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .blockstate(BlockStateGen.horizontalAxisBlockProvider(true))
            .item()
            .transform(customItemModel())
            .register();

    public static final ItemEntry<ShipTotemItem> SHIP_TOTEM = REGISTRATE.item("ship_totem",
                    p -> new ShipTotemItem(false))
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .register();

    public static final ItemEntry<ShipTotemItem> FREEZE_SHIP_TOTEM = REGISTRATE.item("freeze_ship_totem",
                    p -> new ShipTotemItem(true))
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .register();

    private static final BlockBehaviour.Properties BUOY_BLOCK_PROPERTIES =
            BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f);

    //public static final ensures blocks are registered AS SOON as it is accessed by the main mod class

    public static final BlockEntry<BallastBlock> BALLAST_BLOCK = REGISTRATE.block("ballast_block", BallastBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK) // Copy properties from here
            .properties(p -> p.explosionResistance(0.0f)) // Modify specific properties
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                    prov.models().cubeAll(ctx.getName(), prov.modLoc("block/ballast_block"))))
            .simpleItem() // Automatically registers the BlockItem for you
            .register();

    private static BlockEntry<BuoyBlock> registerBuoy(String name, String langName) {
        return REGISTRATE.block(name, BuoyBlock::new)
                .initialProperties(() -> Blocks.WHITE_WOOL)
                .properties(p -> p.explosionResistance(0.0f))
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                        prov.models().cubeBottomTop(ctx.getName(),
                                prov.modLoc("block/buoys/" + name),   // Side texture
                                prov.modLoc("block/buoys/" + name + "_top"), // Bottom texture
                                prov.modLoc("block/buoys/" + name + "_top")     // Top texture
                        )))
                .lang(langName)
                .simpleItem()
                .register();
    }

    public static final BlockEntry<BuoyBlock> BUOY_BLOCK = registerBuoy("buoy", "Buoy");
    public static final BlockEntry<BuoyBlock> WHITE_BUOY = registerBuoy("white_buoy", "White Buoy");
    public static final BlockEntry<BuoyBlock> LIGHT_GRAY_BUOY = registerBuoy("light_gray_buoy", "Light Gray Buoy");
    public static final BlockEntry<BuoyBlock> GRAY_BUOY = registerBuoy("gray_buoy", "Gray Buoy");
    public static final BlockEntry<BuoyBlock> BLACK_BUOY = registerBuoy("black_buoy", "Black Buoy");
    public static final BlockEntry<BuoyBlock> BROWN_BUOY = registerBuoy("brown_buoy", "Brown Buoy");
    public static final BlockEntry<BuoyBlock> RED_BUOY = registerBuoy("red_buoy", "Red Buoy");
    public static final BlockEntry<BuoyBlock> ORANGE_BUOY = registerBuoy("orange_buoy", "Orange Buoy");
    public static final BlockEntry<BuoyBlock> YELLOW_BUOY = registerBuoy("yellow_buoy", "Yellow Buoy");
    public static final BlockEntry<BuoyBlock> LIME_BUOY = registerBuoy("lime_buoy", "Lime Buoy");
    public static final BlockEntry<BuoyBlock> GREEN_BUOY = registerBuoy("green_buoy", "Green Buoy");
    public static final BlockEntry<BuoyBlock> CYAN_BUOY = registerBuoy("cyan_buoy", "Cyan Buoy");
    public static final BlockEntry<BuoyBlock> LIGHT_BLUE_BUOY = registerBuoy("light_blue_buoy", "Light Blue Buoy");
    public static final BlockEntry<BuoyBlock> BLUE_BUOY = registerBuoy("blue_buoy", "Blue Buoy");
    public static final BlockEntry<BuoyBlock> PURPLE_BUOY = registerBuoy("purple_buoy", "Purple Buoy");
    public static final BlockEntry<BuoyBlock> MAGENTA_BUOY = registerBuoy("magenta_buoy", "Magenta Buoy");
    public static final BlockEntry<BuoyBlock> PINK_BUOY = registerBuoy("pink_buoy", "Pink Buoy");


    public static void register() {
    }
}

/*
The old fashoned way to do it
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final RegistryObject<Block> BUOY_BLOCK = registerBlockItem("buoy_block", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> WHITE_BUOY = registerBlockItem("white_buoy", () -> new BuoyBlock(BUOY_BLOCK_PROPERTIES));

    public static RegistryObject<Block> registerBlockItem(String id, java.util.function.Supplier<Block> blockSupplier) {
        RegistryObject<Block> blockReg = BLOCKS.register(id, blockSupplier);
        // We pass the RegistryObject itself (which is a supplier) to the BlockItem
        ITEMS.register(id, () -> new BlockItem(blockReg.get(), new Item.Properties()));
        return blockReg;
    }
* */