/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.simibubi.create.AllTags$AllBlockTags
 *  com.simibubi.create.foundation.data.BlockStateGen
 *  com.simibubi.create.foundation.data.ModelGen
 *  com.simibubi.create.foundation.data.SharedProperties
 *  com.simibubi.create.foundation.data.TagGen
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  dev.simulated_team.simulated.registrate.SimulatedRegistrate
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.material.MapColor
 */
package org.lightning323.createkinetic.registries;

import com.simibubi.create.AllTags;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.KineticRegistrate;
import org.lightning323.createkinetic.config.KineticConfig;
import org.lightning323.createkinetic.content.blocks.sable_track.SableTrackBlock;
import org.lightning323.createkinetic.content.blocks.sable_track.SableTrackRole;
import org.lightning323.createkinetic.content.blocks.reaction_wheel.ReactionWheelBlock;
import org.lightning323.createkinetic.content.items.TrackMountBlockItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.MapColor;
import org.lightning323.createkinetic.content.blocks.joystick.JoystickBlock;
import org.lightning323.createkinetic.content.thruster.ion_thruster.IonThrusterBlock;
import org.lightning323.createkinetic.content.thruster.thruster.ThrusterBlock;
import org.lightning323.createkinetic.content.thruster.thruster.creative_thruster.CreativeThrusterBlock;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorThrusterBlock;
import org.lightning323.createkinetic.content.thruster.vector_thruster.creative_vector_thruster.CreativeVectorThrusterBlock;
import org.lightning323.createkinetic.content.thruster.vector_thruster.liquid_vector_thruster.LiquidVectorThrusterBlock;
import org.lightning323.createkinetic.content.wing.CopycatWingBlock;
import org.lightning323.createkinetic.content.wing.CopycatWingItem;
import org.lightning323.createkinetic.content.wing.WingBlock;

public class KineticBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateKinetic.ID);
    public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(CreateKinetic.ID);
    private static final KineticRegistrate REGISTRATE = CreateKinetic.getRegistrate();




    public static final DeferredBlock<CopycatWingBlock> COPYCAT_WING_12 = BLOCKS.register("copycat_wing_12",
        () -> new CopycatWingBlock(Block.Properties.of().strength(1.5f, 2.0f), 12));
    public static final DeferredBlock<CopycatWingBlock> COPYCAT_WING_8 = BLOCKS.register("copycat_wing_8",
        () -> new CopycatWingBlock(Block.Properties.of().strength(1.5f, 2.0f), 8));
    public static final DeferredBlock<CopycatWingBlock> COPYCAT_WING = BLOCKS.register("copycat_wing",
        () -> new CopycatWingBlock(Block.Properties.of().strength(1.5f, 2.0f), 4));
    public static final DeferredBlock<WingBlock> WING_BLOCK = BLOCKS.register("wing",
        () -> new WingBlock(Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.COPPER)
            .strength(1.5f, 2.0f).noOcclusion()));

    public static final DeferredBlock<CreativeVectorThrusterBlock> CREATIVE_VECTOR_THRUSTER_BLOCK = BLOCKS.register("creative_vector_thruster",
        () -> new CreativeVectorThrusterBlock(Block.Properties.of().mapColor(MapColor.METAL)
            .sound(SoundType.METAL).strength(5.5f, 4.0f).noOcclusion()));
    public static final DeferredBlock<LiquidVectorThrusterBlock> LIQUID_VECTOR_THRUSTER_BLOCK = BLOCKS.register("liquid_vector_thruster",
        () -> new LiquidVectorThrusterBlock(Block.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops()
            .sound(SoundType.METAL).strength(5.5f, 4.0f).noOcclusion()));
    public static final DeferredBlock<VectorThrusterBlock> VECTOR_THRUSTER_BLOCK = BLOCKS.register("vector_thruster",
        () -> new VectorThrusterBlock(Block.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops()
            .sound(SoundType.METAL).strength(5.5f, 4.0f).noOcclusion()));
    public static final DeferredBlock<IonThrusterBlock> ION_THRUSTER_BLOCK = BLOCKS.register("ion_thruster",
        () -> new IonThrusterBlock(Block.Properties.of().mapColor(MapColor.METAL)
            .sound(SoundType.METAL).strength(5.5f, 4.0f).noOcclusion()));
    public static final DeferredBlock<CreativeThrusterBlock> CREATIVE_THRUSTER_BLOCK = BLOCKS.register("creative_thruster",
        () -> new CreativeThrusterBlock(Block.Properties.of().mapColor(MapColor.METAL)
            .sound(SoundType.METAL).strength(5.5f, 4.0f).noOcclusion()));
    public static final DeferredBlock<ThrusterBlock> THRUSTER_BLOCK = BLOCKS.register("thruster",
        () -> new ThrusterBlock(Block.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops()
            .sound(SoundType.METAL).strength(5.5f, 4.0f).noOcclusion()));

    public static final BlockEntry<ReactionWheelBlock> REACTION_WHEEL = REGISTRATE
            .block("reaction_wheel", ReactionWheelBlock::new).initialProperties(SharedProperties::softMetal)
            .properties((p) -> p.noOcclusion())
            .transform(TagGen.axeOrPickaxe())
            .onRegister((block) -> BlockStressValues.IMPACTS.register(block, KineticConfig::reactionWheelStressImpact))
            .simpleItem().register();

    public static final BlockEntry<JoystickBlock> JOYSTICK = REGISTRATE
            .block("joystick", JoystickBlock::new).initialProperties(SharedProperties::wooden)
            .properties((p) -> p.noOcclusion())
            .transform(TagGen.axeOrPickaxe())
            .simpleItem().register();

    public static final BlockEntry<SableTrackBlock> TRACK_MOUNT = REGISTRATE.block("track_mount",
                    properties -> new SableTrackBlock(properties, SableTrackRole.MOUNT))
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY).noOcclusion().isRedstoneConductor((state, level, pos) -> false))
            .transform(TagGen.axeOrPickaxe())
            .addLayer(() -> RenderType::cutoutMipped)
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            //We have to change this registration to tell registrate to not generate blockstate or item models for us (We already have them)
//            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .blockstate((ctx, prov) -> {})
            .item(TrackMountBlockItem::new)
            .model((ctx, prov) -> {})
