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
package org.lightning323.createkinetic.registry;

import com.simibubi.create.AllTags;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.ModelGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import org.lightning323.createkinetic.config.Config;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.KineticRegistrate;
import org.lightning323.createkinetic.content.blocks.sable_track.SableTrackBlock;
import org.lightning323.createkinetic.content.blocks.sable_track.SableTrackRole;
import org.lightning323.createkinetic.content.blocks.gyroscope.GyroscopeBlock;
import org.lightning323.createkinetic.content.items.TrackMountBlockItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.MapColor;
import org.lightning323.createkinetic.content.blocks.joystick.JoystickBlock;

public class KineticBlocks {
    private static final KineticRegistrate REGISTRATE = CreateKinetic.getRegistrate();

    public static final BlockEntry<GyroscopeBlock> GYROSCOPE = REGISTRATE
            .block("gyroscope", GyroscopeBlock::new).initialProperties(SharedProperties::softMetal)
            .properties((p) -> p.noOcclusion())
            .onRegister((block) -> BlockStressValues.IMPACTS.register(block, Config::gyroscopeStressImpact))
            .simpleItem().register();

    public static final BlockEntry<JoystickBlock> JOYSTICK = REGISTRATE
            .block("joystick", JoystickBlock::new).initialProperties(SharedProperties::wooden)
            .properties((p) -> p.noOcclusion())
            .simpleItem().register();

    public static final BlockEntry<SableTrackBlock> TRACK_MOUNT = REGISTRATE.block("track_mount",
                    properties -> new SableTrackBlock(properties, SableTrackRole.MOUNT))
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY).noOcclusion().isRedstoneConductor((state, level, pos) -> false))
            .transform(TagGen.axeOrPickaxe())
            .addLayer(() -> RenderType::cutoutMipped)
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .item(TrackMountBlockItem::new)
            .transform(ModelGen.customItemModel())
            .register();

    public static void init() {
    }
}

