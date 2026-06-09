/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.minecraft.resources.ResourceLocation
 */
package dev.qwxon.tracks.index;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;

public class TracksPartialModels {
    public static final PartialModel TRACKWORK_WHEELS = TracksPartialModels.tracksBlock("wheels");
    public static final PartialModel TRACKWORK_COGS = TracksPartialModels.tracksBlock("cogs");
    public static final PartialModel TRACKWORK_TRACK_LINK = TracksPartialModels.tracksBlock("track_link");
    public static final PartialModel TRACKWORK_TRACK_LINK_DOWN = TracksPartialModels.tracksBlock("track_link_down");
    public static final PartialModel TRACKWORK_WRAPPED_LINK = TracksPartialModels.tracksBlock("wrapped_link");

    private static PartialModel tracksBlock(String path) {
        return PartialModel.of((ResourceLocation)ResourceLocation.tryBuild((String)"tracks", (String)("block/" + path)));
    }

    public static void init() {
    }
}