//            .transform(ModelGen.customItemModel())
            .build()
            .register();


    static {
        registerDefaultBlockItem("thruster", KineticBlocks.THRUSTER_BLOCK);
        registerBlockItem("creative_thruster", KineticBlocks.CREATIVE_THRUSTER_BLOCK, new BlockItem.Properties().rarity(Rarity.EPIC));
        registerBlockItem("ion_thruster", KineticBlocks.ION_THRUSTER_BLOCK, new BlockItem.Properties().rarity(Rarity.UNCOMMON));
        registerBlockItem("vector_thruster", KineticBlocks.VECTOR_THRUSTER_BLOCK, new BlockItem.Properties().rarity(Rarity.UNCOMMON));
        registerBlockItem("liquid_vector_thruster", KineticBlocks.LIQUID_VECTOR_THRUSTER_BLOCK, new BlockItem.Properties().rarity(Rarity.UNCOMMON));
        registerBlockItem("creative_vector_thruster", KineticBlocks.CREATIVE_VECTOR_THRUSTER_BLOCK, new BlockItem.Properties().rarity(Rarity.EPIC));
        registerDefaultBlockItem("wing", KineticBlocks.WING_BLOCK);
        KineticBlocks.BLOCK_ITEMS.register("copycat_wing", () -> new CopycatWingItem(KineticBlocks.COPYCAT_WING.get(), new BlockItem.Properties()));
        KineticBlocks.BLOCK_ITEMS.register("copycat_wing_8", () -> new CopycatWingItem(KineticBlocks.COPYCAT_WING_8.get(), new BlockItem.Properties()));
        KineticBlocks.BLOCK_ITEMS.register("copycat_wing_12", () -> new CopycatWingItem(KineticBlocks.COPYCAT_WING_12.get(), new BlockItem.Properties()));

        PropulsionDefaultStress.setImpact(ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "redstone_transmission"), 0, false);
        PropulsionDefaultStress.setImpact(ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "tilt_adapter"), 0, false);
        PropulsionDefaultStress.setImpact(ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "advanced_tilt_adapter"), 0, false);
    }

    private static <T extends Block> void registerDefaultBlockItem(String name, DeferredBlock<T> block) {
        registerBlockItem(name, block, new BlockItem.Properties());
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block, BlockItem.Properties properties) {
        KineticBlocks.BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), properties));
    }

    public static void register(IEventBus modBus) {
        KineticBlocks.BLOCKS.register(modBus);
        KineticBlocks.BLOCK_ITEMS.register(modBus);
    }
}

