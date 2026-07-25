package org.lightning323.createkinetic.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.content.heat.burners.liquid.LiquidBurnerBlock;
import org.lightning323.createkinetic.content.heat.burners.solid.SolidBurnerBlock;
import org.lightning323.createkinetic.content.heat.engine.StirlingEngineBlock;
import org.lightning323.createkinetic.content.platinum.PlatinumFluidTankBlock;
import org.lightning323.createkinetic.content.platinum.PlatinumFluidTankItem;
import org.lightning323.createkinetic.content.platinum.PlatinumFluidVesselBlock;
import org.lightning323.createkinetic.content.platinum.PlatinumFluidVesselItem;
import org.lightning323.createkinetic.content.redstone_converter.RedstoneConverterBlock;
import org.lightning323.createkinetic.content.thruster.ion_thruster.IonThrusterBlock;
import org.lightning323.createkinetic.content.thruster.thruster.ThrusterBlock;
import org.lightning323.createkinetic.content.thruster.thruster.creative_thruster.CreativeThrusterBlock;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorThrusterBlock;
import org.lightning323.createkinetic.content.thruster.vector_thruster.creative_vector_thruster.CreativeVectorThrusterBlock;
import org.lightning323.createkinetic.content.thruster.vector_thruster.liquid_vector_thruster.LiquidVectorThrusterBlock;
import org.lightning323.createkinetic.content.wing.CopycatWingBlock;
import org.lightning323.createkinetic.content.wing.CopycatWingItem;
import org.lightning323.createkinetic.content.wing.WingBlock;

