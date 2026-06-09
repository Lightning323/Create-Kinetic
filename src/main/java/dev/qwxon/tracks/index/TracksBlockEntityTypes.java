/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.BlockEntityEntry
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  dev.simulated_team.simulated.registrate.SimulatedRegistrate
 */
package dev.qwxon.tracks.index;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.qwxon.tracks.content.blocks.sable_track.SableTrackBlockEntity;
import dev.qwxon.tracks.content.blocks.sable_track.SableTrackRenderer;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import org.lightning323.createkinetic.CreateKinetic;

public class TracksBlockEntityTypes {
    private static final SimulatedRegistrate REGISTRATE = CreateKinetic.getRegistrate();
    public static final BlockEntityEntry<SableTrackBlockEntity> SABLE_TRACK = REGISTRATE.blockEntity("sable_track", SableTrackBlockEntity::new)
            .validBlocks(new NonNullSupplier[]{TracksBlocks.TRACK_MOUNT})
            .renderer(() -> (BlockEntityRendererProvider<SableTrackBlockEntity>) SableTrackRenderer::new)
            .register();

    public static void init() {
    }
}

