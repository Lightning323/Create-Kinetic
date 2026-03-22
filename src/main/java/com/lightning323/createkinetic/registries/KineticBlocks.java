package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.blocks.*;
import com.lightning323.createkinetic.blocks.ballastTank.BallastTankBlock;
import com.lightning323.createkinetic.blocks.helm.ShipHelmBlock;
import com.lightning323.createkinetic.blocks.sail.RetractableSailBlock;
import com.lightning323.createkinetic.blocks.sail.SailClothBlock;
import com.lightning323.createkinetic.blocks.sail.sailPulley.SailPulleyBlock;
import com.simibubi.create.*;
import com.simibubi.create.content.fluids.tank.*;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import java.util.List;

import static com.lightning323.createkinetic.CreateKinetic.REGISTRATE;
import static com.simibubi.create.api.behaviour.display.DisplaySource.displaySource;
import static com.simibubi.create.api.behaviour.movement.MovementBehaviour.movementBehaviour;
import static com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType.mountedFluidStorage;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class KineticBlocks {

    public static final BlockEntry<BallastTankBlock> FLUID_TANK =
            REGISTRATE.block("fluid_tank", BallastTankBlock::kRegular)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.noOcclusion()
                    .isRedstoneConductor((p1, p2, p3) -> true))
            .transform(pickaxeOnly())
            .blockstate(new FluidTankGenerator()::generate)
            .onRegister(CreateRegistrate.blockModel(() -> FluidTankModel::standard))
            .transform(displaySource(AllDisplaySources.BOILER))
            .transform(mountedFluidStorage(AllMountedStorageTypes.FLUID_TANK))
            .onRegister(movementBehaviour(new FluidTankMovementBehavior()))
            .addLayer(() -> RenderType::cutoutMipped)
            .item(FluidTankItem::new)
            .model(AssetLookup.customBlockItemModel("_", "block_single_window"))
            .build()
            .register();

    public static BlockEntry<ShipHelmBlock> registerShipHelm(String name, WoodType woodType) {
        return REGISTRATE
                .block(name, p -> new ShipHelmBlock(p, woodType)) // Manual constructor call
                .initialProperties(SharedProperties::wooden)
                .properties(p -> p.noOcclusion())
                .blockstate((c, p) ->
                        //Base model doesnt have the wheel since we add it in our renderer
                        p.horizontalBlock(c.get(), p.models().getExistingFile(p.modLoc("block/helm/" + name + "_base"))))
                .item()
                //Complete helm model for the item
                .model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/helm/" + name)))
                .build()
                .register();
    }

    public static final BlockEntry<ShipHelmBlock> OAK_SHIP_HELM = registerShipHelm("oak_ship_helm", WoodType.OAK);
    public static final BlockEntry<ShipHelmBlock> SPRUCE_SHIP_HELM = registerShipHelm("spruce_ship_helm", WoodType.SPRUCE);
    public static final BlockEntry<ShipHelmBlock> BIRCH_SHIP_HELM = registerShipHelm("birch_ship_helm", WoodType.BIRCH);
    public static final BlockEntry<ShipHelmBlock> JUNGLE_SHIP_HELM = registerShipHelm("jungle_ship_helm", WoodType.JUNGLE);
    public static final BlockEntry<ShipHelmBlock> ACACIA_SHIP_HELM = registerShipHelm("acacia_ship_helm", WoodType.ACACIA);
    public static final BlockEntry<ShipHelmBlock> DARK_OAK_SHIP_HELM = registerShipHelm("dark_oak_ship_helm", WoodType.DARK_OAK);
    public static final BlockEntry<ShipHelmBlock> WARPED_SHIP_HELM = registerShipHelm("warped_ship_helm", WoodType.WARPED);
    public static final BlockEntry<ShipHelmBlock> MANGROVE_SHIP_HELM = registerShipHelm("mangrove_ship_helm", WoodType.MANGROVE);
    public static final BlockEntry<ShipHelmBlock> BAMBOO_SHIP_HELM = registerShipHelm("bamboo_ship_helm", WoodType.BAMBOO);
    public static final BlockEntry<ShipHelmBlock> CRIMSON_SHIP_HELM = registerShipHelm("crimson_ship_helm", WoodType.CRIMSON);
    public static final BlockEntry<ShipHelmBlock> CHERRY_SHIP_HELM = registerShipHelm("cherry_ship_helm", WoodType.CHERRY);

    public static final BlockEntry<RetractableSailBlock> RETRACTABLE_SAIL = REGISTRATE.block("retractable_sail", RetractableSailBlock::new)
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

    public static final BlockEntry<RetractableSailBlock.WeightBlock> SAIL_WEIGHT =
            REGISTRATE.block("pulley_weight", RetractableSailBlock.WeightBlock::new)
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
                        .define('S', KineticBlocks.RETRACTABLE_SAIL.get()) // Using Create's Sail block
                        .define('P', AllBlocks.BRASS_CASING)
                        .define('A', AllItems.IRON_SHEET)
                        .unlockedBy("has_sail", prov.has(AllBlocks.SAIL.get()))
                        .save(prov);
            })
            .register();

    public static final BlockEntry<SailClothBlock> SAIL_CLOTH = REGISTRATE.block("sail_cloth", SailClothBlock::new)
            .properties(p -> p.sound(SoundType.WOOL)
                    .mapColor(MapColor.COLOR_BROWN))
            .tag(AllTags.AllBlockTags.BRITTLE.tag)
            .tag(BlockTags.CLIMBABLE)
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                    .partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X)
                    .modelForState()
                    .modelFile(p.models().getExistingFile(p.modLoc("block/" + c.getName())))
                    .addModel()

                    // For Axis Z (Rotate 90 degrees)
                    .partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.Z)
                    .modelForState()
                    .modelFile(p.models().getExistingFile(p.modLoc("block/" + c.getName())))
                    .rotationY(90) // This performs the 90-degree turn for the X axis
                    .addModel()
            )
            .register();

    public static final BlockEntry<SailPulleyBlock.MagnetBlock> SAIL_MAGNET =
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
                            .rotationY(yRot + 180)
                            .build();
                });
            })
            .item((block, props) -> new BlockItem(block, props) {
                @Override
                public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
                    KineticItems.shiftForTooltip(tooltip,
                            Component.translatable("tooltip.createkinetic.anchor").withStyle(ChatFormatting.GRAY));
                }
            })
            .model((ctx, prov) ->
                    prov.withExistingParent(ctx.getName(), prov.modLoc("block/anchor_off")))
            .build()
            .recipe((ctx, prov) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ctx.getEntry())
                        .pattern(" I ")
                        .pattern(" T ")
                        .pattern("IBI")
                        .define('T', AllItems.ELECTRON_TUBE)
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


    public static final BlockEntry<EnchantedBallastBlock> ENCHANTED_BALLAST = REGISTRATE.block("enchanted_ballast", EnchantedBallastBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.explosionResistance(0.0f).noOcclusion())
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                    prov.models().getExistingFile(prov.modLoc("block/enchanted_ballast")
                    )))
            .item((block, props) -> new BlockItem(block, props) {
                @Override
                public boolean isFoil(ItemStack stack) {
                    return true; // Keeping your glint logic
                }

                @Override
                public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
                    KineticItems.shiftForTooltip(tooltip,
                            Component.translatable("tooltip.createkinetic.enchantedballast").withStyle(ChatFormatting.GRAY));
                }
            })
            // THIS PART overrides the item model specifically
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), "block/cube_bottom_top")
                    .texture("side", prov.modLoc("block/ballast_side"))
                    .texture("bottom", prov.modLoc("block/ballast_top"))
                    .texture("top", prov.modLoc("block/ballast_top"))
            )
            .build()
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