public class PropulsionBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateKinetic.ID);
    public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(CreateKinetic.ID);

    public static final DeferredBlock<ThrusterBlock> THRUSTER_BLOCK = BLOCKS.register("thruster",
        () -> new ThrusterBlock(Block.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops()
            .sound(SoundType.METAL).strength(5.5f, 4.0f).noOcclusion()));
    public static final DeferredBlock<CreativeThrusterBlock> CREATIVE_THRUSTER_BLOCK = BLOCKS.register("creative_thruster",
        () -> new CreativeThrusterBlock(Block.Properties.of().mapColor(MapColor.METAL)
            .sound(SoundType.METAL).strength(5.5f, 4.0f).noOcclusion()));
    public static final DeferredBlock<IonThrusterBlock> ION_THRUSTER_BLOCK = BLOCKS.register("ion_thruster",
        () -> new IonThrusterBlock(Block.Properties.of().mapColor(MapColor.METAL)
            .sound(SoundType.METAL).strength(5.5f, 4.0f).noOcclusion()));
    public static final DeferredBlock<VectorThrusterBlock> VECTOR_THRUSTER_BLOCK = BLOCKS.register("vector_thruster",
        () -> new VectorThrusterBlock(Block.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops()
            .sound(SoundType.METAL).strength(5.5f, 4.0f).noOcclusion()));
    public static final DeferredBlock<LiquidVectorThrusterBlock> LIQUID_VECTOR_THRUSTER_BLOCK = BLOCKS.register("liquid_vector_thruster",
        () -> new LiquidVectorThrusterBlock(Block.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops()
            .sound(SoundType.METAL).strength(5.5f, 4.0f).noOcclusion()));
    public static final DeferredBlock<CreativeVectorThrusterBlock> CREATIVE_VECTOR_THRUSTER_BLOCK = BLOCKS.register("creative_vector_thruster",
        () -> new CreativeVectorThrusterBlock(Block.Properties.of().mapColor(MapColor.METAL)
            .sound(SoundType.METAL).strength(5.5f, 4.0f).noOcclusion()));
    public static final DeferredBlock<RedstoneConverterBlock> REDSTONE_CONVERTER_BLOCK = BLOCKS.register("redstone_converter",
        () -> new RedstoneConverterBlock(Block.Properties.of().mapColor(MapColor.METAL)
            .sound(SoundType.METAL).instabreak()));

    public static final DeferredBlock<SolidBurnerBlock> SOLID_BURNER = BLOCKS.register("solid_burner",
        () -> new SolidBurnerBlock(Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.COPPER)
            .requiresCorrectToolForDrops().strength(2.5f, 2.0f).lightLevel(s -> s.getValue(SolidBurnerBlock.LIT) ? 13 : 0)));
    public static final DeferredBlock<LiquidBurnerBlock> LIQUID_BURNER = BLOCKS.register("liquid_burner",
        () -> new LiquidBurnerBlock(Block.Properties.of().noOcclusion().mapColor(MapColor.STONE).sound(SoundType.COPPER)
            .requiresCorrectToolForDrops().strength(2.75f, 2.0f)));
    public static final DeferredBlock<StirlingEngineBlock> STIRLING_ENGINE_BLOCK = BLOCKS.register("stirling_engine",
        () -> new StirlingEngineBlock(Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.COPPER)
            .requiresCorrectToolForDrops().strength(2.5f, 2.0f).noOcclusion()));

    public static final DeferredBlock<WingBlock> WING_BLOCK = BLOCKS.register("wing",
        () -> new WingBlock(Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.COPPER)
            .strength(1.5f, 2.0f).noOcclusion()));
    public static final DeferredBlock<WingBlock> TEMPERED_WING_BLOCK = BLOCKS.register("tempered_wing",
        () -> new WingBlock(Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.COPPER)
            .strength(1.5f, 2.0f).noOcclusion()));
    public static final DeferredBlock<CopycatWingBlock> COPYCAT_WING = BLOCKS.register("copycat_wing",
        () -> new CopycatWingBlock(Block.Properties.of().strength(1.5f, 2.0f), 4));
    public static final DeferredBlock<CopycatWingBlock> COPYCAT_WING_8 = BLOCKS.register("copycat_wing_8",
        () -> new CopycatWingBlock(Block.Properties.of().strength(1.5f, 2.0f), 8));
    public static final DeferredBlock<CopycatWingBlock> COPYCAT_WING_12 = BLOCKS.register("copycat_wing_12",
        () -> new CopycatWingBlock(Block.Properties.of().strength(1.5f, 2.0f), 12));


    public static final DeferredBlock<PlatinumFluidTankBlock> PLATINUM_FLUID_TANK = BLOCKS.register("platinum_fluid_tank",
        () -> new PlatinumFluidTankBlock(Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER)
            .requiresCorrectToolForDrops().strength(2.5f, 2.0f).noOcclusion().isRedstoneConductor((s, l, p) -> true)));
    public static final DeferredBlock<PlatinumFluidVesselBlock> PLATINUM_FLUID_VESSEL = BLOCKS.register("platinum_fluid_vessel",
        () -> new PlatinumFluidVesselBlock(Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER)
            .requiresCorrectToolForDrops().strength(2.5f, 2.0f).noOcclusion().isRedstoneConductor((s, l, p) -> true)));



    static {
        registerDefaultBlockItem("thruster", THRUSTER_BLOCK);
        registerBlockItem("creative_thruster", CREATIVE_THRUSTER_BLOCK, new BlockItem.Properties().rarity(Rarity.EPIC));
        registerBlockItem("ion_thruster", ION_THRUSTER_BLOCK, new BlockItem.Properties().rarity(Rarity.UNCOMMON));
        registerBlockItem("vector_thruster", VECTOR_THRUSTER_BLOCK, new BlockItem.Properties().rarity(Rarity.UNCOMMON));
        registerBlockItem("liquid_vector_thruster", LIQUID_VECTOR_THRUSTER_BLOCK, new BlockItem.Properties().rarity(Rarity.UNCOMMON));
        registerBlockItem("creative_vector_thruster", CREATIVE_VECTOR_THRUSTER_BLOCK, new BlockItem.Properties().rarity(Rarity.EPIC));
        registerDefaultBlockItem("redstone_converter", REDSTONE_CONVERTER_BLOCK);

        registerDefaultBlockItem("solid_burner", SOLID_BURNER);
        registerDefaultBlockItem("liquid_burner", LIQUID_BURNER);
        registerDefaultBlockItem("stirling_engine", STIRLING_ENGINE_BLOCK);
        registerDefaultBlockItem("wing", WING_BLOCK);
        registerDefaultBlockItem("tempered_wing", TEMPERED_WING_BLOCK);
        BLOCK_ITEMS.register("copycat_wing", () -> new CopycatWingItem(COPYCAT_WING.get(), new BlockItem.Properties()));
        BLOCK_ITEMS.register("copycat_wing_8", () -> new CopycatWingItem(COPYCAT_WING_8.get(), new BlockItem.Properties()));
        BLOCK_ITEMS.register("copycat_wing_12", () -> new CopycatWingItem(COPYCAT_WING_12.get(), new BlockItem.Properties()));

        BLOCK_ITEMS.register("platinum_fluid_tank", () -> new PlatinumFluidTankItem(PLATINUM_FLUID_TANK.get(), new BlockItem.Properties()));
        BLOCK_ITEMS.register("platinum_fluid_vessel", () -> new PlatinumFluidVesselItem(PLATINUM_FLUID_VESSEL.get(), new BlockItem.Properties()));


        PropulsionDefaultStress.setImpact(ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "redstone_transmission"), 0, false);
        PropulsionDefaultStress.setImpact(ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "tilt_adapter"), 0, false);
        PropulsionDefaultStress.setImpact(ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "advanced_tilt_adapter"), 0, false);
    }

    private static <T extends Block> void registerDefaultBlockItem(String name, DeferredBlock<T> block) {
        registerBlockItem(name, block, new BlockItem.Properties());
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block, BlockItem.Properties properties) {
        BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), properties));
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        BLOCK_ITEMS.register(modBus);
    }
}
