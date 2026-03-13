package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.blocks.AnchorBlock;
import com.lightning323.createkinetic.blocks.BallastBlock;
import com.lightning323.createkinetic.blocks.BuoyBlock;
import com.lightning323.createkinetic.blocks.sail.SailBlock;
import com.lightning323.createkinetic.blocks.sail.sailPulley.SailPulleyBlock;
import com.lightning323.createkinetic.items.ShipTotemItem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import static com.lightning323.createkinetic.Createkinetic.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class KineticItems {


    public static final BlockEntry<SailBlock> RETRACTABLE_SAIL = REGISTRATE.block("retractable_sail", SailBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .properties(p -> p.noOcclusion())
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .blockstate(BlockStateGen.horizontalAxisBlockProvider(true))
            .item()
            .transform(customItemModel())
            .recipe((ctx, prov) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ctx.getEntry())
                        .pattern(" P ")
                        .pattern("SSS")
                        .pattern("AAA")
                        .define('S', AllBlocks.SAIL.get()) // Using Create's Sail block
                        .define('P', AllBlocks.ANDESITE_CASING)
                        .define('A', AllItems.ANDESITE_ALLOY.get())
                        .unlockedBy("has_sail", prov.has(AllBlocks.SAIL.get()))
                        .save(prov);
            })
            .register();

    public static final BlockEntry<SailBlock.WeightBlock> PULLEY_SAIL_WEIGHT =
            REGISTRATE.block("pulley_weight", SailBlock.WeightBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .tag(AllTags.AllBlockTags.BRITTLE.tag)
                    .tag(BlockTags.CLIMBABLE)
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X)
                            .modelForState()
                            .modelFile(p.models().getExistingFile(p.modLoc("block/retractable_sail/" + c.getName())))
                            .addModel()

                            .partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.Z)
                            .modelForState()
                            .modelFile(p.models().getExistingFile(p.modLoc("block/retractable_sail/" + c.getName())))
                            .rotationY(90) // This performs the 90-degree turn for the X axis
                            .addModel()
                    )
                    .register();

    public static final BlockEntry<SailPulleyBlock> SAIL_PULLEY = REGISTRATE.block("sail_pulley", SailPulleyBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.PODZOL))
            .properties(p -> p.noOcclusion())
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .blockstate(BlockStateGen.horizontalAxisBlockProvider(true))
            .item()
            .transform(customItemModel())
            .recipe((ctx, prov) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ctx.getEntry())
                        .pattern("P")
                        .pattern("S")
                        .pattern("A")
                        .define('S', KineticItems.RETRACTABLE_SAIL.get()) // Using Create's Sail block
                        .define('P', AllBlocks.BRASS_CASING)
                        .define('A', AllItems.IRON_SHEET)
                        .unlockedBy("has_sail", prov.has(AllBlocks.SAIL.get()))
                        .save(prov);
            })
            .register();

    public static final BlockEntry<SailPulleyBlock.SailBlock> PULLEY_SAIL_CLOTH = REGISTRATE.block("rope", SailPulleyBlock.SailBlock::new)
            .properties(p -> p.sound(SoundType.WOOL)
                    .mapColor(MapColor.COLOR_BROWN))
            .tag(AllTags.AllBlockTags.BRITTLE.tag)
            .tag(BlockTags.CLIMBABLE)
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                    .partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X)
                    .modelForState()
                    .modelFile(p.models().getExistingFile(p.modLoc("block/sail_pulley/" + c.getName())))
                    .addModel()

                    // For Axis Z (Rotate 90 degrees)
                    .partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.Z)
                    .modelForState()
                    .modelFile(p.models().getExistingFile(p.modLoc("block/sail_pulley/" + c.getName())))
                    .rotationY(90) // This performs the 90-degree turn for the X axis
                    .addModel()
            )
            .register();

    public static final BlockEntry<SailPulleyBlock.MagnetBlock> PULLEY_SAIL_MAGNET =
            REGISTRATE.block("pulley_magnet", SailPulleyBlock.MagnetBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .tag(AllTags.AllBlockTags.BRITTLE.tag)
                    .tag(BlockTags.CLIMBABLE)
                    .blockstate((c, p) -> p.getVariantBuilder(c.get())
                            .partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X)
                            .modelForState()
                            .modelFile(p.models().getExistingFile(p.modLoc("block/sail_pulley/" + c.getName())))
                            .addModel()

                            .partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.Z)
                            .modelForState()
                            .modelFile(p.models().getExistingFile(p.modLoc("block/sail_pulley/" + c.getName())))
                            .rotationY(90) // This performs the 90-degree turn for the X axis
                            .addModel()
                    )
                    .register();

    public static final ItemEntry<ShipTotemItem> SHIP_TOTEM = REGISTRATE.item("ship_totem",
                    p -> new ShipTotemItem(false))
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .register();

    public static final ItemEntry<ShipTotemItem> FREEZE_SHIP_TOTEM = REGISTRATE.item("freeze_ship_totem",
                    p -> new ShipTotemItem(true))
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .register();


    //public static final ensures blocks are registered AS SOON as it is accessed by the main mod class

    public static final BlockEntry<AnchorBlock> ANCHOR = REGISTRATE.block("anchor", AnchorBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.explosionResistance(0.0f))
            .blockstate((ctx, prov) -> {
                // 1. Define the two base models
                var modelOn = prov.models().getExistingFile(prov.modLoc("block/anchor_on"));
                var modelOff = prov.models().getExistingFile(prov.modLoc("block/anchor_off"));

                // 2. Map every state to a model and rotation
                prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
                    boolean powered = state.getValue(BlockStateProperties.POWERED);
                    Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

                    // Calculate Y rotation based on direction
                    int yRot = (int) facing.toYRot();

                    return ConfiguredModel.builder()
                            .modelFile(powered ? modelOn : modelOff)
                            .rotationY(yRot)
                            .build();
                });
            })
            // Replace .simpleItem() with this:
            .item()
            .model((ctx, prov) ->
                    prov.withExistingParent(ctx.getName(), prov.modLoc("block/anchor_off")))
            .build()
            .recipe((ctx, prov) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ctx.getEntry())
                        .pattern(" I ")
                        .pattern("ICI")
                        .pattern("IBI")
                        .define('C', Items.CHAIN)
                        .define('I', Items.IRON_INGOT)
                        .define('B', Items.IRON_BLOCK)
                        .unlockedBy("has_chain", prov.has(Items.CHAIN))
                        .save(prov);
            })
            .register();

    public static final BlockEntry<BallastBlock> BALLAST_BLOCK = REGISTRATE.block("ballast_block", BallastBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK) // Copy properties from here
            .properties(p -> p.explosionResistance(0.0f)) // Modify specific properties
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                    prov.models().cubeBottomTop(ctx.getName(),
                            prov.modLoc("block/ballast_side"),   // Side texture
                            prov.modLoc("block/ballast_top"), // Bottom texture
                            prov.modLoc("block/ballast_top")     // Top texture
                    )))
            .simpleItem() // Automatically registers the BlockItem for you
            .recipe((ctx, prov) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.getEntry())
                        .pattern(" I ")
                        .pattern("ISI")
                        .pattern(" I ")
                        .define('I', AllBlocks.INDUSTRIAL_IRON_BLOCK)
                        .define('S', Items.WATER_BUCKET)
                        .unlockedBy("has_iron", prov.has(AllBlocks.INDUSTRIAL_IRON_BLOCK))
                        // Use prov.getConsumer() to save the recipe
                        .save(prov);
            })
            .register();


    private static BlockEntry<BuoyBlock> registerBuoy(String name, String langName) {

        final Item woolType;

        switch (name) {
            case "orange_buoy":
                woolType = Items.ORANGE_WOOL;
                break;
            case "magenta_buoy":
                woolType = Items.MAGENTA_WOOL;
                break;
            case "light_blue_buoy":
                woolType = Items.LIGHT_BLUE_WOOL;
                break;
            case "yellow_buoy":
                woolType = Items.YELLOW_WOOL;
                break;
            case "lime_buoy":
                woolType = Items.LIME_WOOL;
                break;
            case "pink_buoy":
                woolType = Items.PINK_WOOL;
                break;
            case "gray_buoy":
                woolType = Items.GRAY_WOOL;
                break;
            case "light_gray_buoy":
                woolType = Items.LIGHT_GRAY_WOOL;
                break;
            case "cyan_buoy":
                woolType = Items.CYAN_WOOL;
                break;
            case "purple_buoy":
                woolType = Items.PURPLE_WOOL;
                break;
            case "blue_buoy":
                woolType = Items.BLUE_WOOL;
                break;
            case "brown_buoy":
                woolType = Items.BROWN_WOOL;
                break;
            case "green_buoy":
                woolType = Items.GREEN_WOOL;
                break;
            case "red_buoy":
                woolType = Items.RED_WOOL;
                break;
            case "black_buoy":
                woolType = Items.BLACK_WOOL;
                break;
            default:
                woolType = Items.WHITE_WOOL;
                break;
        }

        return REGISTRATE.block(name, BuoyBlock::new)
                .initialProperties(() -> Blocks.WHITE_WOOL)
                .properties(p -> p.explosionResistance(0.0f))
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                        prov.models().cubeAll(ctx.getName(), prov.modLoc("block/buoy/" + name)
                        )))
                .lang(langName)
                .simpleItem()
                .recipe((ctx, prov) -> {
                    ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ctx.getEntry(), 4)
                            .pattern(" W ")
                            .pattern("WBW")
                            .pattern(" W ")
                            .define('W', woolType)
                            .define('B', Items.BARREL)
                            .unlockedBy("has_water", prov.has(Items.WATER_BUCKET))
                            .save(prov);
                })
                .register();
    }

    //    public static final BlockEntry<BuoyBlock> BUOY_BLOCK = registerBuoy("buoy", "Buoy");
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